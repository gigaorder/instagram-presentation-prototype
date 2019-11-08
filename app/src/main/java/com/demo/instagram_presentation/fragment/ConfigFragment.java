package com.demo.instagram_presentation.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.broadcast_receiver.WifiConnectReceiver;
import com.demo.instagram_presentation.listener.WifiConnectListener;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.BroadcastReceiverUtil;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.NetworkUtil;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.WIFI_P2P_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class ConfigFragment extends Fragment implements WifiConnectListener {
    @BindView(R.id.fragment_config_txtServerInfo)
    TextView txtServerInfo;
    @BindView(R.id.fragment_config_txtTimer)
    TextView txtTimer;
    @BindView(R.id.fragment_config_imgBackground)
    ImageView imgBackground;
    @BindView(R.id.fragment_config_imgLogoText)
    ImageView imgLogoText;
    @BindView(R.id.fragment_config_imgLogo)
    ImageView imgLogo;
    @BindView(R.id.fragment_config_txtError)
    TextView txtError;
    @BindView(R.id.fragment_config_txtAppInfo)
    TextView txtAppInfo;

    @BindString(R.string.config_server_cant_start)
    String configServerCantStartMsg;
    @BindString(R.string.wifi_detected)
    String wifiDetectedMsg;
    @BindString(R.string.pref_is_wifi_connected)
    String isWifiConnectedPrefKey;
    @BindString(R.string.wifi_direct_cant_start)
    String wifiDirectCantStartMsg;
    @BindString(R.string.getting_p2p_info)
    String gettingInfoMsg;
    @BindString(R.string.wifi_direct_no_info)
    String wifiDirectNoInfoMsg;
    @BindString(R.string.source_url_not_set)
    String errorSourceUrlNotSet;
    @BindString(R.string.wait_for_network)
    String waitForNetworkMsg;
    @BindString(R.string.pref_instagram_source)
    String instagramSourceUrlPrefKey;
    @BindString(R.string.pref_instagram_source_tags)
    String instagramSourceTagsPrefKey;
    @BindString(R.string.pref_required_login)
    String requiredLoginPrefKey;
    @BindString(R.string.pref_login_error_msg)
    String loginErrorPrefKey;
    @BindString(R.string.app_info_text)
    String appInfoMsg;

    private SharedPreferences sharedPreferences;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiP2pChannel;
    private WifiManager wifiManager;
    private WifiConnectReceiver wifiConnectReceiver;
    private boolean wifiConnected;
    private ConfigFragment thisFragment;
    private String instagramSourceUrl;
    private String instagramSourceTags;
    private boolean isRequiredLogin;

    private CountDownTimer configServerTimer;

    public ConfigFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentRootView = inflater.inflate(R.layout.fragment_config, container, false);
        ButterKnife.bind(this, fragmentRootView);
        thisFragment = this;

        // Load background images
//        Picasso.get().load(R.drawable.fallback_screen_bg_16_9).centerCrop().fit().noFade().into(imgBackground);
//        Picasso.get().load(R.drawable.feed2wall_logo_white).centerCrop().fit().noFade().into(imgLogoText);
//        Picasso.get().load(R.drawable.rockiton).centerCrop().fit().noFade().into(imgLogo);

        sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        wifiManager = (WifiManager) MainActivity.self.getApplicationContext().getSystemService(WIFI_SERVICE);
        instagramSourceUrl = sharedPreferences.getString(instagramSourceUrlPrefKey, null);
        instagramSourceTags = sharedPreferences.getString(instagramSourceTagsPrefKey, null);
        isRequiredLogin = sharedPreferences.getBoolean(requiredLoginPrefKey, false);

        AppPreferencesUtil.setDefaultImageSize();

        txtTimer.setVisibility(View.GONE);
        txtServerInfo.setVisibility(View.VISIBLE);
        txtAppInfo.setText(String.format(Locale.ENGLISH, appInfoMsg, BuildConfig.VERSION_NAME, MainActivity.DEVICE_ID));

        Handler handler = new Handler();
        new CountDownTimer(Constants.NETWORK_STATUS_CHECK_DELAY, 1000) {
            @Override
            public void onTick(long l) {
                txtServerInfo.setText(String.format(Locale.ENGLISH, waitForNetworkMsg, l / 1000));
            }

            @Override
            public void onFinish() {
                if (AppPreferencesUtil.isAbleToDisplaySlideshow()) {
                    // If Wi-Fi is available and source URL/tags are not null -> replace the fragment with SlideFragment
                    MainActivity.self.sendBroadcast(new Intent(Constants.SHOW_IMAGE_SLIDE_ACTION));
                } else {
                    if (NanoHttpdWebServer.getInstance().isAlive()) {
                        wifiConnectReceiver = new WifiConnectReceiver(thisFragment);
                        IntentFilter ifWifiStateChanged = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                        MainActivity.self.registerReceiver(wifiConnectReceiver, ifWifiStateChanged);

                        if (NetworkUtil.isWifiConnected()) {
                            setServerInfoOnWifi("");
                            setErrorMsg();
                        } else {
                            wifiConnected = false;

                            sharedPreferences.edit().putBoolean(isWifiConnectedPrefKey, false).apply();
                            // Turn on wifi and start scanning
                            if (!wifiManager.isWifiEnabled()) {
                                wifiManager.setWifiEnabled(true);
                            }

                            // Start Wi-Fi Direct
                            // Config server info will be set after Wi-Fi Direct is established (in GroupInfoListener)
                            wifiP2pManager = (WifiP2pManager) MainActivity.self.getSystemService(WIFI_P2P_SERVICE);
                            wifiP2pChannel = wifiP2pManager.initialize(MainActivity.self.getApplicationContext(),
                                    MainActivity.self.getMainLooper(), null);
                            wifiP2pManager.createGroup(wifiP2pChannel, onWifiDirectStartedListener);

                            // Delay because Wi-Fi Direct may not be initialized immediately
                            handler.postDelayed(() ->
                                    wifiP2pManager.requestGroupInfo(wifiP2pChannel, wifiP2pInfoListener), 5000);

                            wifiManager.startScan();
                        }
                    } else {
                        setErrorMsg();
                        txtTimer.setVisibility(View.GONE);
                    }
                }
            }
        }.start();

        return fragmentRootView;
    }

    private void setErrorMsg() {
        txtError.setVisibility(View.VISIBLE);
        if (!NanoHttpdWebServer.getInstance().isAlive()) {
            txtError.setText(configServerCantStartMsg);
        } else if (instagramSourceUrl == null && instagramSourceTags == null) {
            setServerInfoOnWifi("");
            txtError.setText(errorSourceUrlNotSet);
        } else if (isRequiredLogin) {
            setServerInfoOnWifi("authorize");
            txtError.setText(sharedPreferences.getString(loginErrorPrefKey, "Login error"));
        } else if (!AppPreferencesUtil.isInternetAvailable()) {
            setServerInfoOnWifi("wifi");
            txtError.setText("Internet is not available.\nPlease change wifi connection.");
        }
    }

    private void hideServerInfoText() {
        txtServerInfo.setVisibility(View.GONE);
        txtTimer.setVisibility(View.GONE);
    }

    private void setServerInfoOnWifi(String route) {
        WifiInfo info = wifiManager.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        String ssid = info.getSSID();
        final String formatedIpAddress = String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        String serverStatus = "Status: Online | ";
        String wifiSsid = String.format("Connected WiFi SSID: %s\n", ssid);
        String configServerIp = String.format("Config server: %s:%d/%s", formatedIpAddress, Constants.WEB_SERVER_PORT, route);
        int prevLength = serverStatus.length() + wifiSsid.length();

        Spannable serverInfo = new SpannableString(serverStatus + wifiSsid + configServerIp);

        serverInfo.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "online" is green
        serverInfo.setSpan(new StyleSpan(Typeface.BOLD), serverStatus.length() + 21, serverStatus.length() + 21 + ssid.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // SSID is bold
        serverInfo.setSpan(new StyleSpan(Typeface.BOLD), prevLength + 15, prevLength + 15 + formatedIpAddress.length() + 1 + String.valueOf(Constants.WEB_SERVER_PORT).length() + 1 + route.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //IP is bold

        txtServerInfo.setText(serverInfo);

        startConfigServerMsgTimer(true);
    }

    private void startConfigServerMsgTimer(boolean isOnWifi) {
        int length = isOnWifi ? Constants.HIDE_SERVER_INFO_ON_WIFI_DELAY : Constants.HIDE_SERVER_INFO_ON_WIFI_DIRECT_DELAY;

        if (configServerTimer == null) {
            configServerTimer = new CountDownTimer(length, 1000) {
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
    }

    private WifiP2pManager.ActionListener onWifiDirectStartedListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            if (wifiConnected) return;
            txtServerInfo.setText(gettingInfoMsg);
        }

        @Override
        public void onFailure(int reason) {
            if (wifiConnected) return;

            if (reason == WifiP2pManager.ERROR) {
                txtServerInfo.setText(wifiDirectCantStartMsg);
            } else {
                // Wi-Fi Direct may have already been turned on
                txtServerInfo.setText(gettingInfoMsg);
            }
        }
    };

    private WifiP2pManager.GroupInfoListener wifiP2pInfoListener = groupInfo -> {
        if (wifiConnected) return;

        // Set server info on WiFi Direct
        if (groupInfo != null) {
            String p2pNetworkName = groupInfo.getNetworkName();
            String passphrase = groupInfo.getPassphrase();

            String serverStatus = "Status: Online\n";
            String wifiDirectSsid = String.format("WiFi Direct SSID: \"%s\" | ", p2pNetworkName);
            String password = String.format("Password: \"%s\"\n", passphrase);
            String configServerIp = String.format("Config server: 192.168.49.1:%d/wifi", Constants.WEB_SERVER_PORT);

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

    @Override
    public void onWifiConnected() {
        wifiConnected = sharedPreferences.getBoolean(isWifiConnectedPrefKey, false);
        if (!wifiConnected) {
            wifiConnected = true;
            txtServerInfo.setText(wifiDetectedMsg);
            sharedPreferences.edit().putBoolean(isWifiConnectedPrefKey, true).apply();
        }

        Handler handler = new Handler();

        handler.postDelayed(() -> {

            if (wifiP2pChannel != null) {
                wifiP2pManager.removeGroup(wifiP2pChannel, null);
            }

            if (AppPreferencesUtil.isAbleToDisplaySlideshow()) {
                MainActivity.self.sendBroadcast(new Intent(Constants.SHOW_IMAGE_SLIDE_ACTION));
            } else {
                setErrorMsg();
            }
        }, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (wifiP2pManager != null) {
            wifiP2pManager.removeGroup(wifiP2pChannel, null);
        }

        BroadcastReceiverUtil.unregisterReceiver(MainActivity.self, wifiConnectReceiver);
    }
}
