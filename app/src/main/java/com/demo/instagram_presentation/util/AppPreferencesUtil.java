package com.demo.instagram_presentation.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;

public class AppPreferencesUtil {
    private static String imgMainWidthPrefKey;
    private static String imgMainHeightPrefKey;

    // This will be init when application is started - in MainActivity
    private static SharedPreferences sharedPreferences;

    public static void initSharedPreference(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        imgMainWidthPrefKey = context.getResources().getString(R.string.pref_img_main_width);
        imgMainHeightPrefKey = context.getResources().getString(R.string.pref_img_main_height);
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
}
