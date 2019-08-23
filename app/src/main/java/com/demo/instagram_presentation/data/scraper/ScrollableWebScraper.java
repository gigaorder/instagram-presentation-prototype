package com.demo.instagram_presentation.data.scraper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.text.StringEscapeUtils;

/**
 * This requires the WebView to be invisible or visible
 */
public class ScrollableWebScraper {
    private WebView webView;
    private String urlToScrape;
    private boolean scrollStarted = false;
    private HtmlExtractionListener htmlExtractionListener;
    private Handler handler;

    public ScrollableWebScraper(WebView webView, String urlToScrape) {
        this.webView = webView;
        this.urlToScrape = urlToScrape;
        handler = new Handler();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void start() {
        scrollStarted = false;
        webView.getSettings().setJavaScriptEnabled(true);
        loadUrl();
    }

    public void setHtmlExtractionListener(HtmlExtractionListener htmlExtractionListener) {
        this.htmlExtractionListener = htmlExtractionListener;
    }

    public void scrollToBottom() {
        webView.evaluateJavascript(
                "(function() {window.scrollTo(0,document.body.scrollHeight);return document.getElementsByTagName('body')[0].innerHTML;})();",
                html -> {
                    html = StringEscapeUtils.unescapeJava(html);

                    if (htmlExtractionListener != null) {
                        htmlExtractionListener.onHtmlExtracted(html);
                    }
                });
    }

    public void loadUrl() {
        webView.loadUrl(urlToScrape);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (!scrollStarted) {
                    scrollToBottom();
                    scrollStarted = true;
                }
            }
        });
    }

    public void scrollToBottomWithDelay(int ms) {
        handler.postDelayed(this::scrollToBottom, ms);
    }

    public interface HtmlExtractionListener {
        void onHtmlExtracted(String html);
    }
}
