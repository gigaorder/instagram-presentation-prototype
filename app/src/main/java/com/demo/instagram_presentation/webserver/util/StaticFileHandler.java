package com.demo.instagram_presentation.webserver.util;

import android.content.res.AssetManager;

import com.demo.instagram_presentation.webserver.controller.ErrorController;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class StaticFileHandler {
    private AssetManager assetManager;
    private ErrorController errorController;

    public StaticFileHandler(AssetManager assetManager, ErrorController errorController) {
        this.assetManager = assetManager;
        this.errorController = errorController;
    }

    public NanoHTTPD.Response handleResourceFileRequest(String requestUri) {
        try {
            if (requestUri.substring(0, 1).equals("/")) {
                requestUri = requestUri.substring(1);
            }

            InputStream inputStream = assetManager.open(requestUri);

            if (requestUri.endsWith(".ico")) {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "image/x-icon", inputStream);
            } else if (requestUri.endsWith(".png") || requestUri.endsWith(".PNG")) {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "image/png", inputStream);
            } else if (requestUri.endsWith(".jpg") || requestUri.endsWith(".JPG") || requestUri.endsWith(".jpeg") || requestUri.endsWith(".JPEG")) {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "image/jpeg", inputStream);
            } else if (requestUri.endsWith(".js")) {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "application/javascript", inputStream);
            } else if (requestUri.endsWith(".css")) {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/css", inputStream);
            } else if (requestUri.endsWith(".map")) {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "application/json", inputStream);
            } else {
                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/plain", inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return errorController.returnInternalServerError();
        }
    }
}
