package com.demo.instagram_presentation.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class InstagramUser {
    private String id;
    @SerializedName("profile_pic_url")
    private String profilePicUrl;
    @SerializedName("full_name")
    private String fullName;
}
