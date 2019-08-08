package com.demo.instagram_presentation.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class LicenseUtil {
    public static boolean isKeyIdFileInitialized(Context context) {
        File file = new File(context.getFilesDir(), Constants.LICENSE_ID_FILENAME);

        return file.exists();
    }

    public static void initKeyIdFile(Context context) {
        File licenseFile = new File(context.getFilesDir(), Constants.LICENSE_ID_FILENAME);

        int keyId = ThreadLocalRandom.current().nextInt(Constants.BASE_KEY_SEED_MINIMUM_VALUE, Constants.BASE_KEY_SEED_MAXIMUM_VALUE);

        try {
            FileWriter fileWriter = new FileWriter(licenseFile);
            fileWriter.append(String.valueOf(keyId));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeKeyFile(Context context, String licenseKey) {
        File licenseFile = new File(context.getFilesDir(), Constants.LICENSE_KEY_FILENAME);

        try {
            FileWriter fileWriter = new FileWriter(licenseFile);
            fileWriter.append(licenseKey);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int readKeyIdFromFile(Context context) {
        File licenseFile = new File(context.getFilesDir(), Constants.LICENSE_ID_FILENAME);

        try {
            FileInputStream fis = new FileInputStream(licenseFile);
            byte[] data = new byte[(int) licenseFile.length()];
            fis.read(data);
            fis.close();

            return Integer.parseInt(new String(data, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    public static boolean validateKey(int keyId, int key) {
        int correctKey = generateKeyFromKeyId(keyId);

        if (correctKey == key) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateKeyFiles(Context context) {
        int keyId = readKeyIdFromFile(context);
        int key = readKeyFromFile(context);

        return validateKey(keyId, key);
    }

    private static int generateKeyFromKeyId(int keyId) {
        keyId *= Constants.KEY_MULTIPLICATION_FACTOR;
        keyId += Constants.KEY_ADDITION_FACTOR;

        return keyId;
    }

    private static int readKeyFromFile(Context context) {
        File licenseFile = new File(context.getFilesDir(), Constants.LICENSE_KEY_FILENAME);

        try {
            FileInputStream fis = new FileInputStream(licenseFile);
            byte[] data = new byte[(int) licenseFile.length()];
            fis.read(data);
            fis.close();

            return Integer.parseInt(new String(data, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }
}
