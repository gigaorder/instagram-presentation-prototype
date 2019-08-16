package com.demo.instagram_presentation.webserver.router;

import android.content.Context;
import android.content.res.AssetManager;

import com.demo.instagram_presentation.webserver.controller.ErrorController;
import com.demo.instagram_presentation.webserver.controller.IndexController;
import com.demo.instagram_presentation.webserver.controller.LicenseController;
import com.demo.instagram_presentation.webserver.controller.PasswordController;
import com.demo.instagram_presentation.webserver.controller.PreferenceController;
import com.demo.instagram_presentation.webserver.controller.WifiController;
import com.demo.instagram_presentation.webserver.util.StaticFileHandler;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

public class WebServerRouter {
    //TODO: refactor code
    private IndexController indexController;
    private PreferenceController preferenceController;
    private WifiController wifiController;
    private ErrorController errorController;
    private PasswordController passwordController;
    private LicenseController licenseController;
    private StaticFileHandler staticFileHandler;
    private AssetManager assetManager;
    private String lastRequestedUri;

    public WebServerRouter(Context context) {
        assetManager = context.getResources().getAssets();

        errorController = new ErrorController();
        staticFileHandler = new StaticFileHandler(assetManager, errorController);

        indexController = new IndexController(assetManager, errorController);
        preferenceController = new PreferenceController(context);
        wifiController = new WifiController(assetManager, errorController, context);
        passwordController = new PasswordController(assetManager, errorController);
        licenseController = new LicenseController(context);
    }

    public NanoHTTPD.Response routeRequest(NanoHTTPD.IHTTPSession session) {
        String requestUri = session.getUri();

        NanoHTTPD.Method requestMethod = session.getMethod();
        NanoHTTPD.Response response = null;

        switch (requestUri) {
            // Page requests
            case "/":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = indexController.handlePageRequest();
                    lastRequestedUri = requestUri;
                }
                break;
            case "/wifi":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = wifiController.handlePageRequest();
                    lastRequestedUri = requestUri;
                }
                break;
            case "/login":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = passwordController.handleLoginPageRequest();
                }
                break;
            // API requests
            case "/api/v1/preference":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = preferenceController.getPreferences();
                } else if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = preferenceController.savePreferences(getRequestBodyDataAsJson(session));
                }
                break;
            case "/api/v1/wifi":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = wifiController.getWifiList();
                }
                break;
            case "/api/v1/wifi/connect":
                if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = wifiController.connectToWifi(getRequestBodyDataAsJson(session));
                }
                break;
            case "/api/v1/is-authorized":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = passwordController.isUserAuthorized();
                }
                break;
            case "/api/v1/login":
                if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = passwordController.login(getRequestBodyDataAsJson(session), lastRequestedUri);
                }
                break;
            case "/api/v1/license":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = licenseController.getLicenseId();
                }
                break;
            case "/api/v1/license/validate":
                if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = licenseController.validateLicenseKey(getRequestBodyDataAsJson(session));
                }
                break;
            case "/api/v1/license/is-validated":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = licenseController.isDeviceValidated();
                }
                break;
            // Other cases and static files requests
            default:
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = staticFileHandler.handleResourceFileRequest(requestUri);
                }
                break;
        }

        return response;
    }

    private String getRequestBodyDataAsJson(NanoHTTPD.IHTTPSession session) {
        final HashMap<String, String> map = new HashMap<>();
        try {
            session.parseBody(map);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NanoHTTPD.ResponseException e) {
            e.printStackTrace();
        }
        return map.get("postData");
    }
}
