package com.demo.instagram_presentation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.broadcast_receiver.WifiScanResultReceiver;
import com.demo.instagram_presentation.fragment.ConfigFragment;
import com.demo.instagram_presentation.fragment.ImageSlideFragment;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.BroadcastReceiverUtil;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;

import java.io.IOException;

import butterknife.BindString;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindString(R.string.pref_instagram_source)
    String instagramSourcePrefKey;

    private SharedPreferences sharedPreferences;
    private NanoHttpdWebServer webServer;
    private boolean configServerStarted;
    private WifiScanResultReceiver wifiScanResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LicenseUtil.isKeyIdFileInitialized()) {
            LicenseUtil.initKeyIdFile();
        }

        AppPreferencesUtil.initSharedPreference(getApplicationContext());

        // Set fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        String instagramSourceUrl = sharedPreferences.getString(instagramSourcePrefKey, null);

        // Register Broadcast receivers
        wifiScanResultReceiver = new WifiScanResultReceiver();
        IntentFilter ifPrefChanged = new IntentFilter(Constants.PREFERENCE_CHANGED_ACTION);
        IntentFilter ifWifiScanResult = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(appPreferenceChangedReceiver, ifPrefChanged);
        registerReceiver(wifiScanResultReceiver, ifWifiScanResult);

        startConfigServer();

        if (isWifiConnected() && instagramSourceUrl != null) {
            ImageSlideFragment imageSlideFragment = new ImageSlideFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_fragment_container, imageSlideFragment)
                    .commit();
        } else {
            ConfigFragment configFragment = new ConfigFragment(configServerStarted);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragment_container, configFragment)
                    .commit();
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    private void startConfigServer() {
        try {
            webServer = new NanoHttpdWebServer(getApplicationContext(), Constants.WEB_SERVER_PORT);
            webServer.start();
            configServerStarted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver appPreferenceChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recreate();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastReceiverUtil.unregisterReceiver(this, appPreferenceChangedReceiver);
        BroadcastReceiverUtil.unregisterReceiver(this, wifiScanResultReceiver);
        webServer.stop();
    }
}