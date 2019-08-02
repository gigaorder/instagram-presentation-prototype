package com.demo.instagram_presentation.webserver.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.webserver.NanoHttpdWebServer;
import com.demo.instagram_presentation.webserver.model.AppPreference;
import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;

public class PreferenceController {
    // TODO: refactor code + extract hard-coded pref keys

    private ErrorController errorController;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context;

    public PreferenceController(ErrorController errorController, Context context) {
        this.errorController = errorController;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
        this.context = context;
    }

    public NanoHTTPD.Response handleGet() {
        String instagramSourceUrl = sharedPreferences.getString("instagram_source", null);
        int numberOfPostsToDisplay = Integer.parseInt(sharedPreferences.getString("post_no",
                String.valueOf(Constants.DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY)));
        boolean isLikesDisplayed = sharedPreferences.getBoolean("is_post_likes_displayed", true);
        boolean isCommentsDisplayed = sharedPreferences.getBoolean("is_post_comments_displayed", true);
        boolean isDescriptionDisplayed = sharedPreferences.getBoolean("is_post_description_displayed", true);
        boolean isProfilePicDisplayed = sharedPreferences.getBoolean("is_profile_pic_displayed", true);
        boolean isUsernameDisplayed = sharedPreferences.getBoolean("is_username_displayed", true);
        String excludedHashtagsString = sharedPreferences.getString("excluded_hashtags", null);

        AppPreference appPreference = AppPreference.builder()
                .instagramSourceUrl(instagramSourceUrl)
                .numberOfPostsToDisplay(numberOfPostsToDisplay)
                .excludedHashtags(excludedHashtagsString)
                .isLikesDisplayed(isLikesDisplayed)
                .isCommentsDisplayed(isCommentsDisplayed)
                .isDescriptionDisplayed(isDescriptionDisplayed)
                .isProfilePicDisplayed(isProfilePicDisplayed)
                .isUsernameDisplayed(isUsernameDisplayed)
                .build();

        String jsonResponse = gson.toJson(appPreference);

        NanoHTTPD.Response response = NanoHttpdWebServer.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonResponse);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Max-Age", "3628800");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
        response.addHeader("Access-Control-Allow-Headers", "Authorization");
        response.setChunkedTransfer(true);

        return response;
    }

    public NanoHTTPD.Response handlePost(String requestBodyData) {
        AppPreference appPreference = gson.fromJson(requestBodyData, AppPreference.class);

        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        prefEditor.putString("instagram_source", appPreference.getInstagramSourceUrl());
        prefEditor.putString("post_no", String.valueOf(appPreference.getNumberOfPostsToDisplay()));
        prefEditor.putString("excluded_hashtags", appPreference.getExcludedHashtags());
        prefEditor.putBoolean("is_post_likes_displayed", appPreference.isLikesDisplayed());
        prefEditor.putBoolean("is_post_comments_displayed", appPreference.isCommentsDisplayed());
        prefEditor.putBoolean("is_post_description_displayed", appPreference.isDescriptionDisplayed());
        prefEditor.putBoolean("is_profile_pic_displayed", appPreference.isProfilePicDisplayed());
        prefEditor.putBoolean("is_username_displayed", appPreference.isUsernameDisplayed());

        prefEditor.apply();

        context.sendBroadcast(new Intent(Constants.PREFERENCE_CHANGED_ACTION));

        return NanoHttpdWebServer.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "Saved successfully");
    }
}
