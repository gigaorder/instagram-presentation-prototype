package com.demo.instagram_presentation.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.broadcast_receiver.WifiConnectReceiver;
import com.demo.instagram_presentation.broadcast_receiver.WifiScanResultReceiver;
import com.demo.instagram_presentation.fragment.ImagePresentationFragment;
import com.demo.instagram_presentation.listener.WifiConnectListener;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.util.ScreenUtil;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements WifiConnectListener {
    @BindView(R.id.main_activity_txtServerInfo)
    TextView txtServerInfo;
    @BindView(R.id.main_activity_txtTimer)
    TextView txtTimer;
    @BindView(R.id.main_activity_imgBg)
    ImageView imgBackground;
    @BindView(R.id.main_activity_imgLogoText)
    ImageView imgLogoText;
    @BindView(R.id.main_activity_imgLogo)
    ImageView imgLogo;

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
    @BindString(R.string.pref_instagram_source)
    String instagramSourcePrefKey;

    public final static int FRAGMENT_CONTAINER_ID = R.id.main_activity_fragment_container;
    private NanoHttpdWebServer webServer;
    private SharedPreferences sharedPreferences;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;
    private WifiManager wifiManager;
    private WifiScanResultReceiver wifiScanResultReceiver;
    private WifiConnectReceiver wifiConnectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LicenseUtil.isKeyIdFileInitialized(getApplicationContext())) {
            LicenseUtil.initKeyIdFile(getApplicationContext());
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Picasso.get().load(R.drawable.fallback_screen_bg_16_9).centerCrop().fit().noFade().into(imgBackground);

        hideServerInfoText();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        setDefaultPrefValues();

        String instagramSourceUrl = sharedPreferences.getString(instagramSourcePrefKey, null);
        startConfigServer();
        showServerInfoText();
        registerBroadcastReceivers();

        if (isWifiConnected()) {
            setServerInfoOnWifi();

            if (instagramSourceUrl != null) {
                startPresentationFragment();
            }
        } else {
            //TODO: refactor this
            sharedPreferences.edit().putBoolean("wifi_connected", false).apply();
            // Turn on wifi and start scanning
            wifiManager.setWifiEnabled(true);
            wifiManager.startScan();

            // Start Wi-Fi Direct
            // Config server info will be set after Wi-Fi Direct is established (in GroupInfoListener)
            wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
            wifiP2pChannel = wifiP2pManager.initialize(getApplicationContext(), getMainLooper(), null);
            wifiP2pManager.createGroup(wifiP2pChannel, onWifiDirectStartedListener);

            Handler handler = new Handler();
            handler.postDelayed(() ->
                    wifiP2pManager.requestGroupInfo(wifiP2pChannel, wifiP2pInfoListener), 5000); // Delay because Wi-Fi Direct may not have been initialized
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showBackground();
    }

    @Override
    protected void onStop() {
        super.onStop();

        webServer.stop();

        if (wifiP2pManager != null) {
            wifiP2pManager.removeGroup(wifiP2pChannel, null);
        }

        unregisterReceiver(appPreferenceChangedReceiver);

        // TODO: refactor register/unregister broadcast receivers
        if (wifiConnectReceiver != null) {
            unregisterReceiver(wifiConnectReceiver);
        }

        if (wifiScanResultReceiver != null) {
            unregisterReceiver(wifiScanResultReceiver);
        }

        finish();
    }

    private void setServerInfoOnWifi() {
        WifiInfo info = wifiManager.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        String ssid = info.getSSID();
        final String formatedIpAddress = String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        String serverStatus = "Status: Online | ";
        String wifiSsid = String.format("Connected WiFi SSID: %s\n", ssid);
        String configServerIp = String.format("Config server: %s:%d", formatedIpAddress, Constants.WEB_SERVER_PORT);
        int prevLength = serverStatus.length() + wifiSsid.length();

        Spannable serverInfo = new SpannableString(serverStatus + wifiSsid + configServerIp);

        serverInfo.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "online" is green
        serverInfo.setSpan(new StyleSpan(Typeface.BOLD), serverStatus.length() + 21, serverStatus.length() + 21 + ssid.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // SSID is bold
        serverInfo.setSpan(new StyleSpan(Typeface.BOLD), prevLength + 15, prevLength + 15 + formatedIpAddress.length() + 1 + String.valueOf(Constants.WEB_SERVER_PORT).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //IP is bold

        txtServerInfo.setText(serverInfo);

        startConfigServerMsgTimer(true);
    }

    private void setDefaultPrefValues() {
        if (sharedPreferences.getString(imgMainWidthPrefKey, null) == null) {
            int width = ScreenUtil.getScreenWidth(this);
            sharedPreferences.edit().putString(imgMainWidthPrefKey, String.valueOf(width)).apply();
        }

        if (sharedPreferences.getString(imgMainHeightPrefKey, null) == null) {
            int height = (int) (ScreenUtil.getScreenHeight(this) * 0.75); // Likely to be changed by user -> no need to extract as a constant
            sharedPreferences.edit().putString(imgMainHeightPrefKey, String.valueOf(height)).apply();
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
        } catch (IOException e) {
            e.printStackTrace();
            txtServerInfo.setText(configServerCantStartMsg);
        }
    }

    private WifiP2pManager.GroupInfoListener wifiP2pInfoListener = groupInfo -> {
        // Set server info on WiFi Direct
        if (groupInfo != null) {
            String p2pNetworkName = groupInfo.getNetworkName();
            String passphrase = groupInfo.getPassphrase();

            String serverStatus = "Status: Online\n";
            String wifiDirectSsid = String.format("WiFi Direct SSID: \"%s\" | ", p2pNetworkName);
            String password = String.format("Password: \"%s\"\n", passphrase);
            String configServerIp = String.format("Config server: 192.168.49.1:%d/wifi", Constants.WEB_SERVER_PORT, Constants.WEB_SERVER_PORT);

            int prevLength1 = serverStatus.length() + wifiDirectSsid.length();
            int prevLengthTotal = prevLength1 + password.length();

            Spannable serverInfo = new SpannableString(serverStatus + wifiDirectSsid + password + configServerIp);

            serverInfo.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "online" is green
            serverInfo.setSpan(new StyleSpan(Typeface.BOLD), serverStatus.length() + 18, serverStatus.length() + 18 + p2pNetworkName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // SSID is bold
            serverInfo.setSpan(new StyleSpan(Typeface.BOLD), prevLength1 + 10, prevLength1 + 10 + passphrase.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Password is bold
            serverInfo.setSpan(new StyleSpan(Typeface.BOLD), prevLengthTotal + 15, prevLengthTotal + 15 + 12 + 1 + String.valueOf(Constants.WEB_SERVER_PORT).length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //IP is bold

            txtServerInfo.setText(serverInfo);

            startConfigServerMsgTimer(false);
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
                // Wi-Fi Direct may have already been turned on
                txtServerInfo.setText(gettingInfoMsg);
            }
        }
    };

    private BroadcastReceiver appPreferenceChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startPresentationFragment();
        }
    };


    private void hideServerInfoText() {
        txtServerInfo.setVisibility(View.GONE);
        txtTimer.setVisibility(View.GONE);
    }

    private void showServerInfoText() {
        txtServerInfo.setVisibility(View.VISIBLE);
    }

    private void startConfigServerMsgTimer(boolean isOnWifi) {
        int length = isOnWifi ? Constants.HIDE_SERVER_INFO_ON_WIFI_DELAY : Constants.HIDE_SERVER_INFO_ON_WIFI_DIRECT_DELAY;

        new CountDownTimer(length, 1000) {
            @Override
            public void onTick(long l) {
                txtTimer.setText(String.format("This message will disappear in %d seconds", l / 1000));
            }

            @Override
            public void onFinish() {
                hideServerInfoText();
            }
        }.start();

        txtTimer.setVisibility(View.VISIBLE);
    }

    private void startPresentationFragment() {
        hideServerInfoText();
        hideBackground();

        if (wifiConnectReceiver != null) {
            unregisterReceiver(wifiConnectReceiver);
            wifiConnectReceiver = null;
        }

        ImagePresentationFragment imagePresentationFragment = new ImagePresentationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("serverInfo", txtServerInfo.getText().toString());
        imagePresentationFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(FRAGMENT_CONTAINER_ID, imagePresentationFragment)
                .commit();
    }

    private void showBackground() {
        imgBackground.setVisibility(View.VISIBLE);
        imgLogo.setVisibility(View.VISIBLE);
        imgLogoText.setVisibility(View.VISIBLE);
    }

    private void hideBackground() {
        imgBackground.setVisibility(View.GONE);
        imgLogo.setVisibility(View.GONE);
        imgLogoText.setVisibility(View.GONE);
    }

    @Override
    public void onWifiConnected() {
        unregisterReceiver(wifiConnectReceiver);
        //TODO: refactor this
        sharedPreferences.edit().putBoolean("wifi_connected", true).apply();
        showServerInfoText();

        txtServerInfo.setText("Wifi detected, app will restart after 10 seconds");
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            setServerInfoOnWifi();

            if (wifiP2pChannel != null) {
                wifiP2pManager.removeGroup(wifiP2pChannel, null);
            }

            showServerInfoText();

            startPresentationFragment();
        }, 10000);
    }

    private void registerBroadcastReceivers() {
        wifiConnectReceiver = new WifiConnectReceiver(this);
        wifiScanResultReceiver = new WifiScanResultReceiver();

        IntentFilter ifPrefChanged = new IntentFilter(Constants.PREFERENCE_CHANGED_ACTION);
        IntentFilter ifWifiStateChanged = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        IntentFilter ifWifiScanResult = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        registerReceiver(appPreferenceChangedReceiver, ifPrefChanged);
        registerReceiver(wifiConnectReceiver, ifWifiStateChanged);
        registerReceiver(wifiScanResultReceiver, ifWifiScanResult);
    }
}