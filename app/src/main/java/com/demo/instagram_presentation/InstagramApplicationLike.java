package com.demo.instagram_presentation;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.hotfix_plugin.TinkerManager;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.IOException;

@DefaultLifeCycle(application = "com.demo.instagram_presentation.InstagramApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class InstagramApplicationLike extends DefaultApplicationLike {
    public InstagramApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Bugfender.init(getApplication(), Constants.BUGFENDER_APP_TOKEN, BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(getApplication());
        Bugfender.enableLogcatLogging();

        // init webserver
        NanoHttpdWebServer webServerInstance = new NanoHttpdWebServer.Builder(getApplication(), Constants.WEB_SERVER_PORT).build();
        NanoHttpdWebServer.setSingletonInstace(webServerInstance);
        try {
            NanoHttpdWebServer.getInstance().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        NanoHttpdWebServer.getInstance().stop();
        super.onTerminate();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);

        InstagramApplicationContext.application = getApplication();
        InstagramApplicationContext.context = getApplication();
        InstagramApplicationContext.DEVICE_ID = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);

        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.setUpgradeRetryEnable(true);

        TinkerManager.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }
}
