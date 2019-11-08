package com.demo.instagram_presentation.model;

import org.jsoup.nodes.Element;

import java.util.Objects;

import lombok.Data;

/**
 * Wrapper class for Jsoup Element, this is used for storing Instagram posts in a LinkedHashSet
 * <p>
 * While scraping data from Instagram with WebView, the max number of posts in HTML document at
 * any time is 45 -> need to store the posts if user needs more than 45 posts, LinkedHashSet is used
 * for storing the posts without duplication. the post href is used as the hashCode
 */
@Data
public class InstagramPostElement {
    private String href;
    private boolean requested;

    public InstagramPostElement(String href) {
        this.href = href;
        this.requested = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstagramPostElement that = (InstagramPostElement) o;
        return Objects.equals(this.href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href);
    }
}
