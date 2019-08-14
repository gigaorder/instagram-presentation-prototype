package com.demo.instagram_presentation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstagramPost {
    private int likesCount;
    private int commentsCount;
    private String caption;
    private String imgUrl;
}
