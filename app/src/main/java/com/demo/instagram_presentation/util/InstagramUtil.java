package com.demo.instagram_presentation.util;

public class InstagramUtil {
    public static String constructInstagramUserInfoUrl(String instagramSourceUrl) {
        if (instagramSourceUrl.endsWith("/")) {
            instagramSourceUrl += "?__a=1";
        } else {
            instagramSourceUrl += "/?__a=1";
        }

        return instagramSourceUrl;
    }
}
