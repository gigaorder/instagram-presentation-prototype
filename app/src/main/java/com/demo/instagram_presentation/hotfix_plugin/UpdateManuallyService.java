package com.demo.instagram_presentation.hotfix_plugin;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.AppPreferencesUtil;

public class UpdateManuallyService extends Service {
    private final String TAG = InstagramApplicationContext.DEVICE_ID;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bugfender.d(TAG, "Load patch");
        String domain = AppPreferencesUtil.getSharedPreferences().getString(Constant.DOMAIN_KEY, Constant.DEFAULT_DOMAIN);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                PatchingUtil.checkForUpdate(domain);
                return null;
            }
        }.execute();

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
