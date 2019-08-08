package com.demo.instagram_presentation.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.fragment.ImagePresentationFragment;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.ScreenUtil;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_activity_txtServerInfo)
    TextView txtServerInfo;

    @BindString(R.string.pref_img_main_width)
    String imgMainWidthPrefKey;
    @BindString(R.string.pref_img_main_height)
    String imgMainHeightPrefKey;
    @BindString(R.string.wifi_direct_no_info)
    String wifiDirectNoInfoMsg;
    @BindString(R.string.wifi_direct_cant_start)
    String wifiDirectCantStartMsg;
    @BindString(R.string.config_server_cant_start)
    String configServerCantStartMsg;
    @BindString(R.string.getting_p2p_info)
    String gettingInfoMsg;
    @BindString(R.string.pref_wifi_list)
    String wifiListPrefKey;
    @BindString(R.string.pref_is_wifi_connected)
    String isWifiConnectedPrefKey;

    public final static int FRAGMENT_CONTAINER_ID = R.id.settings_container;
    private NanoHttpdWebServer webServer;
    private SharedPreferences sharedPreferences;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;
    private WifiManager wifiManager;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setDefaultPrefValues();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        startConfigServer();

        if (isWifiConnected()) {
            setServerInfoOnWifi();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(FRAGMENT_CONTAINER_ID, new ImagePresentationFragment())
                    .commit();
        } else {
            sharedPreferences.edit().putBoolean(isWifiConnectedPrefKey, false).apply();
            // Turn on wifi and start scanning
            wifiManager.setWifiEnabled(true);
            wifiManager.startScan();
            registerReceiver(wifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            // Start Wi-fi Direct
            // Config server info will be set after Wi-fi Direct is established (in GroupInfoListener)
            wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
            wifiP2pChannel = wifiP2pManager.initialize(getApplicationContext(), getMainLooper(), null);
            wifiP2pManager.createGroup(wifiP2pChannel, onWifiDirectStartedListener);

            Handler handler = new Handler();
            handler.postDelayed(() -> wifiP2pManager.requestGroupInfo(wifiP2pChannel, wifiP2pInfoListener), 2000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter ifPrefChanged = new IntentFilter();
        ifPrefChanged.addAction(Constants.PREFERENCE_CHANGED_ACTION);

        IntentFilter ifWifiConnected = new IntentFilter();
        ifWifiConnected.addAction(Constants.WIFI_CONNECTED_ACTION);

        registerReceiver(appPreferenceChangedReceiver, ifPrefChanged);
        registerReceiver(wifiConnectedReceiver, ifWifiConnected);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(appPreferenceChangedReceiver);
        unregisterReceiver(wifiConnectedReceiver);
    }

    //TODO: refactor code
    private void setServerInfoOnWifi() {
        String serverInfo = "Remote config server status: online";

        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format(Locale.ENGLISH, "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        serverInfo = String.format(Locale.ENGLISH, "Status: online\n" +
                "Connected WiFi SSID: %s\n" +
                "Config server IP address: %s:%d\n" +
                "This message will disappear after 60 seconds", ssid, formatedIpAddress, Constants.WEB_SERVER_PORT);

        txtServerInfo.setText(serverInfo);
    }

    private void setDefaultPrefValues() {
        if (sharedPreferences.getString(imgMainWidthPrefKey, null) == null) {
            int width = ScreenUtil.getScreenWidth(this);
            sharedPreferences.edit().putString(imgMainWidthPrefKey, String.valueOf(width)).apply();
        }

        if (sharedPreferences.getString(imgMainHeightPrefKey, null) == null) {
            int height = (int) (ScreenUtil.getScreenHeight(this) * 0.75); // Default percentage, can be changed easily -> no need to extract as a constant
            sharedPreferences.edit().putString(imgMainHeightPrefKey, String.valueOf(height)).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webServer.stop();
        wifiP2pManager.removeGroup(wifiP2pChannel, null);

        unregisterReceiver(wifiScanReceiver);
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
        } catch (IOException e) {
            e.printStackTrace();
            txtServerInfo.setText(configServerCantStartMsg);
        } finally {
            // Make server info text disappear after a while
            hideServerInfoTextAfter(60000);
        }
    }

    private WifiP2pManager.GroupInfoListener wifiP2pInfoListener = groupInfo -> {
        if (groupInfo != null) {
            String p2pNetworkName = groupInfo.getNetworkName();
            String passphrase = groupInfo.getPassphrase();
            String serverInfo = String.format(Locale.ENGLISH, "Status: online\n" +
                            "Wi-fi Direct SSID: \"%s\"\n" +
                            "Passphrase: \"%s\"\n" +
                            "Wi-fi config IP address: 192.168.49.1:%d\n" +
                            "This message will disappear after 60 seconds",
                    p2pNetworkName, passphrase, Constants.WEB_SERVER_PORT);

            txtServerInfo.setText(serverInfo);
        } else {
            txtServerInfo.setText(wifiDirectNoInfoMsg);
        }
    };

    private WifiP2pManager.ActionListener onWifiDirectStartedListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            txtServerInfo.setText(gettingInfoMsg);
        }

        @Override
        public void onFailure(int reason) {
            if (reason == WifiP2pManager.ERROR) {
                txtServerInfo.setText(wifiDirectCantStartMsg);
            } else {
                // Wi-fi Direct may have already been turned on
                txtServerInfo.setText(gettingInfoMsg);
            }
        }
    };

    private BroadcastReceiver appPreferenceChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(FRAGMENT_CONTAINER_ID, new ImagePresentationFragment())
                    .commit();
        }
    };

    private BroadcastReceiver wifiConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            txtServerInfo.setText("Wifi detected, app will restart after 10 seconds");
            Handler handler = new Handler();

            handler.postDelayed(() -> {
                unregisterReceiver(wifiScanReceiver);
                setServerInfoOnWifi();
                wifiP2pManager.removeGroup(wifiP2pChannel, null);

                txtServerInfo.setVisibility(View.VISIBLE);
                hideServerInfoTextAfter(60000);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(FRAGMENT_CONTAINER_ID, new ImagePresentationFragment())
                        .commit();
            }, 10000);
        }
    };

    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> wifiScanResults = wifiManager.getScanResults();
                Set<String> wifiSsidSet = new HashSet<>();

                for (ScanResult wifiScanResult : wifiScanResults) {
                    wifiSsidSet.add(wifiScanResult.SSID);
                }

                sharedPreferences.edit().putString(wifiListPrefKey, gson.toJson(wifiSsidSet)).apply();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void hideServerInfoTextAfter(int timeInMs) {
        Handler handler = new Handler();
        handler.postDelayed(() -> txtServerInfo.setVisibility(View.GONE), timeInMs);
    }
}