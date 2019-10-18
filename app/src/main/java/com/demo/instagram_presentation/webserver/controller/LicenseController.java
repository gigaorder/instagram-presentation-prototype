package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;

import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.webserver.model.LicenseKeyInfo;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class LicenseController {
    private Gson gson;
    private Context context;

    public LicenseController(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public NanoHTTPD.Response getLicenseId() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", String.valueOf(LicenseUtil.readKeyIdFromFile()));
    }

    public NanoHTTPD.Response validateLicenseKey(String requestBodyData) {
        LicenseKeyInfo licenseKeyInfo = gson.fromJson(requestBodyData, LicenseKeyInfo.class);
        NanoHTTPD.Response response;

        int keyId = LicenseUtil.readKeyIdFromFile();
        int key = licenseKeyInfo.getLicenseKey();

        if (LicenseUtil.validateKey(keyId, key)) {
            LicenseUtil.writeKeyFile(String.valueOf(key));
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "Success"); //Just need the response status
        } else {
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Invalid key");
        }

        return response;
    }

    public NanoHTTPD.Response isDeviceValidated() {
        Map<String, Boolean> returnedDataMap = new HashMap<>();
        returnedDataMap.put("validated", LicenseUtil.isKeyFileExisted());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(returnedDataMap));
    }
}