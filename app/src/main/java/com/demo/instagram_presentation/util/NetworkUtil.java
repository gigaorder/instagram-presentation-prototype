package com.demo.instagram_presentation.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    private static ConnectivityManager connectivityManager;
    private static NetworkInfo networkInfo;

    public static void initNetworkService(Activity activity) {
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isWifiConnected() {
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}
