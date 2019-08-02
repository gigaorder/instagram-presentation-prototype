package com.demo.instagram_presentation.util;

public class InstagramUtil {
    public static String contructFeedRequestUrl(String userId) {
        return String.format(Constants.INSTAGRAM_API_URL_FORMAT, Constants.INSTAGRAM_QUERY_ID, userId);
    }

    public static String constructInstagramUserInfoUrl(String instagramSourceUrl) {
        if (instagramSourceUrl.endsWith("/")) {
            instagramSourceUrl += "?__a=1";
        } else {
            instagramSourceUrl += "/?__a=1";
        }

        return instagramSourceUrl;
    }
}
