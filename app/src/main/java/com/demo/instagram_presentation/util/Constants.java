package com.demo.instagram_presentation.util;

public class Constants {
    //Instagram URL constants
    public final static String INSTAGRAM_QUERY_ID = "17888483320059182";
    public final static String INSTAGRAM_API_URL_FORMAT =
            "https://instagram.com/graphql/query/?query_id=%s&variables={\"id\":\"%s\",\"first\":100,\"after\":null}";
    public final static String INSTAGRAM_IMAGE_TYPE_NAME = "GraphImage";

    //Config constants
    public final static int IMAGE_PRESENTATION_INTERVAL = 5000;
    public final static int DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY = 5;
    public final static double DEFAULT_POST_OVERLAY_OPACITY = 0.5;
}
