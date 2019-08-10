package com.demo.instagram_presentation.util;

public class Constants {
    //Instagram URL constants
    public static final String INSTAGRAM_QUERY_ID = "f2405b236d85e8296cf30347c9f08c2a";
    //    public static final String INSTAGRAM_API_URL_FORMAT =
//            "https://instagram.com/graphql/query/?query_hash=%s&variables={\"id\":\"%s\",\"first\":100,\"after\":null}";
    public static final String INSTAGRAM_API_URL_FORMAT = "https://instagram.com/%s/?__a=1";
    public static final String INSTAGRAM_IMAGE_TYPE_NAME = "GraphImage";

    //Config constants
    public static final int DEFAULT_PRESENTATION_INTERVAL = 10000;
    public static final int DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY = 5;
    //Size constants
    public static final int DEFAULT_PROFILE_PIC_WIDTH = 100;
    public static final int DEFAULT_PROFILE_PIC_HEIGHT = 100;
    public static final int DEFAULT_USERNAME_TEXT_SIZE = 40;
    public static final int DEFAULT_LIKE_TEXT_SIZE = 30;
    public static final int DEFAULT_COMMENT_TEXT_SIZE = 30;
    public static final int DEFAULT_DESCRIPTION_TEXT_SIZE = 30;

    //Web server configs
    public static final int WEB_SERVER_PORT = 8888;
    public static final String WEB_SERVER_PASSWORD = "rockiton";
    public static final int WEB_SERVER_PASSWORD_COOKIE_MAX_AGE = 365; // in days

    //Broadcasts constants
    public static final String PREFERENCE_CHANGED_ACTION = "PREFERENCE_CHANGED";
    public static final String WIFI_CONNECTED_ACTION = "WIFI_CONNECTED_ACTION";

    //License key constants
    public static final int BASE_KEY_SEED_MINIMUM_VALUE = 100;
    public static final int BASE_KEY_SEED_MAXIMUM_VALUE = 10000;
    public static final int KEY_MULTIPLICATION_FACTOR = 12;
    public static final int KEY_ADDITION_FACTOR = 1234;
    public static final String LICENSE_ID_FILENAME = "key-id.txt";
    public static final String LICENSE_KEY_FILENAME = "key.txt";

    //View visibility hiding/showing
    public static final int HIDE_SERVER_INFO_ON_WIFI_DELAY = 120 * 1000; //in ms
    public static final int HIDE_SERVER_INFO_ON_WIFI_DIRECT_DELAY = 300 * 1000; //in ms
}
