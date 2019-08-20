package com.demo.instagram_presentation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.data.scraper.ScrollableWebScraper;
import com.demo.instagram_presentation.model.InstagramPost;
import com.demo.instagram_presentation.model.InstagramPostElement;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.InstagramUtil;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageSlideFragment extends Fragment {
    @BindView(R.id.fragment_present_imgMain)
    ImageView imgMain;
    @BindView(R.id.fragment_present_txtError)
    TextView txtError;
    @BindView(R.id.fragment_present_imgProfile)
    ImageView imgProfile;
    @BindView(R.id.fragment_present_txtUsername)
    TextView txtUsername;
    @BindView(R.id.fragment_present_txtNumberOfLikes)
    TextView txtNumberOfLikes;
    @BindView(R.id.fragment_present_txtNumberOfComments)
    TextView txtNumberOfComments;
    @BindView(R.id.fragment_present_txtPostCaption)
    TextView txtPostCaption;
    @BindView(R.id.fragment_present_layout_user_section)
    View userInfoSection;
    @BindView(R.id.fragment_present_progressBar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.fragment_present_txtProgress)
    TextView txtProgress;
    @BindView(R.id.fragment_present_imgWatermark)
    ImageView imgWatermark;
    @BindView(R.id.fragment_present_txtServerInfo)
    TextView txtServerInfo;
    @BindView(R.id.fragment_present_txtTimer)
    TextView txtTimer;
    @BindView(R.id.fragment_present_webView)
    WebView webView;

    @BindString(R.string.source_url_not_set)
    String errorSourceUrlNotSet;
    @BindString(R.string.invalid_source_url)
    String errorInvalidSourceUrl;
    @BindString(R.string.feed_request_error)
    String errorFeedRequestFailed;
    @BindString(R.string.progress_getting_user_info)
    String progressGettingFeedData;
    @BindString(R.string.progress_done)
    String progressDone;
    @BindString(R.string.pref_instagram_source)
    String instagramSourcePrefKey;
    @BindString(R.string.pref_post_no)
    String postNoPrefKey;
    @BindString(R.string.pref_is_post_likes_displayed)
    String isLikesDisplayedPrefKey;
    @BindString(R.string.pref_is_post_comments_displayed)
    String isCommentsDisplayedPrefKey;
    @BindString(R.string.pref_is_post_caption_displayed)
    String isPostCaptionDisplayedPrefKey;
    @BindString(R.string.pref_is_profile_pic_displayed)
    String isProfilePicDisplayedPrefKey;
    @BindString(R.string.pref_is_username_displayed)
    String isUsernameDisplayPrefKey;
    @BindString(R.string.pref_excluded_hashtags)
    String excludedHashtagsPrefKey;
    @BindString(R.string.pref_img_main_height)
    String imgMainHeightPrefKey;
    @BindString(R.string.pref_img_main_width)
    String imgMainWidthPrefKey;
    @BindString(R.string.pref_profile_pic_width)
    String profilePicWidthPrefKey;
    @BindString(R.string.pref_profile_pic_height)
    String profilePicHeightPrefKey;
    @BindString(R.string.pref_username_text_size)
    String usernameTextSizePrefKey;
    @BindString(R.string.pref_like_text_size)
    String likeTextSizePrefKey;
    @BindString(R.string.pref_comment_text_size)
    String commentTextSizePrefKey;
    @BindString(R.string.pref_caption_text_size)
    String captionTextSizePrefKey;
    @BindString(R.string.pref_present_interval)
    String presentIntervalPrefKey;
    @BindString(R.string.pref_refresh_interval)
    String refreshIntervalPrefKey;
    @BindString(R.string.source_url_error)
    String sourceUrlError;
    @BindString(R.string.timer_msg_server)
    String timerMessageForServer;
    @BindString(R.string.timer_msg_retry)
    String timerMessageForRetry;

    private RequestQueue requestQueue;
    private Runnable imagePresentationLoader;
    private final Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private JsonParser jsonParser;

    // Configs
    private List<String> excludedHashtags;
    private String instagramSourceUrl;
    private int numberOfPostsToDisplay;
    private int profilePicWidth;
    private int profilePicHeight;
    private int usernameTextSize;
    private int imgMainWidth;
    private int imgMainHeight;
    private int likeTextSize;
    private int commentTextSize;
    private int captionTextSize;
    private int presentInterval;
    private int refreshInterval;
    private boolean isLikesDisplayed;
    private boolean isCommentsDisplayed;
    private boolean isCaptionDisplayed;
    private boolean isProfilePicDisplayed;
    private boolean isUsernameDisplayed;
    private ScrollableWebScraper scrollableWebScraper;
    private Set<InstagramPostElement> postElementSet;
    private List<InstagramPost> instagramPosts;
    private List<InstagramPost> newInstagramPosts;
    private boolean maxNumberOfPostsReached;
    private boolean slideStarted;
    private int lastNumberOfPosts;
    private int startPostIndex;
    private int nextSlideIndex;

    /**
     * The number of posts user's Instagram feed has
     */
    private int feedMaxNumberOfPosts;

    /**
     * Count the number of times the webView scrolls but no new content is found
     * -> it can be due to network error or the webView has scrolled to the end
     * If the count exceeds a limit -> stop scrolling (scroll timeout)
     */
    private int scrollCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init tools
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        jsonParser = new JsonParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentRootView = inflater.inflate(R.layout.fragment_image_slide, container, false);
        ButterKnife.bind(this, fragmentRootView);

        webView.getSettings().setLoadsImagesAutomatically(false);
        setServerInfo();
        startConfigServerMsgTimer(timerMessageForServer, Constants.HIDE_SERVER_INFO_ON_WIFI_DELAY, txtTimer, true);
        getPreferences();
        initComponentsSize();
        showWatermark();

        // Hide components, show them after the images are loaded
        hideComponents();

        // If source URL is not set -> request user to go to settings screen to setup first
        if (instagramSourceUrl == null) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(errorSourceUrlNotSet);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestQueue.add(new StringRequest(instagramSourceUrl,
                            success -> {
                                instagramPosts = new ArrayList<>();
                                newInstagramPosts = new ArrayList<>();
                                nextSlideIndex = 0;

                                txtError.setVisibility(View.GONE);
                                txtProgress.setText(progressGettingFeedData);
                                progressBar.setProgress(0);
                                progressBar.setMax(numberOfPostsToDisplay);
                                txtProgress.setVisibility(View.VISIBLE);

                                // Start scraper to get posts of user
                                scrollableWebScraper = new ScrollableWebScraper(webView, instagramSourceUrl);
                                scrollableWebScraper.setHtmlExtractionListener(initialHtmlListener);

                                getUserInfo();
                                initScraperVariables();
                                scrollableWebScraper.start();

                                // Refresh task
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initScraperVariables();
                                        scrollableWebScraper.setHtmlExtractionListener(refreshHtmlListener);
                                        scrollableWebScraper.start();
                                        handler.postDelayed(this, refreshInterval * 60 * 1000); //refreshInterval is in minutes
                                    }
                                }, refreshInterval * 60 * 1000);
                            },
                            err -> {
                                if (err.networkResponse != null && err.networkResponse.statusCode == 404) {
                                    txtError.setVisibility(View.VISIBLE);
                                    txtError.setText(errorInvalidSourceUrl);
                                } else {
                                    // Retry
                                    handler.postDelayed(this, Constants.DEFAULT_FEED_REQUEST_RETRY_INTERVAL);
                                    startConfigServerMsgTimer(timerMessageForRetry, Constants.DEFAULT_FEED_REQUEST_RETRY_INTERVAL, txtError, false);
                                }
                            }));
                }
            });
        }

        return fragmentRootView;
    }

    private void getUserInfo() {
        String url = InstagramUtil.constructInstagramUserInfoUrl(instagramSourceUrl);

        requestQueue.add(new StringRequest(url,
                response -> {
                    JsonObject userInfo = jsonParser.parse(response)
                            .getAsJsonObject().get("graphql")
                            .getAsJsonObject().get("user")
                            .getAsJsonObject();

                    feedMaxNumberOfPosts = userInfo.get("edge_owner_to_timeline_media")
                            .getAsJsonObject().get("count")
                            .getAsInt();

                    txtUsername.setText(userInfo.get("username").getAsString());

                    Picasso.get()
                            .load(userInfo.get("profile_pic_url_hd").getAsString())
                            .fit()
                            .centerCrop()
                            .into(imgProfile);
                }, error -> Log.e("Error", error.toString())));
    }

    private void initScraperVariables() {
        postElementSet = new LinkedHashSet<>();
        maxNumberOfPostsReached = false;
        scrollCount = 0;
        lastNumberOfPosts = 0;

        if (slideStarted) {
            newInstagramPosts = new ArrayList<>();
        } else {
            instagramPosts = new ArrayList<>();
            startPostIndex = 0;
        }
    }

    private void onInstagramPostRequestSuccess(InstagramPost instagramPost) {
        // If post contains excluded hashtag -> skip
        if (checkExcludedHashtags(instagramPost.getCaption())) {
            shiftStartPostIndex(instagramPost.getIndex());
            return;
        }

        instagramPosts.add(instagramPost);

        if (maxNumberOfPostsReached) {
            txtProgress.setText(String.format(Locale.ENGLISH, "Retrieved %d/%d posts", instagramPosts.size(), numberOfPostsToDisplay));
            progressBar.setProgress(instagramPosts.size() + 1);
        }

        if (checkPostLimit(instagramPosts)) {
            hideProgress();
            cleanupWebView();
            maxNumberOfPostsReached = true;
        }

        // Sort posts using the order of the posts in HTML document
        Collections.sort(instagramPosts);

        // Start the slide when the first image is retrieved
        if (!slideStarted && !instagramPosts.isEmpty()
                && ((instagramPosts.get(0).getIndex() == startPostIndex) || maxNumberOfPostsReached)) {
            startImagePresentation(instagramPosts);
        }
    }

    private boolean checkPostLimit(List<InstagramPost> posts) {
        return posts.size() >= numberOfPostsToDisplay || posts.size() >= feedMaxNumberOfPosts;
    }

    private void startImagePresentation(List<InstagramPost> instagramPosts) {
        if (imagePresentationLoader != null) {
            handler.removeCallbacks(imagePresentationLoader);
        }

        imagePresentationLoader = new Runnable() {
            @Override
            public void run() {
                int index = getNextSlideIndex();

                if (index >= numberOfPostsToDisplay || index >= instagramPosts.size()) {
                    index = 0;
                }

                setNextSlideIndex(index + 1);
                InstagramPost post = instagramPosts.get(index);

                Picasso.get()
                        .load(post.getImgUrl())
                        .fit()
                        .centerCrop()
                        .into(imgMain);

                DecimalFormat numberFormatter = new DecimalFormat("#,###");
                String noOfLikes = numberFormatter.format(post.getLikesCount()) + " likes";
                String noOfComments = numberFormatter.format(post.getCommentsCount()) + " comments";

                txtNumberOfLikes.setText(noOfLikes);
                txtNumberOfComments.setText(noOfComments);
                txtPostCaption.setText(post.getCaption());

                showComponents();

                handler.postDelayed(this, presentInterval);
            }
        };

        handler.post(imagePresentationLoader);
        slideStarted = true;
    }

    public int getNextSlideIndex() {
        return nextSlideIndex++;
    }

    public void setNextSlideIndex(int nextSlideIndex) {
        this.nextSlideIndex = nextSlideIndex;
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
        progressBar.setProgress(0);
    }

    private void hideComponents() {
        imgProfile.setVisibility(View.INVISIBLE);
        txtUsername.setVisibility(View.GONE);
        imgMain.setVisibility(View.GONE);
        txtNumberOfLikes.setVisibility(View.GONE);
        txtNumberOfComments.setVisibility(View.GONE);
        txtPostCaption.setVisibility(View.GONE);

        if (!isUsernameDisplayed && !isProfilePicDisplayed) {
            userInfoSection.setVisibility(View.GONE);
        }
    }

    private void showComponents() {
        imgMain.setVisibility(View.VISIBLE);

        if (isUsernameDisplayed || isProfilePicDisplayed) {
            userInfoSection.setVisibility(View.VISIBLE);
        }

        if (isUsernameDisplayed) {
            txtUsername.setVisibility(View.VISIBLE);
        }

        if (isProfilePicDisplayed) {
            imgProfile.setVisibility(View.VISIBLE);
        }

        if (isLikesDisplayed) {
            txtNumberOfLikes.setVisibility(View.VISIBLE);
        }

        if (isCommentsDisplayed) {
            txtNumberOfComments.setVisibility(View.VISIBLE);
        }

        if (isCaptionDisplayed) {
            txtPostCaption.setVisibility(View.VISIBLE);
        }

        showWatermark();
    }

    private void showWatermark() {
        if (!LicenseUtil.validateKeyFiles()) {
            imgWatermark.setVisibility(View.VISIBLE);
        } else {
            imgWatermark.setVisibility(View.GONE);
        }
    }

    private void getPreferences() {
        // Data configs
        instagramSourceUrl = sharedPreferences.getString(instagramSourcePrefKey, null);
        numberOfPostsToDisplay = getIntValueFromPref(postNoPrefKey, Constants.DEFAULT_NUMBER_OF_POSTS_TO_DISPLAY);
        isLikesDisplayed = sharedPreferences.getBoolean(isLikesDisplayedPrefKey, true);
        isCommentsDisplayed = sharedPreferences.getBoolean(isCommentsDisplayedPrefKey, true);
        isCaptionDisplayed = sharedPreferences.getBoolean(isPostCaptionDisplayedPrefKey, true);
        isProfilePicDisplayed = sharedPreferences.getBoolean(isProfilePicDisplayedPrefKey, true);
        isUsernameDisplayed = sharedPreferences.getBoolean(isUsernameDisplayPrefKey, true);

        // Size configs
        profilePicWidth = getIntValueFromPref(profilePicWidthPrefKey, Constants.DEFAULT_PROFILE_PIC_WIDTH);
        profilePicHeight = getIntValueFromPref(profilePicHeightPrefKey, Constants.DEFAULT_PROFILE_PIC_HEIGHT);
        usernameTextSize = getIntValueFromPref(usernameTextSizePrefKey, Constants.DEFAULT_USERNAME_TEXT_SIZE);
        imgMainWidth = getIntValueFromPref(imgMainWidthPrefKey, 0); //Width is initialized as screen's width
        imgMainHeight = getIntValueFromPref(imgMainHeightPrefKey, 0); //Height is initialized as 3/4 of screen's width
        likeTextSize = getIntValueFromPref(likeTextSizePrefKey, Constants.DEFAULT_LIKE_TEXT_SIZE);
        commentTextSize = getIntValueFromPref(commentTextSizePrefKey, Constants.DEFAULT_COMMENT_TEXT_SIZE);
        captionTextSize = getIntValueFromPref(captionTextSizePrefKey, Constants.DEFAULT_CAPTION_TEXT_SIZE);
        presentInterval = getIntValueFromPref(presentIntervalPrefKey, Constants.DEFAULT_PRESENTATION_INTERVAL);
        refreshInterval = getIntValueFromPref(refreshIntervalPrefKey, Constants.DEFAULT_REFRESH_INTERVAL);

        String excludedHashtagsString = sharedPreferences.getString(excludedHashtagsPrefKey, "").toLowerCase();

        if (excludedHashtagsString.isEmpty()) {
            excludedHashtags = new ArrayList<>();
        } else {
            excludedHashtags = Arrays.asList(excludedHashtagsString.split(","));
        }
    }

    private int getIntValueFromPref(String key, int defaultValue) {
        return Integer.parseInt(sharedPreferences.getString(key,
                String.valueOf(defaultValue)));
    }

    private void initComponentsSize() {
        imgProfile.getLayoutParams().width = profilePicWidth;
        imgProfile.getLayoutParams().height = profilePicHeight;
        txtUsername.setTextSize(usernameTextSize);
        imgMain.getLayoutParams().height = imgMainHeight;
        imgMain.getLayoutParams().width = imgMainWidth;
        txtNumberOfLikes.setTextSize(likeTextSize);
        txtNumberOfComments.setTextSize(commentTextSize);
        txtPostCaption.setTextSize(captionTextSize);
    }

    private void startConfigServerMsgTimer(String messageFormat, int duration, TextView target, boolean hideServerInfo) {
        target.setVisibility(View.VISIBLE);
        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                target.setText(String.format(messageFormat, l / 1000));
            }

            @Override
            public void onFinish() {
                if (hideServerInfo) {
                    txtServerInfo.setVisibility(View.GONE);
                }
                target.setVisibility(View.GONE);
            }
        }.start();
    }

    private void setServerInfo() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        String ssid = info.getSSID();
        final String formatedIpAddress = String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        String serverStatus = "Status: Online | ";
        String wifiSsid = String.format(Locale.ENGLISH, "Connected WiFi SSID: %s%n", ssid);
        String configServerIp = String.format(Locale.ENGLISH, "Config server: %s:%d", formatedIpAddress, Constants.WEB_SERVER_PORT);
        String serverInfo = serverStatus + wifiSsid + configServerIp;
        txtServerInfo.setText(serverInfo);
    }

    private boolean checkExcludedHashtags(String caption) {
        if (excludedHashtags.isEmpty()) return false;

        for (String hashtag : excludedHashtags) {
            if (caption != null && caption.toLowerCase().contains(hashtag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method shifts the start post index in case the request for the first post has error
     * (Set the next post as the start post)
     * If after 1 batch of requests (a batch has 12 requests), the 1st post of the instagramPosts list
     * doesn't have the same index as startPostIndex then set the 1st post as the start post
     * (This is to make sure the image slide will show)
     */
    private void shiftStartPostIndex(int postIndex) {
        if (postIndex == startPostIndex) {
            startPostIndex++;
        } else if (!instagramPosts.isEmpty()
                && instagramPosts.get(0).getIndex() != startPostIndex
                && postIndex > startPostIndex + 12) {
            startPostIndex = instagramPosts.get(0).getIndex();
        }
    }

    private void cleanupWebView() {
        webView.loadUrl("about:blank");
        webView.clearHistory();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(imagePresentationLoader);
    }

    private ScrollableWebScraper.HtmlExtractionListener initialHtmlListener = html -> {
        if (progressBar.getVisibility() == View.GONE && !maxNumberOfPostsReached) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (maxNumberOfPostsReached || scrollCount >= Constants.SCROLL_COUNT_LIMIT) {
            return;
        }

        Elements elements = Jsoup.parse(html).select("article a");
        // Use a Set of Element to avoid duplication
        for (Element element : elements) {
            postElementSet.add(new InstagramPostElement(element));
        }

        int currentNumberOfPosts = postElementSet.size();
        // If there are new posts, process the HTML document
        if (currentNumberOfPosts > lastNumberOfPosts) {
            scrollCount = 0;
            int postIndex = 0;
            for (InstagramPostElement postElement : postElementSet) {
                final int index = postIndex++;
                // If the element is requested for info once -> won't be processed
                if (!postElement.isRequested()) {
                    String postHref = postElement.getElement().attr("href");

                    requestQueue.add(new StringRequest("https://instagram.com" + postHref,
                            // Success listener
                            instagramPostHtml -> {
                                //Mark the element as requested -> won't be processed in the next iteration
                                postElement.setRequested(true);
                                InstagramPost post = InstagramUtil.parseInstagramPostHtml(instagramPostHtml, index, postHref);
                                onInstagramPostRequestSuccess(post);
                            },
                            // Error listener
                            error -> shiftStartPostIndex(index)));
                }
            }
        } else {
            scrollCount++;
        }

        lastNumberOfPosts = postElementSet.size();
        if (currentNumberOfPosts == 0) {
            // Wait for the page to be fully loaded the first time -> less delay
            scrollableWebScraper.scrollToBottomWithDelay(Constants.FIRST_SCROLL_DELAY);
        } else {
            // Delay to wait for requests to be finished -> avoid requesting redundantly
            scrollableWebScraper.scrollToBottomWithDelay(Constants.NEXT_SCROLLS_DELAY);
        }
    };

    private ScrollableWebScraper.HtmlExtractionListener refreshHtmlListener = html -> {
        if (maxNumberOfPostsReached || scrollCount >= Constants.SCROLL_COUNT_LIMIT) {
            Collections.sort(newInstagramPosts);

            String currentPostHref = instagramPosts.get(nextSlideIndex).getPostHref();

            for (int i = 0; i < newInstagramPosts.size(); i++) {
                if (currentPostHref.equals(newInstagramPosts.get(i).getPostHref())) {
                    nextSlideIndex = i;
                }
            }

            handler.removeCallbacks(imagePresentationLoader);
            startImagePresentation(newInstagramPosts);

            instagramPosts = new ArrayList<>();
            instagramPosts.addAll(newInstagramPosts);
            cleanupWebView();
            return;
        }

        Elements elements = Jsoup.parse(html).select("article a");
        // Use a Set of Element to avoid duplication
        for (Element element : elements) {
            postElementSet.add(new InstagramPostElement(element));
        }

        int currentNumberOfPosts = postElementSet.size();
        // If there are new posts, process the HTML document
        if (currentNumberOfPosts > lastNumberOfPosts) {
            scrollCount = 0;
            int postIndex = 0;
            for (InstagramPostElement postElement : postElementSet) {
                final int index = postIndex++;
                // If the element is requested for info once -> won't be processed
                if (!postElement.isRequested()) {
                    String postHref = postElement.getElement().attr("href");

                    requestQueue.add(new StringRequest("https://instagram.com" + postHref,
                            // Success listener
                            instagramPostHtml -> {
                                //Mark the element as requested -> won't be processed in the next iteration
                                postElement.setRequested(true);
                                InstagramPost post = InstagramUtil.parseInstagramPostHtml(instagramPostHtml, index, postHref);
                                if (!checkExcludedHashtags(post.getCaption())) {
                                    newInstagramPosts.add(post);
                                    maxNumberOfPostsReached = checkPostLimit(newInstagramPosts);
                                }
                            },
                            // Error listener
                            null));
                }
            }
        } else {
            scrollCount++;
        }

        lastNumberOfPosts = postElementSet.size();
        if (currentNumberOfPosts == 0) {
            // Wait for the page to be fully loaded the first time -> less delay
            scrollableWebScraper.scrollToBottomWithDelay(Constants.FIRST_SCROLL_DELAY);
        } else {
            // Delay to wait for requests to be finished -> avoid requesting redundantly
            scrollableWebScraper.scrollToBottomWithDelay(Constants.NEXT_SCROLLS_DELAY);
        }
    };
}