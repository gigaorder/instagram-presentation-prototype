package com.demo.instagram_presentation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstagramPost implements Comparable<InstagramPost> {
    private int likesCount;
    private int commentsCount;
    private String caption;
    private String imgUrl;
    private int index;


    @Override
    public int compareTo(InstagramPost instagramPost) {
        return Integer.compare(index, instagramPost.getIndex());
    }

    @Override
    public String toString() {
        return "" + index;
    }
}
