package com.demo.instagram_presentation.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

public class BroadcastReceiverUtil {
    public static void unregisterReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        try {
            context.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            // No need to handle anything as the receiver is not registered <- what we need
            Log.e("Error", "Tried to unregister a receiver that has not been registered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
