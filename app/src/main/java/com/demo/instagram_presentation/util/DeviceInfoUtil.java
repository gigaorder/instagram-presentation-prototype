package com.demo.instagram_presentation.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DeviceInfoUtil {
    public static String getDeviceId() {
        File file = new File(Environment.getDataDirectory(), Constants.DEVICE_ID_FILENAME);
        String deviceId = UUID.randomUUID().toString();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = new byte[(int) file.length()];
                fis.read(data);

                deviceId = new String(data, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.append(deviceId);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return deviceId;
    }
}
