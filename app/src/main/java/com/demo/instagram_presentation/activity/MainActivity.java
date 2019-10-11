package com.demo.instagram_presentation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.provider.Settings.Secure;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.hotfix_plugin.Constant;
import com.demo.instagram_presentation.hotfix_plugin.PatchingUtil;
import com.demo.instagram_presentation.util.PermissionUtil;
import com.demo.instagram_presentation.service.RestartAppService;
import com.demo.instagram_presentation.broadcast_receiver.WifiScanResultReceiver;
import com.demo.instagram_presentation.fragment.ConfigFragment;
import com.demo.instagram_presentation.fragment.ImageSlideFragment;
import com.demo.instagram_presentation.util.AppExceptionHandler;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.BroadcastReceiverUtil;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.util.NetworkUtil;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindString(R.string.pref_instagram_source)
    String instagramSourceUrlPrefKey;
    @BindString(R.string.pref_instagram_source_tags)
    String instagramSourceTagsPrefKey;
    @BindString(R.string.login_error_intent_key)
    String loginErrorMsgIntentKey;

    private SharedPreferences sharedPreferences;
    private NanoHttpdWebServer webServer;
    private boolean configServerStarted;
    private WifiScanResultReceiver wifiScanResultReceiver;
    private Intent restartServiceIntent;
    public static MainActivity self;
    public static final String DEVICE_ID = Secure.getString(InstagramApplicationContext.context.getContentResolver(), Secure.ANDROID_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        boolean deviceBoot = getIntent().getBooleanExtra("deviceBoot", false);

        AppPreferencesUtil.initSharedPreference(getApplicationContext());
        NetworkUtil.initNetworkService();

        restartServiceIntent = new Intent(this, RestartAppService.class);
        startService(restartServiceIntent);
        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(this));

        if (!LicenseUtil.isKeyIdFileInitialized()) {
            LicenseUtil.initKeyIdFile();
        }

        // Set fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        String instagramSourceUrl = sharedPreferences.getString(instagramSourceUrlPrefKey, null);
        String instagramSourceTags = sharedPreferences.getString(instagramSourceTagsPrefKey, null);

        // Register Broadcast receivers
        IntentFilter ifPrefChanged = new IntentFilter(Constants.PREFERENCE_CHANGED_ACTION);
        registerReceiver(appPreferenceChangedReceiver, ifPrefChanged);
        IntentFilter ifLoginInfoChanged = new IntentFilter(Constants.LOGIN_INFO_CHANGED_ACTION);
        registerReceiver(appPreferenceChangedReceiver, ifLoginInfoChanged);

        IntentFilter ifLoginFailed = new IntentFilter(Constants.LOGIN_FAILED_ACTION);
        registerReceiver(loginFailedReceiver, ifLoginFailed);

        wifiScanResultReceiver = new WifiScanResultReceiver();
        IntentFilter ifWifiScanResult = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanResultReceiver, ifWifiScanResult);

        startConfigServer();

        if (!deviceBoot && NetworkUtil.isWifiConnected() && (instagramSourceUrl != null || instagramSourceTags != null)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_fragment_container, new ImageSlideFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_fragment_container, new ConfigFragment(configServerStarted))
                    .commit();
        }

        PermissionUtil.askForRequiredPermissions();
        hotfixPluginSetup();
    }

    private void startConfigServer() {
        try {
            webServer = new NanoHttpdWebServer(getApplicationContext(), Constants.WEB_SERVER_PORT);
            webServer.start();
            configServerStarted = true;
        } catch (IOException e) {
            configServerStarted = false;
            e.printStackTrace();
        }
    }

    private void hotfixPluginSetup() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.TOPIC);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                PatchingUtil.checkForUpdate(Constant.DEFAULT_DOMAIN);
                return null;
            }
        }.execute();
    }

    private BroadcastReceiver appPreferenceChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_fragment_container, new ImageSlideFragment())
                    .commit();
        }
    };

    private BroadcastReceiver loginFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_fragment_container, new ConfigFragment(true, intent.getStringExtra(loginErrorMsgIntentKey)))
                    .commit();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(restartServiceIntent);
        BroadcastReceiverUtil.unregisterReceiver(this, appPreferenceChangedReceiver);
        BroadcastReceiverUtil.unregisterReceiver(this, wifiScanResultReceiver);
        BroadcastReceiverUtil.unregisterReceiver(this, loginFailedReceiver);
        webServer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility
                ( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
    }
}