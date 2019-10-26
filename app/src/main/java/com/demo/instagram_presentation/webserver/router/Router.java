package com.demo.instagram_presentation.webserver.router;

import android.content.Context;
import android.content.res.AssetManager;

import com.demo.instagram_presentation.webserver.controller.ErrorController;
import com.demo.instagram_presentation.webserver.controller.IndexController;
import com.demo.instagram_presentation.webserver.controller.LicenseController;
import com.demo.instagram_presentation.webserver.controller.AuthorizationController;
import com.demo.instagram_presentation.webserver.controller.AppConfigController;
import com.demo.instagram_presentation.webserver.controller.WifiController;
import com.demo.instagram_presentation.webserver.util.StaticFileHandler;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

public class Router {
    private IndexController indexController;
    private AppConfigController preferenceController;
    private WifiController wifiController;
    private ErrorController errorController;
    private AuthorizationController authorizationController;
    private LicenseController licenseController;
    private StaticFileHandler staticFileHandler;
    private AssetManager assetManager;
    private String lastRequestedUri;

    public Router(Context context) {
        assetManager = context.getResources().getAssets();

        errorController = new ErrorController();
        staticFileHandler = new StaticFileHandler(assetManager, errorController);

        indexController = new IndexController(assetManager, errorController);
        preferenceController = new AppConfigController(context);
        wifiController = new WifiController(assetManager, errorController, context);
        authorizationController = new AuthorizationController(assetManager, errorController, context);
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
            case "/authorize":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = authorizationController.handleLoginPageRequest();
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
                    response = authorizationController.isUserAuthorized();
                }
                break;
            case "/api/v1/authorize":
                if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = authorizationController.authorize(getRequestBodyDataAsJson(session), lastRequestedUri);
                }
                break;
            case "/api/v1/is-required-login":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = authorizationController.isRequiredInstagramLogin();
                }
                break;
            case "/api/v1/save-instagram-login-info":
                if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = authorizationController.saveInstagramLoginInfo(getRequestBodyDataAsJson(session), lastRequestedUri);
                }
                break;
            case "/api/v1/is-required-security-code":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = authorizationController.isRequiredLoginSecurityCode();
                }
                break;

            case "/api/v1/submit-security-code":
                if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = authorizationController.submitSecurityCode(getRequestBodyDataAsJson(session), lastRequestedUri);
                }
                break;
            case "/api/v1/get-new-code":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = authorizationController.getNewSecurityCode();
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
