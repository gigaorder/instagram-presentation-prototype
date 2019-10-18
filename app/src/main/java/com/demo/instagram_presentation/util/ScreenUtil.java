package com.demo.instagram_presentation.util;

import android.util.DisplayMetrics;

import com.demo.instagram_presentation.activity.MainActivity;

public class ScreenUtil {
    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        MainActivity.self.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        MainActivity.self.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
