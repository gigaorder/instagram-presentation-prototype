package com.demo.instagram_presentation.webserver.controller;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class IndexController {
    private static final String INDEX_FILENAME = "index.html";
    private AssetManager assetManager;
    private ErrorController errorController;

    public IndexController(AssetManager assetManager, ErrorController errorController) {
        this.assetManager = assetManager;
        this.errorController = errorController;
    }

    public NanoHTTPD.Response handlePageRequest() {
        try {
            InputStream inputStream = assetManager.open(INDEX_FILENAME);
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/html", inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return errorController.returnInternalServerError();
        }
    }
}
