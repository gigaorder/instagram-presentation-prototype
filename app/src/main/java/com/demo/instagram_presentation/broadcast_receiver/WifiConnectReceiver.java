package com.demo.instagram_presentation.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.demo.instagram_presentation.listener.WifiConnectListener;

public class WifiConnectReceiver extends BroadcastReceiver {

    WifiConnectListener wifiConnectListener;

    public WifiConnectReceiver(WifiConnectListener wifiConnectListener) {
        this.wifiConnectListener = wifiConnectListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // Wifi is connected
                if (wifiConnectListener != null) {
                    wifiConnectListener.onWifiConnected();
                }
            }
        }
    }
}
