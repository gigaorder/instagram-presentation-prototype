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
                    String scrollToBottomAndGetBodyContent = "(function() {\n" +
                            "    window.scrollTo(0,document.body.scrollHeight);\n" +
                            "    var posts = Array.from(document.getElementsByTagName('body')[0].querySelectorAll('article a'));\n" +
                            "    var result = [];\n" +
                            "    for (var i = 0; i < posts.length; i++) {\n" +
                            "       result.push(posts[i].getAttribute('href'));\n" +
                            "    }\n" +
                            "    return result.toString();\n" +
                            "})();";
                    webView.evaluateJavascript(
                            scrollToBottomAndGetBodyContent,
                            urls -> {
                                urls = StringEscapeUtils.unescapeJava(urls);
                                urls = urls.replaceAll("\"", "");
                                if (urls.isEmpty()) return;
                                String[] listUrl = urls.split(",");
                                getPostsListener.onFinish(listUrl);
                            });
                }
                , delay);
    }

    protected interface FetchPageListener {
        void onFinish(boolean redirected, boolean startGettingPost);
    }

    protected interface GetPostsListener {
        void onFinish(String[] listUrl);
    }
}
