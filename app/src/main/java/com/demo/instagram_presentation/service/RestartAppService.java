package com.demo.instagram_presentation.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestartAppService extends Service {
    private Logger log;
    private final String bugfenderTag = MainActivity.DEVICE_ID;

    public RestartAppService() {
        log = LoggerFactory.getLogger(RestartAppService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            startMainActivity();
        }

        return Service.START_STICKY;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        log.debug("App killed by system and will be restarted"); // Write logs to file
        Bugfender.e(bugfenderTag, "App killed by system and will be restarted"); // Send logs to Bugfender
    }
}
