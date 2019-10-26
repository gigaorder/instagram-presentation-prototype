package com.demo.instagram_presentation.util;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.demo.instagram_presentation.fragment.ConfigFragment;
import com.demo.instagram_presentation.fragment.ImageSlideFragment;

public class FragmentUtil {
    public static void showImageSlideFragment(int fragmentId, FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(fragmentId, new ImageSlideFragment())
                .commit();
    }

    public static void showConfigFragment(int fragmentId, FragmentActivity activity, boolean configServerStarted) {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(fragmentId, new ConfigFragment(configServerStarted))
                .commit();
    }
}
