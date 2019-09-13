package com.demo.instagram_presentation.webserver;

import android.content.Context;

import com.demo.instagram_presentation.webserver.router.Router;

import fi.iki.elonen.NanoHTTPD;

/**
 * This class is used for creating embedded web server inside Android application
 * The web server is used for manipulating preferences for the app remotely via a browser
 */
public class NanoHttpdWebServer extends NanoHTTPD {
    private Router router;
    public static CookieHandler cookieHandler;

    public NanoHttpdWebServer(Context context, int port) {
        super(port);
        router = new Router(context);
    }

    @Override
    public Response serve(IHTTPSession session) {
        cookieHandler = new CookieHandler(session.getHeaders());
        return router.routeRequest(session);
    }
}
