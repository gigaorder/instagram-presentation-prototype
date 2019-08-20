package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.model.AppPreference;
import com.demo.instagram_presentation.webserver.util.RequestUtil;
import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;

public class PreferenceController {
    // TODO: refactor code + extract hard-coded pref keys

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context;

    // Preference keys
    private String instagramSourcePrefKey;
    private String postNoPrefKey;
    private String isLikesDisplayedPrefKey;
    private String isCommentsDisplayedPrefKey;
    private String isPostCaptionDisplayedPrefKey;
    private String isProfilePicDisplayedPrefKey;
    private String isUsernameDisplayPrefKey;
    private String excludedHashtagsPrefKey;
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

    public PreferenceController(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
        this.context = context;

        getPreferenceKeys();
    }

    public NanoHTTPD.Response getPreferences() {
        // Data configs
        String instagramSourceUrl = sharedPreferences.getString(instagramSourcePrefKey, null);
        int numberOfPostsToDisplay = getIntValueFromPref(postNoPrefKey, Constants.DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY);
        boolean isLikesDisplayed = sharedPreferences.getBoolean(isLikesDisplayedPrefKey, true);
        boolean isCommentsDisplayed = sharedPreferences.getBoolean(isCommentsDisplayedPrefKey, true);
        boolean isCaptionDisplayed = sharedPreferences.getBoolean(isPostCaptionDisplayedPrefKey, true);
        boolean isProfilePicDisplayed = sharedPreferences.getBoolean(isProfilePicDisplayedPrefKey, true);
        boolean isUsernameDisplayed = sharedPreferences.getBoolean(isUsernameDisplayPrefKey, true);
        String excludedHashtagsString = sharedPreferences.getString(excludedHashtagsPrefKey, null);

        // Size configs
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

        AppPreference appPreference = AppPreference.builder()
                //Data variables
                .instagramSourceUrl(instagramSourceUrl)
                .numberOfPostsToDisplay(numberOfPostsToDisplay)
                .excludedHashtags(excludedHashtagsString)
                .isLikesDisplayed(isLikesDisplayed)
                .isCommentsDisplayed(isCommentsDisplayed)
                .isCaptionDisplayed(isCaptionDisplayed)
                .isProfilePicDisplayed(isProfilePicDisplayed)
                .isUsernameDisplayed(isUsernameDisplayed)
                //Size variables
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

        String jsonResponse = gson.toJson(appPreference);

        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonResponse);
        // Allow CORS
        RequestUtil.addCorsHeadersToResponse(response);
        response.setChunkedTransfer(true);

        return response;
    }

    public NanoHTTPD.Response savePreferences(String requestBodyData) {
        AppPreference appPreference = gson.fromJson(requestBodyData, AppPreference.class);

        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putString(instagramSourcePrefKey, appPreference.getInstagramSourceUrl());
        prefEditor.putString(postNoPrefKey, String.valueOf(appPreference.getNumberOfPostsToDisplay()));
        prefEditor.putString(excludedHashtagsPrefKey, appPreference.getExcludedHashtags());
        prefEditor.putBoolean(isLikesDisplayedPrefKey, appPreference.isLikesDisplayed());
        prefEditor.putBoolean(isCommentsDisplayedPrefKey, appPreference.isCommentsDisplayed());
        prefEditor.putBoolean(isPostCaptionDisplayedPrefKey, appPreference.isCaptionDisplayed());
        prefEditor.putBoolean(isProfilePicDisplayedPrefKey, appPreference.isProfilePicDisplayed());
        prefEditor.putBoolean(isUsernameDisplayPrefKey, appPreference.isUsernameDisplayed());
        // Size prefs
        prefEditor.putString(imgMainHeightPrefKey, String.valueOf(appPreference.getImgMainHeight()));
        prefEditor.putString(imgMainWidthPrefKey, String.valueOf(appPreference.getImgMainWidth()));
        prefEditor.putString(profilePicWidthPrefKey, String.valueOf(appPreference.getProfilePicWidth()));
        prefEditor.putString(profilePicHeightPrefKey, String.valueOf(appPreference.getProfilePicHeight()));
        prefEditor.putString(usernameTextSizePrefKey, String.valueOf(appPreference.getUsernameTextSize()));
        prefEditor.putString(likeTextSizePrefKey, String.valueOf(appPreference.getLikeTextSize()));
        prefEditor.putString(commentTextSizePrefKey, String.valueOf(appPreference.getCommentTextSize()));
        prefEditor.putString(captionTextSizePrefKey, String.valueOf(appPreference.getCaptionTextSize()));
        prefEditor.putString(presentIntervalPrefKey, String.valueOf(appPreference.getPresentInterval()));
        prefEditor.putString(refreshIntervalPrefKey, String.valueOf(appPreference.getRefreshInterval()));

        prefEditor.apply();

        context.sendBroadcast(new Intent(Constants.PREFERENCE_CHANGED_ACTION));

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "Saved successfully");
    }

    private void getPreferenceKeys() {
        // Data pref keys
        instagramSourcePrefKey = context.getResources().getString(R.string.pref_instagram_source);
        postNoPrefKey = context.getResources().getString(R.string.pref_post_no);
        isLikesDisplayedPrefKey = context.getResources().getString(R.string.pref_is_post_likes_displayed);
        isCommentsDisplayedPrefKey = context.getResources().getString(R.string.pref_is_post_comments_displayed);
        isPostCaptionDisplayedPrefKey = context.getResources().getString(R.string.pref_is_post_caption_displayed);
        isProfilePicDisplayedPrefKey = context.getResources().getString(R.string.pref_is_profile_pic_displayed);
        isUsernameDisplayPrefKey = context.getResources().getString(R.string.pref_is_username_displayed);
        excludedHashtagsPrefKey = context.getResources().getString(R.string.pref_excluded_hashtags);
        // Size pref keys
        imgMainHeightPrefKey = context.getResources().getString(R.string.pref_img_main_height);
        imgMainWidthPrefKey = context.getResources().getString(R.string.pref_img_main_width);
        profilePicWidthPrefKey = context.getResources().getString(R.string.pref_profile_pic_width);
        profilePicHeightPrefKey = context.getResources().getString(R.string.pref_profile_pic_height);
        usernameTextSizePrefKey = context.getResources().getString(R.string.pref_username_text_size);
        likeTextSizePrefKey = context.getResources().getString(R.string.pref_like_text_size);
        commentTextSizePrefKey = context.getResources().getString(R.string.pref_comment_text_size);
        captionTextSizePrefKey = context.getResources().getString(R.string.pref_caption_text_size);
        presentIntervalPrefKey = context.getResources().getString(R.string.pref_present_interval);
        refreshIntervalPrefKey = context.getResources().getString(R.string.pref_refresh_interval);
    }

    private int getIntValueFromPref(String key, int defaultValue) {
        return Integer.parseInt(sharedPreferences.getString(key,
                String.valueOf(defaultValue)));
    }
}
