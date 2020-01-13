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
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.R;
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
    @BindView(R.id.fragment_config_imgBackground)
    ImageView imgBackground;
    @BindView(R.id.fragment_config_txtError)
    TextView txtError;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentRootView = inflater.inflate(R.layout.fragment_config, container, false);
        ButterKnife.bind(this, fragmentRootView);
        thisFragment = this;

        sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        wifiManager = (WifiManager) InstagramApplicationContext.context.getSystemService(WIFI_SERVICE);
        instagramSourceUrl = sharedPreferences.getString(instagramSourceUrlPrefKey, null);
        instagramSourceTags = sharedPreferences.getString(instagramSourceTagsPrefKey, null);
        isRequiredLogin = sharedPreferences.getBoolean(requiredLoginPrefKey, false);

        AppPreferencesUtil.setDefaultImageSize(getActivity());

        txtServerInfo.setVisibility(View.VISIBLE);

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
                    InstagramApplicationContext.context.sendBroadcast(new Intent(Constants.SHOW_IMAGE_SLIDE_ACTION));
                } else {
                    if (NanoHttpdWebServer.getInstance().isAlive()) {
                        wifiConnectReceiver = new WifiConnectReceiver(thisFragment);
                        IntentFilter ifWifiStateChanged = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                        getActivity().registerReceiver(wifiConnectReceiver, ifWifiStateChanged);

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
                            wifiP2pManager = (WifiP2pManager) getActivity().getSystemService(WIFI_P2P_SERVICE);
                            wifiP2pChannel = wifiP2pManager.initialize(InstagramApplicationContext.context,
                                    getActivity().getMainLooper(), null);
                            wifiP2pManager.createGroup(wifiP2pChannel, onWifiDirectStartedListener);

                            // Delay because Wi-Fi Direct may not be initialized immediately
                            handler.postDelayed(() ->
                                    wifiP2pManager.requestGroupInfo(wifiP2pChannel, wifiP2pInfoListener), 5000);

                            wifiManager.startScan();
                        }
                    } else {
                        setErrorMsg();
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

    private void setServerInfoOnWifi(String route) {
        WifiInfo info = wifiManager.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        String ssid = info.getSSID();
        final String formatedIpAddress = String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        String wifiSsid = String.format("WIFI: %s", ssid);
        String configServerIp = String.format("Setup Site: %s:%d/%s", formatedIpAddress, Constants.WEB_SERVER_PORT, route);

        Spannable serverInfo = new SpannableString(wifiSsid + "  |  " + configServerIp);

        serverInfo.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        serverInfo.setSpan(new StyleSpan(Typeface.BOLD), 6 + ssid.length(), 6 + ssid.length() + 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtServerInfo.setText(serverInfo);
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

            String wifiDirectSsid = String.format("WIFI: \"%s\" | ", p2pNetworkName);
            String password = String.format("Password: \"%s\"\n", passphrase);
            String configServerIp = String.format("Setup Site: 192.168.49.1:%d/wifi", Constants.WEB_SERVER_PORT);

            Spannable serverInfo = new SpannableString(wifiDirectSsid + password + configServerIp);
//
            serverInfo.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // SSID is bold
            serverInfo.setSpan(new StyleSpan(Typeface.BOLD), 6 + p2pNetworkName.length() + 5, 6 + p2pNetworkName.length() + 5 + 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Password is bold
            serverInfo.setSpan(new StyleSpan(Typeface.BOLD), 6 + p2pNetworkName.length() + 5 + 11 + passphrase.length() + 2, 6 + p2pNetworkName.length() + 5 + 11 + passphrase.length() + 2 + 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //IP is bold

            txtServerInfo.setText(serverInfo);
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
                InstagramApplicationContext.context.sendBroadcast(new Intent(Constants.SHOW_IMAGE_SLIDE_ACTION));
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

        BroadcastReceiverUtil.unregisterReceiver(getActivity(), wifiConnectReceiver);
    }
}
