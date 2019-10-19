package com.demo.instagram_presentation.hotfix_plugin;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.ITinkerPatchService;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tencent.tinker.lib.tinker.Tinker;

public class PatchingService extends Service {
    private final String TAG = MainActivity.DEVICE_ID;
    private final String LOAD_PATCH = "load_patch";
    private final String CLEAN_PATCH = "clean_patch";
    private final String LOAD_LIBRARY = "load_library";
    private final String KILL_PROCESS = "kill_process";

    private ITinkerPatchService.Stub mBinder = new ITinkerPatchService.Stub() {
        @Override
        public void onNewCommandReceived(String command, String domain) {
            Log.d("Tinker command", command);
            switch (command) {
                case LOAD_PATCH:
                    Bugfender.d(TAG, "Load patch");
                    PatchingUtil.checkForUpdate(domain);
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
    };

    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

//public class PatchingService extends FirebaseMessagingService {
//    private final String TAG = MainActivity.DEVICE_ID;
//    private final String LOAD_PATCH = "load_patch";
//    private final String CLEAN_PATCH = "clean_patch";
//    private final String LOAD_LIBRARY = "load_library";
//    private final String KILL_PROCESS = "kill_process";
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        String command = remoteMessage.getData().get("command");
//        Log.d("Tinker command", command);
//        switch (command) {
//            case LOAD_PATCH:
//                Bugfender.d(TAG, "Load patch");
//
//                String domain = remoteMessage.getData().get("domain");
//                PatchingUtil.checkForUpdate(domain);
//                break;
//            case LOAD_LIBRARY:
//                Bugfender.d(TAG, "Load library");
//                PatchingUtil.loadLibrary();
//                break;
//            case CLEAN_PATCH:
//                Bugfender.d(TAG, "Clean patch");
//                PatchingUtil.cleanPatch();
//                break;
//            case KILL_PROCESS:
//                Bugfender.d(TAG, "Kill process");
//                PatchingUtil.killProcess();
//                break;
//        }
//    }
//}
