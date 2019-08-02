package com.demo.instagram_presentation.webserver.router;

import android.content.Context;
import android.content.res.AssetManager;

import com.demo.instagram_presentation.webserver.controller.ErrorController;
import com.demo.instagram_presentation.webserver.controller.IndexController;
import com.demo.instagram_presentation.webserver.controller.PreferenceController;
import com.demo.instagram_presentation.webserver.util.StaticFileHandler;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

public class WebServerRouter {
    //TODO: refactor code

    private IndexController indexController;
    private PreferenceController preferenceController;
    private ErrorController errorController;
    private StaticFileHandler staticFileHandler;
    private AssetManager assetManager;

    public WebServerRouter(Context context) {
        assetManager = context.getResources().getAssets();

        errorController = new ErrorController();
        staticFileHandler = new StaticFileHandler(assetManager, errorController);

        indexController = new IndexController(assetManager, errorController);
        preferenceController = new PreferenceController(errorController, context);
    }

    public NanoHTTPD.Response routeRequest(NanoHTTPD.IHTTPSession session) {
        String requestUri = session.getUri();

        NanoHTTPD.Method requestMethod = session.getMethod();
        NanoHTTPD.Response response = null;

        switch (requestUri) {
            case "/":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = indexController.handleGet();
                }

                break;
            case "/preference":
                if (NanoHTTPD.Method.GET.equals(requestMethod)) {
                    response = preferenceController.handleGet();
                } else if (NanoHTTPD.Method.POST.equals(requestMethod)) {
                    response = preferenceController.handlePost(getRequestBodyDataAsJson(session));
                }
                break;
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
