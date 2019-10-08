package com.demo.instagram_presentation.hotfix_plugin;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tencent.tinker.lib.tinker.Tinker;

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
        Log.d("Tinker command", command);
        switch (command) {
            case LOAD_PATCH:
                Bugfender.d(TAG, "Load patch");

                SharedPreferences sharedPreferences = AppPreferencesUtil.getSharedPreferences();
                Tinker tinker = Tinker.with(getApplicationContext());
                if (!tinker.isTinkerLoaded()) {
                    sharedPreferences.edit().putString("originalVersion", BuildConfig.VERSION_NAME).apply();
                }

                String version = sharedPreferences.getString("originalVersion", BuildConfig.VERSION_NAME);
                String domain = remoteMessage.getData().get("domain");

                String patchUrl = String.format("%s/static-apk/%s/%s/%s", domain, BuildConfig.TOPIC, version, Constants.APK_NAME);
                String patchPath = MainActivity.self.getFilesDir().getAbsolutePath() +"/" + Constants.APK_NAME;
                String md5Url = String.format("%s/md5/%s/%s/%s", domain, BuildConfig.TOPIC, version, Constants.APK_NAME);

                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.putString(Constant.PATCH_URL_KEY, patchUrl);
                preferencesEditor.putString(Constant.PATCH_PATH_KEY, patchPath);
                preferencesEditor.putString(Constant.MD5_URL_KEY, md5Url);
                preferencesEditor.apply();

                PatchingUtil.updateCounter = 1;
                PatchingUtil.downloadAndUpdate();
                break;
            case LOAD_LIBRARY:
                Bugfender.d(TAG, "Load library");
                PatchingUtil.loadLibrary();
                break;
            case CLEAN_PATCH:
                Bugfender.d(TAG, "Clean patch");
                PatchingUtil.cleanPatch();
                break;
            case KILL_PROCESS:
                Bugfender.d(TAG, "Kill process");
                PatchingUtil.killProcess();
                break;
        }
    }
}
