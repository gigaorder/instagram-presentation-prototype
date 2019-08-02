package com.demo.instagram_presentation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class InstagramFeedData {
    @SerializedName("count")
    private int postCount;

    @SerializedName("page_info")
    private PageInfo pageInfo;

    @SerializedName("edges")
    private List<InstagramPostNode> postNodes;

    private List<InstagramPost> posts;

    public List<InstagramPost> getPosts() {
        List<InstagramPost> posts = new ArrayList<>();

        for (InstagramPostNode postNode : postNodes) {
            posts.add(postNode.getPost());
        }

        return posts;
    }


    // Schemas for parsing nest JSON
    @Data
    private class PageInfo {
        private boolean hasNextPage;
        private String endCursor;
    }

    @Data
    private class InstagramPostNode {
        @SerializedName("node")
        private InstagramPost post;
    }
}
