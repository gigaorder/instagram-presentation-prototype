package com.demo.instagram_presentation.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.WIFI_SERVICE;

public class WifiScanResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            List<ScanResult> wifiScanResults = wifiManager.getScanResults();
            Set<String> wifiSsidSet = new HashSet<>();

            for (ScanResult wifiScanResult : wifiScanResults) {
                wifiSsidSet.add(wifiScanResult.SSID);
            }

            String wifiListPrefKey = context.getResources().getString(R.string.pref_wifi_list);
            sharedPreferences.edit().putString(wifiListPrefKey, gson.toJson(wifiSsidSet)).apply();
        }
    }
}
