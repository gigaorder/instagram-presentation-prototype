package com.demo.instagram_presentation.webserver.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppConfig {
    // Data variables
    private String instagramSourceUrl;
    private String instagramSourceTags;
    private String instagramUsername;
    private String instagramPassword;
    private String excludedHashtags;
    private int numberOfPostsToDisplay;
    private boolean isProfilePicDisplayed;
    private boolean isUsernameDisplayed;
    private boolean isLikesDisplayed;
    private boolean isCommentsDisplayed;
    private boolean isCaptionDisplayed;
    private boolean isNetworkStrengthDisplayed;

    // Size variables
    private boolean autoSize;
    private int profilePicWidth;
    private int profilePicHeight;
    private int usernameTextSize;
    private int imgMainWidth;
    private int imgMainHeight;
    private int likeTextSize;
    private int commentTextSize;
    private int captionTextSize;

    // Slideshow variables
    private int presentInterval;
    private int refreshInterval;
}
