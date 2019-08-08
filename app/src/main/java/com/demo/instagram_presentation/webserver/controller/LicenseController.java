package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;

import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.webserver.model.LicenseKeyInfo;
import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;

public class LicenseController {
    private Gson gson;
    private Context context;

    public LicenseController(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public NanoHTTPD.Response getLicenseId() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", String.valueOf(LicenseUtil.readKeyIdFromFile(context)));
    }

    public NanoHTTPD.Response validateLicenseKey(String requestBodyData) {
        LicenseKeyInfo licenseKeyInfo = gson.fromJson(requestBodyData, LicenseKeyInfo.class);
        NanoHTTPD.Response response;

        int keyId = LicenseUtil.readKeyIdFromFile(context);
        int key = licenseKeyInfo.getLicenseKey();

        if (LicenseUtil.validateKey(keyId, key)) {
            LicenseUtil.writeKeyFile(context, String.valueOf(key));
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "Success"); //Just need the response status
        } else {
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Invalid key");
        }

        return response;
    }
}