package com.demo.instagram_presentation.hotfix_plugin;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.activity.MainActivity;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;

import java.io.File;

public class TinkerResultService extends DefaultTinkerResultService {
    private static final String TAG = MainActivity.DEVICE_ID;

    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            TinkerLog.e(TAG, "TinkerResultService received null result!!!!");
            return;
        }
        TinkerLog.i(TAG, "TinkerResultService receive result: %s", result.toString());

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (result.isSuccess) {
                Toast.makeText(getApplicationContext(), "patch success, please restart process", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "patch fail, please check reason", Toast.LENGTH_LONG).show();
            }
        });

        if (result.isSuccess) {
            deleteRawPatchFile(new File(result.rawPatchFilePath));

            if (checkIfNeedKill(result)) {
                restartProcess();
            } else {
                TinkerLog.i(TAG, "I have already install the newly patch version!");
            }
        } else {
            TinkerLog.e(TAG, String.format("Update patch failed %d time(s), retry update process...", PatchingUtil.updateCounter));
            if (PatchingUtil.updateCounter < PatchingUtil.MAX_UPDATE_RETRY) {
                PatchingUtil.updateCounter += 1;
                PatchingUtil.downloadAndUpdate();
            } else {
                Bugfender.e(TAG, String.format("Stop update process after %d times retry", PatchingUtil.MAX_UPDATE_RETRY));
            }
        }
    }

    private void restartProcess() {
        TinkerLog.i(TAG, "app is background now, i can kill quietly");
        //you can send service or broadcast intent to restart your process
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
