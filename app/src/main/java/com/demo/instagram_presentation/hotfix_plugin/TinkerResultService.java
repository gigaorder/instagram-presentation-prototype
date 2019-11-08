package com.demo.instagram_presentation.hotfix_plugin;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.Constants;
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

        if (result.isSuccess) {
            deleteRawPatchFile(new File(result.rawPatchFilePath));

            if (checkIfNeedKill(result)) {
                Intent intent = new Intent(Constants.DISPLAY_APP_MESSAGE_ACTION);
                intent.putExtra("message", "A new patch is available. App is updating");
                getApplicationContext().sendBroadcast(intent);
                try {
                    Thread.sleep(30 * 1000); // 30s
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
