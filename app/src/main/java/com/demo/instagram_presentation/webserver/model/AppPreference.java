package com.demo.instagram_presentation.webserver.model;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppPreference {
    private String instagramSourceUrl;
    private String excludedHashtags;
    private int numberOfPostsToDisplay;
    private boolean isProfilePicDisplayed;
    private boolean isUsernameDisplayed;
    private boolean isLikesDisplayed;
    private boolean isCommentsDisplayed;
    private boolean isDescriptionDisplayed;
}
