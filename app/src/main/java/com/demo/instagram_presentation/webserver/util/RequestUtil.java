package com.demo.instagram_presentation.webserver.util;

import fi.iki.elonen.NanoHTTPD;

public class RequestUtil {
    public static void addCorsHeadersToResponse(NanoHTTPD.Response response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Max-Age", "3628800");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
        response.addHeader("Access-Control-Allow-Headers", "Authorization");
    }
}
