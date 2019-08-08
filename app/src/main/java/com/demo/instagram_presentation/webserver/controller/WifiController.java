package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.demo.instagram_presentation.webserver.model.NetworkLoginInfo;
import com.demo.instagram_presentation.webserver.util.RequestUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WifiController {
    private SharedPreferences sharedPreferences;
    private AssetManager assetManager;
    private ErrorController errorController;
    private Context context;
    private Gson gson;

    public WifiController(AssetManager assetManager, ErrorController errorController, Context context) {
        this.assetManager = assetManager;
        this.errorController = errorController;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public NanoHTTPD.Response handlePageRequest() {
        try {
            InputStream inputStream = assetManager.open("wifi.html");
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/html", inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return errorController.returnInternalServerError();
        }
    }

    public NanoHTTPD.Response getWifiList() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        String wifiListPrefKey = context.getResources().getString(R.string.pref_wifi_list);
        String wifiList = sharedPreferences.getString(wifiListPrefKey, "");

        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", wifiList);
        RequestUtil.addCorsHeadersToResponse(response);
        response.setChunkedTransfer(true);

        return response;
    }

    public NanoHTTPD.Response connectToWifi(String requestBodyData) {
        NetworkLoginInfo networkLoginInfo = gson.fromJson(requestBodyData, NetworkLoginInfo.class);

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkLoginInfo.getSsid() + "\"";
        conf.preSharedKey = "\"" + networkLoginInfo.getPassphrase() + "\"";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> networkConfigs = wifiManager.getConfiguredNetworks();
        boolean connectResult = false;

        for (WifiConfiguration networkConfig : networkConfigs) {
            if (networkConfig.SSID != null && networkConfig.SSID.equals("\"" + networkLoginInfo.getSsid() + "\"")) {
                try {
                    wifiManager.enableNetwork(networkConfig.networkId, true);
                    connectResult = wifiManager.reconnect();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Map<String, Boolean> dataMap = new HashMap<>();
        dataMap.put("result", connectResult);
        String result = gson.toJson(dataMap);

        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", result);
        RequestUtil.addCorsHeadersToResponse(response);
        response.setChunkedTransfer(true);

        if (connectResult) {
            String wifiListPrefKey = context.getResources().getString(R.string.pref_wifi_list);
            sharedPreferences.edit().putBoolean(wifiListPrefKey, true).apply();
            context.sendBroadcast(new Intent(Constants.WIFI_CONNECTED_ACTION));
        }

        return response;
    }
}
