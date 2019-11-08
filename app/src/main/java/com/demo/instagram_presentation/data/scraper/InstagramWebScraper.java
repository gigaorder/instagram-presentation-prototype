package com.demo.instagram_presentation.data.scraper;

import android.annotation.SuppressLint;
import android.webkit.WebView;

import lombok.Setter;

public class InstagramWebScraper {
    private WebView webView;
    private InstagramLogin instagramLogin;
    private InstagramPostsGetter instagramPostsGetter;

    @Setter
    private InstagramLoginListener instagramLoginListener;

    @Setter
    private HtmlExtractionListener htmlExtractionListener;

    public InstagramWebScraper(WebView webView, String username, String password, String pageUrlToFetch) {
        this.webView = webView;
        this.instagramLogin = new InstagramLogin(username, password, webView);
        this.instagramPostsGetter = new InstagramPostsGetter(pageUrlToFetch, webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void configWebView() {
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    public void startScrapingInstagram() {
        instagramPostsGetter.fetchPage((redirected, startGettingPost) -> {
            if (redirected) {
                instagramLogin.executeLoginInstagram((loginStatus) -> {
                    if (loginStatus.success) {
                        instagramLoginListener.onFinish(true, loginStatus.code, "");
                    } else {
                        instagramLoginListener.onFinish(false, loginStatus.code, loginStatus.message);
                    }
                });
            } else if (startGettingPost) {
                instagramPostsGetter.getHtmlContent(listUrl -> htmlExtractionListener.onHtmlExtracted(listUrl), 0);
            }
        });
    }

    public void continueGettingPosts(int delay) {
        instagramPostsGetter.getHtmlContent(listUrl -> htmlExtractionListener.onHtmlExtracted(listUrl), delay);
    }

    public void submitSecurityCode(String code) {
        instagramLogin.executeSubmitSecurityCodeWithJSCode(code);
    }

    public void getNewSecurityCode() {
        instagramLogin.getNewSecurityCode();
    }

    public interface InstagramLoginListener {
        void onFinish(boolean success, int loginCode, String loginErrorMessage);
    }

    public interface HtmlExtractionListener {
        void onHtmlExtracted(String[] listUrl);
    }
}
