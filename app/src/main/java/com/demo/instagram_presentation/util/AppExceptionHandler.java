package com.demo.instagram_presentation.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.InstagramApplicationLike;
import com.demo.instagram_presentation.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Activity activity;
    private Logger log;
    private final String bugfenderTag = InstagramApplicationContext.DEVICE_ID;

    public AppExceptionHandler(Activity activity) {
        this.activity = activity;
        log = LoggerFactory.getLogger(AppExceptionHandler.class);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        String stackTrace = Log.getStackTraceString(throwable);
        log.debug("Exception caught, app will be restarted");
        log.debug(stackTrace); // Write to files

        Bugfender.e(bugfenderTag, "Exception caught, app will be restarted");
        Bugfender.d(bugfenderTag, stackTrace); // Send to Bugfender

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
