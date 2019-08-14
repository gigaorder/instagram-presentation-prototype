package com.demo.instagram_presentation.data.scraper;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.commons.text.StringEscapeUtils;

/**
 * This requires the WebView to be invisible or visible
 */
public class ScrollableWebScraper {
    private WebView webView;
    private int numberOfPostsToGet;
    private String urlToScrape;
    private boolean scrollStarted = false;
    private Handler handler;
    private HtmlExtractionListener htmlExtractionListener;

    public ScrollableWebScraper(WebView webView, String urlToScrape) {
        this.webView = webView;
        this.urlToScrape = urlToScrape;
        handler = new Handler();
    }

    public void start() {
        webView.getSettings().setJavaScriptEnabled(true);
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

    public void setHtmlExtractionListener(HtmlExtractionListener htmlExtractionListener) {
        this.htmlExtractionListener = htmlExtractionListener;
    }

    public void scrollToBottom() {
        handler.postDelayed(() -> {
            webView.evaluateJavascript(
                    "(function() {window.scrollTo(0,document.body.scrollHeight);return document.getElementsByTagName('body')[0].innerHTML;})();",
                    html -> {
                        html = StringEscapeUtils.unescapeJava(html);

                        if (htmlExtractionListener != null) {
                            htmlExtractionListener.onHtmlExtracted(html);
                        }
                    });
        }, 200);
    }

    public interface HtmlExtractionListener {
        void onHtmlExtracted(String html);
    }
}
