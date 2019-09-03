package com.demo.instagram_presentation;

import android.app.Application;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.DeviceInfoUtil;

public class App extends Application {
    public static final String DEVICE_ID = DeviceInfoUtil.getDeviceId();

    @Override
    public void onCreate() {
        super.onCreate();

        Bugfender.init(this, Constants.BUGFENDER_APP_TOKEN, BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging();
    }
}
