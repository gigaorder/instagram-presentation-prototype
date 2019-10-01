package com.demo.instagram_presentation.util;

import android.os.Environment;

public class Constants {
    //Config constants
    public static final int DEFAULT_FEED_REQUEST_RETRY_INTERVAL = 10 * 1000; // in milliseconds
    public static final int DEFAULT_PRESENTATION_INTERVAL = 10 * 1000; // in milliseconds
    public static final int DEFAULT_REFRESH_INTERVAL = 60; // in minutes
    public static final int DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY = 5;

    //Size constants
    public static final int DEFAULT_PROFILE_PIC_WIDTH = 100;
    public static final int DEFAULT_PROFILE_PIC_HEIGHT = 100;
    public static final int DEFAULT_USERNAME_TEXT_SIZE = 40;
    public static final int DEFAULT_LIKE_TEXT_SIZE = 30;
    public static final int DEFAULT_COMMENT_TEXT_SIZE = 30;
    public static final int DEFAULT_CAPTION_TEXT_SIZE = 30;

    //Web server configs
    public static final int WEB_SERVER_PORT = 8888;
    public static final String AUTHORIZATION_KEY = "rockiton";
    public static final int AUTHORIZATION_KEY_COOKIE_MAX_AGE = 365; // in days
    public static final int NETWORK_STATUS_CHECK_DELAY = 15 * 1000;

    //Broadcasts constants
    public static final String PREFERENCE_CHANGED_ACTION = "PREFERENCE_CHANGED";
    public static final String LOGIN_INFO_CHANGED_ACTION = "LOGIN_INFO_CHANGED";
    public static final String LOGIN_FAILED_ACTION = "LOGIN_FAILED";

    //License key constants
    public static final int BASE_KEY_SEED_MINIMUM_VALUE = 100;
    public static final int BASE_KEY_SEED_MAXIMUM_VALUE = 10000;
    public static final int KEY_MULTIPLICATION_FACTOR = 12;
    public static final int KEY_ADDITION_FACTOR = 1234;
    public static final String LICENSE_ID_FILENAME = "feed2wall-key-id.txt";
    public static final String LICENSE_KEY_FILENAME = "feed2wall-key.txt";
    public static final String DEVICE_ID_FILENAME = "feed2wall-device-id.txt";

    //View visibility hiding/showing
    public static final int HIDE_SERVER_INFO_ON_WIFI_DELAY = 120 * 1000; //in ms
    public static final int HIDE_SERVER_INFO_ON_WIFI_DIRECT_DELAY = 300 * 1000; //in ms

    //webView scraper configs
    public static final int FIRST_SCROLL_DELAY = 1000; //ms
    public static final int NEXT_SCROLLS_DELAY = 5 * 1000; //ms
    public static final int SCROLL_TIMEOUT = 60 * 1000; //ms
    public static final int SCROLL_COUNT_LIMIT = SCROLL_TIMEOUT / NEXT_SCROLLS_DELAY;
    public static final int INFINITE_SCROLL_POST_COUNT = 6 * 12;

    //Bugfender constants
    public static final String BUGFENDER_APP_TOKEN = "m8oZhIAO7JVUQGSTbVPB9yyvz3WxJJoa";

    //Network config constants
    public static final int NETWORK_SIGNAL_SCAN_INTERVAL = 10 * 1000; //ms

    // Instagram web constants
    public static final String BASE_URL = "https://www.instagram.com/";
    public static final String LOGIN_URL = BASE_URL + "accounts/login/";
    public static final String LOGINPAGE_TITLE = "Login • Instagram";
    public static final String HOMEPAGE_TITLE = "Instagram";

    public static final String APK_NAME = "patch_signed_7zip.apk";
    public static final String FIREBASE_TOPIC = "instagramPatching";
}
