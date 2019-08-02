package com.demo.instagram_presentation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class InstagramPost {
    private String id;

    @SerializedName("__typename")
    private String type;

    @SerializedName("display_url")
    private String displayUrl;

    @SerializedName("edge_media_preview_like")
    private LikeData likeData;

    @SerializedName("edge_media_to_comment")
    private CommentData commentData;

    @SerializedName("edge_media_to_caption")
    private DescriptionData descriptionData;

    private transient int numberOfLikes;

    private transient int numberOfComments;

    private transient String postDescription;

    public int getNumberOfLikes() {
        return likeData.getCount();
    }

    public int getNumberOfComments() {
        return commentData.getCount();
    }

    public String getPostDescription() {
        if (descriptionData.getEdges().isEmpty()) {
            return "";
        }

        return descriptionData.getEdges().get(0).getNode().getText();
    }

    // Schemas for parsing nested JSON
    @Data
    private class LikeData {
        private int count;
    }

    @Data
    private class CommentData {
        private int count;
    }

    @Data
    private class DescriptionData {
        private List<Edge> edges;

        @Data
        private class Edge {
            private Node node;

            @Data
            private class Node {
                private String text;
            }
        }
    }
}
