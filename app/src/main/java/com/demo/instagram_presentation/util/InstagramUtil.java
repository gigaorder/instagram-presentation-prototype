package com.demo.instagram_presentation.util;

import com.demo.instagram_presentation.model.InstagramPost;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class InstagramUtil {
    private static JsonParser jsonParser;

    public static String constructInstagramUserInfoUrl(String instagramSourceUrl) {
        if (instagramSourceUrl.endsWith("/")) {
            instagramSourceUrl += "?__a=1";
        } else {
            instagramSourceUrl += "/?__a=1";
        }

        return instagramSourceUrl;
    }

    public static String constructInstagramHashtagQuery(String hashtag) {
        return "https://www.instagram.com/explore/tags/" + hashtag.substring(1);
    }

    public static InstagramPost parseInstagramPostHtml(String html, int postIndex, String postHref) {
        if (jsonParser == null) {
            jsonParser = new JsonParser();
        }

        Document doc = Jsoup.parse(html);
        String dataScript = doc.select("body > script:containsData(_sharedData =)").html();

        if (dataScript.isEmpty()) {
            dataScript = doc.select("body > script").get(0).html();
        }

        String jsonString = dataScript.split("_sharedData =")[1].trim();
        if (jsonString.endsWith(";")) {
            jsonString = jsonString.substring(0, jsonString.length() - 1);
        }

        JsonObject postInfo = jsonParser.parse(jsonString)
                .getAsJsonObject().get("entry_data")
                .getAsJsonObject().get("PostPage")
                .getAsJsonArray().get(0)
                .getAsJsonObject().get("graphql")
                .getAsJsonObject().get("shortcode_media")
                .getAsJsonObject();

        //Get likes
        int likes = postInfo.get("edge_media_preview_like")
                .getAsJsonObject().get("count")
                .getAsInt();

        //Get comments
        int comments = postInfo.get("edge_media_preview_comment") != null
                ? postInfo.get("edge_media_preview_comment")
                .getAsJsonObject().get("count")
                .getAsInt()
                : postInfo.get("edge_media_to_comment")
                .getAsJsonObject().get("count")
                .getAsInt();

        //Get caption
        String caption;
        try {
            caption = postInfo.get("edge_media_to_caption")
                    .getAsJsonObject().get("edges")
                    .getAsJsonArray().get(0)
                    .getAsJsonObject().get("node")
                    .getAsJsonObject().get("text")
                    .getAsString();
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            caption = null;
        }

        //Get imgSrcSet, this is the set of images used for different screen sizes
        JsonArray imgSrcSet = jsonParser.parse(jsonString)
                .getAsJsonObject().get("entry_data")
                .getAsJsonObject().get("PostPage")
                .getAsJsonArray().get(0)
                .getAsJsonObject().get("graphql")
                .getAsJsonObject().get("shortcode_media")
                .getAsJsonObject().get("display_resources")
                .getAsJsonArray();

        //Get imgUrl - image with the highest resolution (last img in srcSet)
        String imgSrc = imgSrcSet.get(imgSrcSet.size() - 1)
                .getAsJsonObject().get("src")
                .getAsString();

        //Get username
        String username = postInfo.get("owner")
                .getAsJsonObject().get("username")
                .getAsString();

        //Get profile pic
        String userProfilePic = postInfo.get("owner")
                .getAsJsonObject().get("profile_pic_url")
                .getAsString();

        return new InstagramPost(likes, comments, caption, imgSrc, postIndex, postHref, username, userProfilePic);
    }
}
