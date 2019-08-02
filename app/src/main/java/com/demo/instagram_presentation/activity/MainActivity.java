package com.demo.instagram_presentation.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.fragment.ImagePresentationFragment;
import com.demo.instagram_presentation.fragment.SettingsFragment;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_activity_txtServerInfo)
    TextView txtServerInfo;

    public final static int FRAGMENT_CONTAINER_ID = R.id.settings_container;
    private NanoHttpdWebServer webServer;
    private BroadcastReceiver appPreferenceChangedReceiver;
    private String serverStatus = "offline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        webServer = new NanoHttpdWebServer(getApplicationContext(), Constants.WEB_SERVER_PORT);
        try {
            webServer.start();
            logWebServerIpAddress();
            serverStatus = "online";
            setServerInfo();
        } catch (IOException e) {
            e.printStackTrace();
            txtServerInfo.setText("Remote config server status: offline. This message will disappear after 60 seconds");
        } finally {
            // Make server info text disappear after a while
            Handler handler = new Handler();
            handler.postDelayed(() -> txtServerInfo.setVisibility(View.GONE), 60000);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(FRAGMENT_CONTAINER_ID, new ImagePresentationFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PREFERENCE_CHANGED_ACTION);

        appPreferenceChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(FRAGMENT_CONTAINER_ID, new ImagePresentationFragment())
                        .commit();
            }
        };

        registerReceiver(appPreferenceChangedReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(appPreferenceChangedReceiver);
    }

    private void logWebServerIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        Log.d("web-server-info", "Web server is listening on " + formatedIpAddress + ":" + Constants.WEB_SERVER_PORT);
    }

    //TODO: refactor code
    private void setServerInfo() {
        String serverInfo = "Remote config server status: " + serverStatus;

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        serverInfo += ". Connected WiFi SSID: " + ssid;

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        serverInfo += ". Web server is listening on " + formatedIpAddress + ":" + Constants.WEB_SERVER_PORT;
        serverInfo += ". This message will disappear after 60 seconds";

        txtServerInfo.setText(serverInfo);
    }
}