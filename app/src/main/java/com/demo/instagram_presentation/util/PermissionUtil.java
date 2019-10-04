package com.demo.instagram_presentation.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class PermissionUtil {
    private static String [] permissions = {
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE
    };

    public static void askForRequiredPermissions() {
        List<String> requestPermissions = new ArrayList<>();
        for (String permission: permissions) {
            if (!hasPermission(permission)) {
                requestPermissions.add(permission);
            }
        }
        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.self, requestPermissions.toArray(new String[0]), 0);
        }
    }

    public static boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(InstagramApplicationContext.context.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasStoragePermissions() {
        final int readStorageGranted = ContextCompat.checkSelfPermission(InstagramApplicationContext.context.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        final int writeStorageGranted = ContextCompat.checkSelfPermission(InstagramApplicationContext.context.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readStorageGranted == PackageManager.PERMISSION_GRANTED && writeStorageGranted == PackageManager.PERMISSION_GRANTED;
    }
}
