package com.demo.instagram_presentation.webserver.controller;

import android.content.res.AssetManager;

import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.demo.instagram_presentation.webserver.model.WebServerLoginInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.CookieHandler;

public class PasswordController {
    private Gson gson;
    private AssetManager assetManager;
    private ErrorController errorController;

    public PasswordController(AssetManager assetManager, ErrorController errorController) {
        gson = new Gson();
        this.assetManager = assetManager;
        this.errorController = errorController;
    }

    public NanoHTTPD.Response isUserAuthorized() {
        CookieHandler cookieHandler = NanoHttpdWebServer.cookieHandler;
        String password = cookieHandler.read("web-server-password");
        boolean isAuthorized = false;

        if (password != null && password.equals(Constants.WEB_SERVER_PASSWORD)) {
            isAuthorized = true;
        }

        Map<String, Boolean> returnedDataMap = new HashMap<>();
        returnedDataMap.put("isUserAuthorized", isAuthorized);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(returnedDataMap));
    }

    public NanoHTTPD.Response handleLoginPageRequest() {
        try {
            InputStream inputStream = assetManager.open("login.html");
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/html", inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return errorController.returnInternalServerError();
        }
    }

    public NanoHTTPD.Response login(String requestBodyData, String lastRequestedUri) {
        WebServerLoginInfo loginInfo = gson.fromJson(requestBodyData, WebServerLoginInfo.class);
        String password = loginInfo.getPassword();
        NanoHTTPD.Response response;
        CookieHandler cookieHandler = NanoHttpdWebServer.cookieHandler;

        if (lastRequestedUri == null) {
            lastRequestedUri = "/";
        }

        if (password.equals(Constants.WEB_SERVER_PASSWORD)) {
            cookieHandler.set("web-server-password", password, Constants.WEB_SERVER_PASSWORD_COOKIE_MAX_AGE);

            Map<String, String> returnedDataMap = new HashMap<>();
            returnedDataMap.put("lastRequestedUri", lastRequestedUri);

            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(returnedDataMap));
        } else {
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Wrong password");
        }

        cookieHandler.unloadQueue(response);
        return response;
    }
}
