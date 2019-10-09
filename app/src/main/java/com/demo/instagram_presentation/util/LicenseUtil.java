package com.demo.instagram_presentation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class LicenseUtil {
    private static boolean isValidLicense = false;

    public static boolean isKeyIdFileInitialized() {
        File file = new File(DeviceUtil.getDataPath(), Constants.LICENSE_ID_FILENAME);

        return file.exists();
    }

    public static boolean isKeyFileExisted() {
        File file = new File(DeviceUtil.getDataPath(), Constants.LICENSE_KEY_FILENAME);

        return file.exists();
    }

    public static void initKeyIdFile() {
        File licenseFile = new File(DeviceUtil.getDataPath(), Constants.LICENSE_ID_FILENAME);

        int keyId = ThreadLocalRandom.current().nextInt(Constants.BASE_KEY_SEED_MINIMUM_VALUE, Constants.BASE_KEY_SEED_MAXIMUM_VALUE);

        try (FileWriter fileWriter = new FileWriter(licenseFile)) {
            fileWriter.append(String.valueOf(keyId));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeKeyFile(String licenseKey) {
        File licenseFile = new File(DeviceUtil.getDataPath(), Constants.LICENSE_KEY_FILENAME);

        try (FileWriter fileWriter = new FileWriter(licenseFile)) {

            fileWriter.append(licenseKey);
            fileWriter.flush();
            isValidLicense = true;
        } catch (IOException e) {
            e.printStackTrace();
            isValidLicense = false;
        }
    }

    public static int readKeyIdFromFile() {
        File licenseFile = new File(DeviceUtil.getDataPath(), Constants.LICENSE_ID_FILENAME);

        try (FileInputStream fis = new FileInputStream(licenseFile)) {

            byte[] data = new byte[(int) licenseFile.length()];
            fis.read(data);

            return Integer.parseInt(new String(data, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    public static boolean validateKey(int keyId, int key) {
        int correctKey = generateKeyFromKeyId(keyId);

        return correctKey == key;
    }

    public static boolean validateKeyFiles() {
        if (isKeyFileExisted()) {
            if (!isValidLicense) {
                int keyId = readKeyIdFromFile();
                int key = readKeyFromFile();

                isValidLicense = validateKey(keyId, key);
            }
        }
        return isValidLicense;
    }

    private static int generateKeyFromKeyId(int keyId) {
        keyId *= Constants.KEY_MULTIPLICATION_FACTOR;
        keyId += Constants.KEY_ADDITION_FACTOR;

        return keyId;
    }

    private static int readKeyFromFile() {
        File licenseFile = new File(DeviceUtil.getDataPath(), Constants.LICENSE_KEY_FILENAME);

        try (FileInputStream fis = new FileInputStream(licenseFile)){
            byte[] data = new byte[(int) licenseFile.length()];
            fis.read(data);

            return Integer.parseInt(new String(data, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }
}
