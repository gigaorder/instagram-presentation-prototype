package com.demo.instagram_presentation.model;

import java.util.Objects;

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
    private String postHref;
    private String username;
    private String userProfilePicUrl;

    @Override
    public int compareTo(InstagramPost instagramPost) {
        return Integer.compare(index, instagramPost.getIndex());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstagramPost that = (InstagramPost) o;
        return Objects.equals(this.imgUrl, that.imgUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imgUrl);
    }

    @Override
    public String toString() {
        return "" + index;
    }
}
