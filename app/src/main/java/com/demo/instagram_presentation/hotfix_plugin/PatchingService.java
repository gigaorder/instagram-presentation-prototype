package com.demo.instagram_presentation.hotfix_plugin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.PermissionUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class PatchingService extends FirebaseMessagingService {
    private final String TAG = MainActivity.DEVICE_ID;
    private final String LOAD_PATCH = "load_patch";
    private final String CLEAN_PATCH = "clean_patch";
    private final String LOAD_LIBRARY = "load_library";
    private final String KILL_PROCESS = "kill_process";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String command = remoteMessage.getData().get("command");
        switch (command) {
            case LOAD_PATCH:
                Bugfender.d(TAG, "Load patch");
                String domain = remoteMessage.getData().get("domain");
                String apkUrl = String.format("%s/static-apk/%s/%s/%s", domain, BuildConfig.TOPIC, BuildConfig.VERSION_NAME, Constants.APK_NAME);
                String apkPath = MainActivity.self.getFilesDir().getAbsolutePath() +"/" + Constants.APK_NAME;
                Log.d("APK Path", apkPath);
                downloadApk((success) -> {
                    if (success) {
                        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), apkPath);
                    } else {
                        Bugfender.e(TAG, "Download APK failed");
                    }
                }, apkUrl, apkPath);
                break;
            case LOAD_LIBRARY:
                Bugfender.d(TAG, "Load library");
                TinkerLoadLibrary.installNavitveLibraryABI(getApplicationContext(), "armeabi");
                System.loadLibrary("stlport_shared");
                break;
            case CLEAN_PATCH:
                Bugfender.d(TAG, "Clean patch");
                Tinker.with(getApplicationContext()).cleanPatch();
                break;
            case KILL_PROCESS:
                Bugfender.d(TAG, "Kill process");
                ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }

    private void downloadApk(DownloadTask downloadTask, String url, String savePath) {
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
