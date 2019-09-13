package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.demo.instagram_presentation.webserver.model.InstagramAccount;
import com.demo.instagram_presentation.webserver.model.AuthorizationInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.CookieHandler;

public class AuthorizationController {
    private Gson gson;
    private AssetManager assetManager;
    private ErrorController errorController;
    private SharedPreferences sharedPreferences;
    private String requiredLoginPrefKey;
    private String instagramUsernamePrefKey;
    private String instagramPasswordPrefKey;
    private Context context;

    public AuthorizationController(AssetManager assetManager, ErrorController errorController, Context context) {
        gson = new Gson();
        this.assetManager = assetManager;
        this.errorController = errorController;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        requiredLoginPrefKey = context.getResources().getString(R.string.pref_required_login);
        instagramUsernamePrefKey = context.getResources().getString(R.string.pref_instagram_username);
        instagramPasswordPrefKey = context.getResources().getString(R.string.pref_instagram_password);
    }

    public NanoHTTPD.Response isUserAuthorized() {
        CookieHandler cookieHandler = NanoHttpdWebServer.cookieHandler;
        String password = cookieHandler.read("web-server-password");
        boolean isAuthorized = false;

        if (password != null && password.equals(Constants.AUTHORIZATION_KEY)) {
            isAuthorized = true;
        }

        Map<String, Boolean> returnedDataMap = new HashMap<>();
        returnedDataMap.put("isUserAuthorized", isAuthorized);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(returnedDataMap));
    }

    public NanoHTTPD.Response handleLoginPageRequest() {
        try {
            InputStream inputStream = assetManager.open("authorize.html");
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/html", inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return errorController.returnInternalServerError();
        }
    }

    public NanoHTTPD.Response authorize(String requestBodyData, String lastRequestedUri) {
        AuthorizationInfo authorizationInfo = gson.fromJson(requestBodyData, AuthorizationInfo.class);
        String authorizationKey = authorizationInfo.getAuthorizationKey();
        NanoHTTPD.Response response;
        CookieHandler cookieHandler = NanoHttpdWebServer.cookieHandler;

        if (lastRequestedUri == null) {
            lastRequestedUri = "/";
        }

        if (authorizationKey.equals(Constants.AUTHORIZATION_KEY)) {
            cookieHandler.set("authorization-key", authorizationKey, Constants.AUTHORIZATION_KEY_COOKIE_MAX_AGE);

            Map<String, String> returnedDataMap = new HashMap<>();
            returnedDataMap.put("lastRequestedUri", lastRequestedUri);

            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(returnedDataMap));
        } else {
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Wrong password");
        }

        cookieHandler.unloadQueue(response);
        return response;
    }

    public NanoHTTPD.Response isRequiredInstagramLogin() {
        Map<String, Boolean> isRequiredInstagramLoginMsg = new HashMap<>();
        isRequiredInstagramLoginMsg.put("isRequiredLogin", sharedPreferences.getBoolean(requiredLoginPrefKey, false));

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(isRequiredInstagramLoginMsg));
    }

    public NanoHTTPD.Response saveInstagramLoginInfo(String requestBodyData, String lastRequestedUri) {
        try {
            InstagramAccount instagramAccount = gson.fromJson(requestBodyData, InstagramAccount.class);

            SharedPreferences.Editor prefEditor = sharedPreferences.edit();
            prefEditor.putString(instagramUsernamePrefKey, instagramAccount.getUsername());
            prefEditor.putString(instagramPasswordPrefKey, instagramAccount.getPassword());
            prefEditor.putBoolean(requiredLoginPrefKey, false);
            prefEditor.apply();

            if (lastRequestedUri == null) {
                lastRequestedUri = "/";
            }

            context.sendBroadcast(new Intent(Constants.LOGIN_INFO_CHANGED_ACTION));

            Map<String, String> saveLoginInfoResponseMsg = new HashMap<>();
            saveLoginInfoResponseMsg.put("success", "Login info has been saved!");
            saveLoginInfoResponseMsg.put("lastRequestUri", lastRequestedUri);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(saveLoginInfoResponseMsg));
        } catch (Exception e) {
            Map<String, String> saveLoginInfoResponseMsg = new HashMap<>();
            saveLoginInfoResponseMsg.put("error", "Some errors occured. Cannot save login info!");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(saveLoginInfoResponseMsg));
        }
    }
}
