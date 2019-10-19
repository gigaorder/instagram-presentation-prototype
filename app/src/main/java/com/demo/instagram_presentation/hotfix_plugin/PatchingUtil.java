package com.demo.instagram_presentation.hotfix_plugin;

import android.content.SharedPreferences;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PatchingUtil {
    private static final String TAG = MainActivity.DEVICE_ID;
    private static final int MAX_DOWNLOAD_RETRY = 12;
    public static final int MAX_UPDATE_RETRY = 12;
    public static int updateCounter = 0;

    public static void loadLibrary() {
        TinkerLoadLibrary.installNavitveLibraryABI(InstagramApplicationContext.context, "armeabi");
        System.loadLibrary("stlport_shared");
    }

    public static void cleanPatch() {
        Tinker.with(InstagramApplicationContext.context).cleanPatch();
    }

    public static void killProcess() {
        ShareTinkerInternals.killAllOtherProcess(InstagramApplicationContext.context);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void checkForUpdate(String domain) {
        Bugfender.d(TAG, "Check for update");
        initUrl(domain);

        Tinker tinker = Tinker.with(InstagramApplicationContext.context);
        TinkerLoadResult tinkerLoadResult = tinker.getTinkerLoadResultIfPresent();

        String patchMd5 = getMD5Code(AppPreferencesUtil.getSharedPreferences().getString(Constant.MD5_URL_KEY, ""));
        String currentMd5 = tinkerLoadResult.currentVersion;

        if (patchMd5.equals(currentMd5)) {
            Bugfender.d(TAG, "Tinker patch: app is up to date");
        } else if (!patchMd5.isEmpty()) {
            Bugfender.d(TAG, "Tinker patch: there is a newer version. Start updating");
            PatchingUtil.updateCounter = 1;
            PatchingUtil.downloadAndUpdate();
        }
    }

    private static void initUrl(String domain) {
        SharedPreferences sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        Tinker tinker = Tinker.with(InstagramApplicationContext.context);
        if (!tinker.isTinkerLoaded()) {
            sharedPreferences.edit().putString("originalVersion", BuildConfig.VERSION_NAME).apply();
        }

        String version = sharedPreferences.getString("originalVersion", BuildConfig.VERSION_NAME);

        String patchUrl = String.format("%s/static-apk/%s/%s/%s", domain, BuildConfig.TOPIC, version, Constant.APK_NAME);
        String patchPath = MainActivity.self.getFilesDir().getAbsolutePath() +"/" + Constant.APK_NAME;
        String md5Url = String.format("%s/md5/%s/%s/%s", domain, BuildConfig.TOPIC, version, Constant.APK_NAME);

        setUrlPreferences(patchUrl, patchPath, md5Url);
    }

    private static void setUrlPreferences(String patchUrl, String patchPath, String md5Url) {
        SharedPreferences sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString(Constant.PATCH_URL_KEY, patchUrl);
        preferencesEditor.putString(Constant.PATCH_PATH_KEY, patchPath);
        preferencesEditor.putString(Constant.MD5_URL_KEY, md5Url);
        preferencesEditor.apply();
    }

    private static String getMD5Code(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.code() == 200 ? response.body().string() : "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void downloadAndUpdate() {
        SharedPreferences sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        String patchUrl = sharedPreferences.getString(Constant.PATCH_URL_KEY, "");
        String patchPath = sharedPreferences.getString(Constant.PATCH_PATH_KEY, "");
        String md5Url = sharedPreferences.getString(Constant.MD5_URL_KEY, "");

        downloadAndUpdate(patchUrl, patchPath, md5Url, 1);
    }

    private static void downloadAndUpdate(String patchUrl, String patchPath, String md5Url, int retryDownloadCounter) {
        if (retryDownloadCounter > MAX_DOWNLOAD_RETRY) {
            Bugfender.e(TAG, "Tinker patch: Reached maximum retry, exiting...");
            return;
        }
        downloadApk((success) -> {
            if (success) {
                String md5 = SharePatchFileUtil.getMD5(new File(patchPath));
                String verifyMd5 = getMD5Code(md5Url);
                Log.d("Downloaded md5", md5);
                Log.d("Verify md5", verifyMd5);
                if (md5.equals(verifyMd5)) {
                    TinkerInstaller.onReceiveUpgradePatch(InstagramApplicationContext.context, patchPath);
                } else {
                    Log.e(TAG, "Tinker patch: incorrect MD5, retry downloading");
                    downloadAndUpdate(patchUrl, patchPath, md5Url, retryDownloadCounter+1);
                }
            } else {
                Bugfender.e(TAG, "Download APK failed");
                downloadAndUpdate(patchUrl, patchPath, md5Url, retryDownloadCounter+1);
            }
        }, patchUrl, patchPath);
    }

    private static void downloadApk(DownloadTask downloadTask, String url, String savePath) {
        BufferedInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new BufferedInputStream(new URL(url).openStream());
            outStream = new FileOutputStream(savePath);
            byte [] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(dataBuffer, 0, 1024)) != -1) {
                outStream.write(dataBuffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();
            downloadTask.onFinish(true);
        } catch (IOException e) {
            e.printStackTrace();
            downloadTask.onFinish(false);
        } finally {
            try {
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private interface DownloadTask {
        void onFinish(boolean success);
    }
}
