package com.demo.instagram_presentation.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.demo.instagram_presentation.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Activity activity;
    private Logger log;

    public AppExceptionHandler(Activity activity) {
        this.activity = activity;
        log = LoggerFactory.getLogger(AppExceptionHandler.class);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        log.debug("Exception caught, app will be restarted");
        log.debug(throwable.toString());

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        activity.finish();
        System.exit(2);
    }
}
