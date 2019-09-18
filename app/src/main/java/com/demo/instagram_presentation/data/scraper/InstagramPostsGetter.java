package com.demo.instagram_presentation.data.scraper;

import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.text.StringEscapeUtils;

public class InstagramPostsGetter {
    private WebView webView;
    private String pageUrlToFetch;

    public InstagramPostsGetter(String pageUrlToFetch, WebView webView) {
        this.webView = webView;
        this.pageUrlToFetch = pageUrlToFetch;
    }

    public void fetchPage(FetchPageListener fetchPageListener) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String webViewUrl) {
                if (webViewUrl.equals(pageUrlToFetch)) {
                    fetchPageListener.onFinish(false, true);
                } else if (webViewUrl.equals("about:blank")) {
                    fetchPageListener.onFinish(false, false);
                } else {
                    fetchPageListener.onFinish(true, false);
                }
            }
        });

        webView.loadUrl(pageUrlToFetch);
    }

    public void getHtmlContent(GetPostsListener getPostsListener, int delay) {

        new Handler().postDelayed(() -> {
                    String scrollToBottomAndGetBodyContent = "(function() {window.scrollTo(0,document.body.scrollHeight);return document.getElementsByTagName('body')[0].innerHTML;})();";
                    webView.evaluateJavascript(
                            scrollToBottomAndGetBodyContent,
                            html -> {
                                html = StringEscapeUtils.unescapeJava(html);
                                getPostsListener.onFinish(html);
                            });
                }
                , delay);
    }

    public interface FetchPageListener {
        void onFinish(boolean redirected, boolean startGettingPost);
    }

    public interface GetPostsListener {
        void onFinish(String html);
    }
}
