package com.demo.instagram_presentation.webserver.controller;

import fi.iki.elonen.NanoHTTPD;

public class ErrorController {
    public NanoHTTPD.Response returnInternalServerError() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Internal server error");
    }

    public NanoHTTPD.Response returnNotFound() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "Not found");
    }
}
