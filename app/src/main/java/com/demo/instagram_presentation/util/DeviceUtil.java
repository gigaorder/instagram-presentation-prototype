package com.demo.instagram_presentation.util;

import android.os.Environment;

import com.demo.instagram_presentation.activity.MainActivity;

public class DeviceUtil {
    private static String dataPath;
    public static String getDataPath() {
        if (dataPath == null || dataPath.isEmpty()) {
            dataPath = PermissionUtil.hasStoragePermissions() ?
                    Environment.getExternalStorageDirectory().getAbsolutePath() :
                    MainActivity.self.getFilesDir().getAbsolutePath();
        }
        return dataPath;
    }
}
