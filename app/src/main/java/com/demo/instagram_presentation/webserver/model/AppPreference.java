package com.demo.instagram_presentation.webserver.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppPreference {
    // Data variables
    private String instagramSourceUrl;
    private String excludedHashtags;
    private int numberOfPostsToDisplay;
    private boolean isProfilePicDisplayed;
    private boolean isUsernameDisplayed;
    private boolean isLikesDisplayed;
    private boolean isCommentsDisplayed;
    private boolean isDescriptionDisplayed;

    // Size variables
    private int profilePicWidth;
    private int profilePicHeight;
    private int usernameTextSize;
    private int imgMainWidth;
    private int imgMainHeight;
    private int likeTextSize;
    private int commentTextSize;
    private int descriptionTextSize;
    private int presentInterval;
}
