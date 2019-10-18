package com.demo.instagram_presentation.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.demo.instagram_presentation.InstagramApplicationContext;

public class NetworkUtil {
    private static ConnectivityManager connectivityManager;
    private static WifiManager wifiManager;

    public static void initNetworkService() {
        connectivityManager = (ConnectivityManager) InstagramApplicationContext.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) InstagramApplicationContext.context.getSystemService(Context.WIFI_SERVICE);
    }

    public static boolean isWifiConnected() {
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    public static int getNetworkStrength(int numberOfLevels) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
    }
}
