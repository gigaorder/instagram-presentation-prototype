package com.demo.instagram_presentation.hotfix_plugin;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import lombok.Setter;

public class PermissionUtil {
    @Setter
    private static Activity activity;

    public static void askForStoragePermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (!hasStoragePermissions()) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    public static boolean hasStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 16) {
            final int readStorageGranted = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            final int writeStorageGranted = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readStorageGranted == PackageManager.PERMISSION_GRANTED && writeStorageGranted == PackageManager.PERMISSION_GRANTED;
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            final int res = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return res == PackageManager.PERMISSION_GRANTED;
        }
    }
}
