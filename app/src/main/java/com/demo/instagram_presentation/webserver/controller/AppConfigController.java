package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.model.AppConfig;
import com.demo.instagram_presentation.webserver.util.RequestUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class AppConfigController {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context;

    // Preference keys
    private String instagramSourcePrefKey;
    private String instagramSourceTagsPrefKey;
    private String authenticatedPrefKey;
    private String postNoPrefKey;
    private String isLikesDisplayedPrefKey;
    private String isCommentsDisplayedPrefKey;
    private String isPostCaptionDisplayedPrefKey;
    private String isProfilePicDisplayedPrefKey;
    private String isUsernameDisplayedPrefKey;
    private String isNetworkStrengthDisplayedPrefKey;
    private String excludedHashtagsPrefKey;
    private String autoSizePrefKey;
    private String imgMainHeightPrefKey;
    private String imgMainWidthPrefKey;
    private String profilePicWidthPrefKey;
    private String profilePicHeightPrefKey;
    private String usernameTextSizePrefKey;
    private String likeTextSizePrefKey;
    private String commentTextSizePrefKey;
    private String captionTextSizePrefKey;
    private String presentIntervalPrefKey;
    private String refreshIntervalPrefKey;
    private String requiredLoginPrefKey;

    public AppConfigController(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
        this.context = context;

        getPreferenceKeys();
    }

    public NanoHTTPD.Response getPreferences() {
        // Data configs
        String instagramSourceUrl = sharedPreferences.getString(instagramSourcePrefKey, "");
        String instagramSourceTags = sharedPreferences.getString(instagramSourceTagsPrefKey, "");
        int numberOfPostsToDisplay = getIntValueFromPref(postNoPrefKey, Constants.DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY);
        boolean isLikesDisplayed = sharedPreferences.getBoolean(isLikesDisplayedPrefKey, true);
        boolean isCommentsDisplayed = sharedPreferences.getBoolean(isCommentsDisplayedPrefKey, true);
        boolean isCaptionDisplayed = sharedPreferences.getBoolean(isPostCaptionDisplayedPrefKey, true);
        boolean isProfilePicDisplayed = sharedPreferences.getBoolean(isProfilePicDisplayedPrefKey, true);
        boolean isUsernameDisplayed = sharedPreferences.getBoolean(isUsernameDisplayedPrefKey, true);
        boolean isNetworkStrengthDisplayed = sharedPreferences.getBoolean(isNetworkStrengthDisplayedPrefKey, false);
        String excludedHashtagsString = sharedPreferences.getString(excludedHashtagsPrefKey, "");

        // Size configs
        boolean autoSize = sharedPreferences.getBoolean(autoSizePrefKey, true);
        int profilePicWidth = getIntValueFromPref(profilePicWidthPrefKey, Constants.DEFAULT_PROFILE_PIC_WIDTH);
        int profilePicHeight = getIntValueFromPref(profilePicHeightPrefKey, Constants.DEFAULT_PROFILE_PIC_HEIGHT);
        int usernameTextSize = getIntValueFromPref(usernameTextSizePrefKey, Constants.DEFAULT_USERNAME_TEXT_SIZE);
        int imgMainWidth = getIntValueFromPref(imgMainWidthPrefKey, 0); //Width is initialized as screen's width in MainActivity
        int imgMainHeight = getIntValueFromPref(imgMainHeightPrefKey, 0); //Height is initialized as 3/4 of screen's width in MainActivity
        int likeTextSize = getIntValueFromPref(likeTextSizePrefKey, Constants.DEFAULT_LIKE_TEXT_SIZE);
        int commentTextSize = getIntValueFromPref(commentTextSizePrefKey, Constants.DEFAULT_COMMENT_TEXT_SIZE);
        int captionTextSize = getIntValueFromPref(captionTextSizePrefKey, Constants.DEFAULT_CAPTION_TEXT_SIZE);

        // Slideshow configs
        int presentInterval = getIntValueFromPref(presentIntervalPrefKey, Constants.DEFAULT_PRESENTATION_INTERVAL);
        int refreshInterval = getIntValueFromPref(refreshIntervalPrefKey, Constants.DEFAULT_REFRESH_INTERVAL);

        AppConfig appConfig = AppConfig.builder()
                //Data variables
                .instagramSourceUrl(instagramSourceUrl)
                .instagramSourceTags(instagramSourceTags)
                .numberOfPostsToDisplay(numberOfPostsToDisplay)
                .excludedHashtags(excludedHashtagsString)
                .isLikesDisplayed(isLikesDisplayed)
                .isCommentsDisplayed(isCommentsDisplayed)
                .isCaptionDisplayed(isCaptionDisplayed)
                .isProfilePicDisplayed(isProfilePicDisplayed)
                .isUsernameDisplayed(isUsernameDisplayed)
                .isNetworkStrengthDisplayed(isNetworkStrengthDisplayed)
                //Size variables
                .autoSize(autoSize)
                .profilePicWidth(profilePicWidth)
                .profilePicHeight(profilePicHeight)
                .usernameTextSize(usernameTextSize)
                .imgMainWidth(imgMainWidth)
                .imgMainHeight(imgMainHeight)
                .likeTextSize(likeTextSize)
                .commentTextSize(commentTextSize)
                .captionTextSize(captionTextSize)
                //Slideshow variables
                .presentInterval(presentInterval)
                .refreshInterval(refreshInterval)
                .build();

        String jsonResponse = gson.toJson(appConfig);

        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonResponse);
        // Allow CORS
        RequestUtil.addCorsHeadersToResponse(response);
        response.setChunkedTransfer(true);

        return response;
    }

    public NanoHTTPD.Response savePreferences(String requestBodyData) {
        try {
            boolean isRequiredLogin = sharedPreferences.getBoolean(requiredLoginPrefKey, false);
            if (isRequiredLogin) {
                Map<String, String> errorResponseMsg = new HashMap<>();
                errorResponseMsg.put("error", "Provide instagram login info before saving configs!");
                errorResponseMsg.put("redirect", "/authorize");

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(errorResponseMsg));
            } else {
                AppConfig appConfig = gson.fromJson(requestBodyData, AppConfig.class);

                SharedPreferences.Editor prefEditor = sharedPreferences.edit();

                prefEditor.putString(instagramSourcePrefKey, appConfig.getInstagramSourceUrl());
                prefEditor.putString(instagramSourceTagsPrefKey, appConfig.getInstagramSourceTags());
                prefEditor.putBoolean(authenticatedPrefKey, false);
                prefEditor.putString(postNoPrefKey, String.valueOf(appConfig.getNumberOfPostsToDisplay()));
                prefEditor.putString(excludedHashtagsPrefKey, appConfig.getExcludedHashtags());
                prefEditor.putBoolean(isLikesDisplayedPrefKey, appConfig.isLikesDisplayed());
                prefEditor.putBoolean(isCommentsDisplayedPrefKey, appConfig.isCommentsDisplayed());
                prefEditor.putBoolean(isPostCaptionDisplayedPrefKey, appConfig.isCaptionDisplayed());
                prefEditor.putBoolean(isProfilePicDisplayedPrefKey, appConfig.isProfilePicDisplayed());
                prefEditor.putBoolean(isUsernameDisplayedPrefKey, appConfig.isUsernameDisplayed());
                prefEditor.putBoolean(isNetworkStrengthDisplayedPrefKey, appConfig.isNetworkStrengthDisplayed());
                // Size prefs
                prefEditor.putBoolean(autoSizePrefKey, appConfig.isAutoSize());
                if (!appConfig.isAutoSize()) {
                    prefEditor.putString(imgMainHeightPrefKey, String.valueOf(appConfig.getImgMainHeight()));
                    prefEditor.putString(imgMainWidthPrefKey, String.valueOf(appConfig.getImgMainWidth()));
                    prefEditor.putString(profilePicWidthPrefKey, String.valueOf(appConfig.getProfilePicWidth()));
                    prefEditor.putString(profilePicHeightPrefKey, String.valueOf(appConfig.getProfilePicHeight()));
                    prefEditor.putString(usernameTextSizePrefKey, String.valueOf(appConfig.getUsernameTextSize()));
                    prefEditor.putString(likeTextSizePrefKey, String.valueOf(appConfig.getLikeTextSize()));
                    prefEditor.putString(commentTextSizePrefKey, String.valueOf(appConfig.getCommentTextSize()));
                    prefEditor.putString(captionTextSizePrefKey, String.valueOf(appConfig.getCaptionTextSize()));
                }
                prefEditor.putString(presentIntervalPrefKey, String.valueOf(appConfig.getPresentInterval()));
                prefEditor.putString(refreshIntervalPrefKey, String.valueOf(appConfig.getRefreshInterval()));

                prefEditor.apply();

                context.sendBroadcast(new Intent(Constants.PREFERENCE_CHANGED_ACTION));

                Map<String, String> savePreferencesResponseMsg = new HashMap<>();
                savePreferencesResponseMsg.put("success", "Configs has been saved!");

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(savePreferencesResponseMsg));
            }
        } catch (Exception e) {
            Map<String, String> savePreferencesResponseMsg = new HashMap<>();
            savePreferencesResponseMsg.put("error", "Some errors occurred. Cannot save app configs");

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(savePreferencesResponseMsg));
        }
    }

    private void getPreferenceKeys() {
        // Data pref keys
        instagramSourcePrefKey = context.getString(R.string.pref_instagram_source);
        instagramSourceTagsPrefKey = context.getString(R.string.pref_instagram_source_tags);
        authenticatedPrefKey = context.getString(R.string.pref_instagram_authenticated);
        postNoPrefKey = context.getString(R.string.pref_post_no);
        isLikesDisplayedPrefKey = context.getString(R.string.pref_is_post_likes_displayed);
        isCommentsDisplayedPrefKey = context.getString(R.string.pref_is_post_comments_displayed);
        isPostCaptionDisplayedPrefKey = context.getString(R.string.pref_is_post_caption_displayed);
        isProfilePicDisplayedPrefKey = context.getString(R.string.pref_is_profile_pic_displayed);
        isUsernameDisplayedPrefKey = context.getString(R.string.pref_is_username_displayed);
        isNetworkStrengthDisplayedPrefKey = context.getString(R.string.pref_is_network_strength_displayed);
        excludedHashtagsPrefKey = context.getString(R.string.pref_excluded_hashtags);
        requiredLoginPrefKey = context.getString(R.string.pref_required_login);
        // Size pref keys
        autoSizePrefKey = context.getString(R.string.pref_auto_size);
        imgMainHeightPrefKey = context.getString(R.string.pref_img_main_height);
        imgMainWidthPrefKey = context.getString(R.string.pref_img_main_width);
        profilePicWidthPrefKey = context.getString(R.string.pref_profile_pic_width);
        profilePicHeightPrefKey = context.getString(R.string.pref_profile_pic_height);
        usernameTextSizePrefKey = context.getString(R.string.pref_username_text_size);
        likeTextSizePrefKey = context.getString(R.string.pref_like_text_size);
        commentTextSizePrefKey = context.getString(R.string.pref_comment_text_size);
        captionTextSizePrefKey = context.getString(R.string.pref_caption_text_size);
        presentIntervalPrefKey = context.getString(R.string.pref_present_interval);
        refreshIntervalPrefKey = context.getString(R.string.pref_refresh_interval);
    }

    private int getIntValueFromPref(String key, int defaultValue) {
        return Integer.parseInt(sharedPreferences.getString(key,
                String.valueOf(defaultValue)));
    }
}
