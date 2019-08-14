package com.demo.instagram_presentation.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.data.scraper.ScrollableWebScraper;
import com.demo.instagram_presentation.model.InstagramPost;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.InstagramUtil;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageSlideFragment extends Fragment implements ScrollableWebScraper.HtmlExtractionListener {
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
    @BindView(R.id.fragment_present_txtPostDescription)
    TextView txtPostDescription;
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
    @BindString(R.string.progress_getting_feed_data)
    String progressGettingPosts;
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
    @BindString(R.string.pref_is_post_description_displayed)
    String isPostDescriptionDisplayedPrefKey;
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
    @BindString(R.string.pref_description_text_size)
    String descriptionTextSizePrefKey;
    @BindString(R.string.pref_present_interval)
    String presentIntervalPrefKey;
    @BindString(R.string.source_url_error)
    String sourceUrlError;

    private RequestQueue requestQueue;
    private Runnable imagePresentationLoader;
    private final Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private JsonParser jsonParser;
    private Gson gson;
    private CountDownTimer countDownTimer;

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
    private int descriptionTextSize;
    private int presentInterval;
    private boolean isLikesDisplayed;
    private boolean isCommentsDisplayed;
    private boolean isDescriptionDisplayed;
    private boolean isProfilePicDisplayed;
    private boolean isUsernameDisplayed;
    private ScrollableWebScraper scrollableWebScraper;
    private Set<InstagramPostElement> postElementSet;
    private InstagramPost[] instagramPosts;
    private int successRequestCount;
    private int failedRequestCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init tools
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        jsonParser = new JsonParser();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentRootView = inflater.inflate(R.layout.fragment_image_slide, container, false);
        ButterKnife.bind(this, fragmentRootView);

        webView.getSettings().setLoadsImagesAutomatically(false);

        setServerInfo();
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
            postElementSet = new LinkedHashSet<>();
            instagramPosts = new InstagramPost[numberOfPostsToDisplay];
            // Else, get user's id from source URL
            txtError.setVisibility(View.GONE);

            txtProgress.setText(progressGettingFeedData);
            progressBar.setProgress(0);
            progressBar.setMax(1 + numberOfPostsToDisplay);

            txtProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            // Start scraper to get posts of user
            scrollableWebScraper = new ScrollableWebScraper(webView, instagramSourceUrl);
            scrollableWebScraper.setHtmlExtractionListener(this);

            getUserInfo();
            scrollableWebScraper.start();

            startConfigServerMsgTimer();
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

                    txtUsername.setText(userInfo.get("username").getAsString());

                    Picasso.get()
                            .load(userInfo.get("profile_pic_url_hd").getAsString())
                            .fit()
                            .centerCrop()
                            .into(imgProfile);
                }, error -> Log.e("Error", error.toString())));
    }

    @Override
    public void onHtmlExtracted(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("article a");

        // Create a Set of Element to avoid duplication, stop when post limit is reached
        for (Element element : elements) {
            InstagramPostElement postElement = new InstagramPostElement(element);
            postElementSet.add(postElement);
        }

        txtProgress.setText(String.format(Locale.ENGLISH, "Found %d posts", postElementSet.size()));
        if (postElementSet.size() < numberOfPostsToDisplay) {
            scrollableWebScraper.scrollToBottom();
            return;
        }

        List<Element> htmlElements = new ArrayList<>();
        for (InstagramPostElement postElement : postElementSet) {
            htmlElements.add(postElement.getElement());
        }

        txtProgress.setText(progressGettingPosts);
        progressBar.setProgress(1);
        successRequestCount = 0;
        failedRequestCount = 0;
        for (int i = 0; i < numberOfPostsToDisplay; i++) {
            Element htmlElement = htmlElements.get(i);
            final int index = i;

            requestQueue.add(new StringRequest("https://instagram.com" + htmlElement.attr("href"),
                    // Success listener
                    instagramPostHtml -> {
                        Document doc = Jsoup.parse(instagramPostHtml);
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

                        instagramPosts[index] = new InstagramPost(likes, comments, caption, imgSrc);
                        onInstagramPostRequestDone(true);
                    },
                    // Error listener
                    error -> {
                        instagramPosts[index] = null;
                        onInstagramPostRequestDone(false);
                    }));
        }
    }

    private void onInstagramPostRequestDone(boolean requestSuccess) {
        if (requestSuccess) {
            successRequestCount++;
        } else {
            failedRequestCount++;
        }

        int totalRequestCount = successRequestCount + failedRequestCount;

        progressBar.setProgress(1 + totalRequestCount);
        txtProgress.setText(String.format(Locale.ENGLISH, "Retrieved %d/%d posts",
                totalRequestCount, numberOfPostsToDisplay));

        if (failedRequestCount == numberOfPostsToDisplay) {
            // TODO: Handle error - all requests failed -> no posts were retrieved
        } else if (totalRequestCount == numberOfPostsToDisplay) {
            startImagePresentation(instagramPosts);
        }
    }

    private void startImagePresentation(InstagramPost[] instagramPosts) {
        if (imagePresentationLoader != null) {
            handler.removeCallbacks(imagePresentationLoader);
        }

        imagePresentationLoader = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= instagramPosts.length) {
                    index = 0;
                }

                InstagramPost post = null;

                while (post == null) {
                    post = instagramPosts[index++];
                }

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
                txtPostDescription.setText(post.getCaption());

                showComponents();
                hideProgress();

                handler.postDelayed(this, presentInterval);
            }
        };

        handler.post(imagePresentationLoader);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
        progressBar.setProgress(0);
    }

    private void hideComponents() {
        progressBar.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
        imgProfile.setVisibility(View.INVISIBLE);
        txtUsername.setVisibility(View.GONE);
        imgMain.setVisibility(View.GONE);
        txtNumberOfLikes.setVisibility(View.GONE);
        txtNumberOfComments.setVisibility(View.GONE);
        txtPostDescription.setVisibility(View.GONE);

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

        if (isDescriptionDisplayed) {
            txtPostDescription.setVisibility(View.VISIBLE);
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
        isDescriptionDisplayed = sharedPreferences.getBoolean(isPostDescriptionDisplayedPrefKey, true);
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
        descriptionTextSize = getIntValueFromPref(descriptionTextSizePrefKey, Constants.DEFAULT_DESCRIPTION_TEXT_SIZE);
        presentInterval = getIntValueFromPref(presentIntervalPrefKey, Constants.DEFAULT_PRESENTATION_INTERVAL);

        String excludedHashtagsString = sharedPreferences.getString(excludedHashtagsPrefKey, null);
        if (excludedHashtagsString != null) {
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
        txtUsername.setTextSize(TypedValue.COMPLEX_UNIT_SP, usernameTextSize);
        imgMain.getLayoutParams().height = imgMainHeight;
        imgMain.getLayoutParams().width = imgMainWidth;
        txtNumberOfLikes.setTextSize(TypedValue.COMPLEX_UNIT_SP, likeTextSize);
        txtNumberOfComments.setTextSize(TypedValue.COMPLEX_UNIT_SP, commentTextSize);
        txtPostDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, descriptionTextSize);
    }

    private void startConfigServerMsgTimer() {
        if (countDownTimer == null) {
            int length = Constants.HIDE_SERVER_INFO_ON_WIFI_DELAY;

            countDownTimer = new CountDownTimer(length, 1000) {
                @Override
                public void onTick(long l) {
                    txtTimer.setText(String.format("This message will disappear in %d seconds", l / 1000));
                }

                @Override
                public void onFinish() {
                    txtServerInfo.setVisibility(View.GONE);
                    txtTimer.setVisibility(View.GONE);
                    countDownTimer = null;
                }
            };

            countDownTimer.start();
        }
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

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(imagePresentationLoader);
    }

    /**
     * Wrapper class for Jsoup Element, this is used for storing Instagram posts in a LinkedHashSet
     * <p>
     * While scraping data from Instagram with WebView, the max number of posts in HTML document at
     * any time is 45 -> need to store the posts if user needs more than 45 posts, LinkedHashSet is used
     * for storing the posts without duplication. the post href is used as the hashCode
     */
    private class InstagramPostElement {
        private Element element;
        private String href;

        InstagramPostElement(Element element) {
            this.element = element;
            href = element.attr("href");
        }

        public Element getElement() {
            return element;
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
}