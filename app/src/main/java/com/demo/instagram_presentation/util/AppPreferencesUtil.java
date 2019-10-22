package com.demo.instagram_presentation.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;

import butterknife.BindString;

public class AppPreferencesUtil {
    private static String imgMainWidthPrefKey;
    private static String imgMainHeightPrefKey;
    private static String instagramSourceUrlPrefKey;
    private static String instagramSourceTagsPrefKey;
    private static String requiredLoginPrefKey;
    private static String internetAvailablePrefKey;

    // This will be init when application is started - in MainActivity
    private static SharedPreferences sharedPreferences;

    public static void initSharedPreference(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        imgMainWidthPrefKey = context.getResources().getString(R.string.pref_img_main_width);
        imgMainHeightPrefKey = context.getResources().getString(R.string.pref_img_main_height);
        instagramSourceUrlPrefKey = context.getResources().getString(R.string.pref_instagram_source);
        instagramSourceTagsPrefKey = context.getResources().getString(R.string.pref_instagram_source_tags);
        requiredLoginPrefKey = context.getResources().getString(R.string.pref_required_login);
        internetAvailablePrefKey = context.getResources().getString(R.string.pref_internet_available);
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static void setDefaultImageSize(Activity activity) {
        if (sharedPreferences.getString(imgMainWidthPrefKey, null) == null) {
            int width = ScreenUtil.getScreenWidth();
            sharedPreferences.edit().putString(imgMainWidthPrefKey, String.valueOf(width)).apply();
        }

        if (sharedPreferences.getString(imgMainHeightPrefKey, null) == null) {
            int height = (int) (ScreenUtil.getScreenHeight() * 0.75); // Likely to be changed by user -> no need to extract as a constant
            sharedPreferences.edit().putString(imgMainHeightPrefKey, String.valueOf(height)).apply();
        }
    }

    public static boolean isAbleToDisplaySlideshow() {
        String instagramSourceUrl = sharedPreferences.getString(instagramSourceUrlPrefKey, null);
        String instagramSourceTags = sharedPreferences.getString(instagramSourceTagsPrefKey, null);
        boolean isRequiredLogin = sharedPreferences.getBoolean(requiredLoginPrefKey, false);
        boolean internetAvailable = sharedPreferences.getBoolean(internetAvailablePrefKey, true);

        return NetworkUtil.isWifiConnected() && internetAvailable
                && (instagramSourceUrl != null || instagramSourceTags != null)
                && !isRequiredLogin;
    }

    public static void setFlagNoInternet() {
        sharedPreferences.edit().putBoolean(internetAvailablePrefKey, false).apply();
    }

    public static void setFlagInternetAvailable() {
        sharedPreferences.edit().putBoolean(internetAvailablePrefKey, true).apply();
    }

    public static boolean isInternetAvailable() {
        return sharedPreferences.getBoolean(internetAvailablePrefKey, true);
    }
}
