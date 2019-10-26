package com.demo.instagram_presentation.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.InstagramApplicationLike;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.data.scraper.InstagramLogin;
import com.demo.instagram_presentation.data.scraper.InstagramWebScraper;
import com.demo.instagram_presentation.hotfix_plugin.Constant;
import com.demo.instagram_presentation.hotfix_plugin.PatchingUtil;
import com.demo.instagram_presentation.model.InstagramPost;
import com.demo.instagram_presentation.model.InstagramPostElement;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.BroadcastReceiverUtil;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.InstagramUtil;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.util.NetworkUtil;
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
    @BindView(R.id.fragment_present_imgNetworkStrength)
    ImageView imgNetworkStrength;
    @BindView(R.id.fragment_present_txtLoginError)
    TextView txtLoginError;

    @BindString(R.string.source_url_not_set)
    String errorSourceUrlNotSet;
    @BindString(R.string.invalid_source_url)
    String errorInvalidSourceUrl;
    @BindString(R.string.feed_request_error)
    String errorFeedRequestFailed;
    @BindString(R.string.progress_getting_user_info)
    String progressGettingFeedData;
    @BindString(R.string.progress_login)
    String progressLogin;
    @BindString(R.string.progress_done)
    String progressDone;
    @BindString(R.string.pref_instagram_source)
    String instagramSourcePrefKey;
    @BindString(R.string.pref_instagram_source_tags)
    String instagramSourceTagsPrefKey;
    @BindString(R.string.pref_instagram_username)
    String instagramUsernamePrefKey;
    @BindString(R.string.pref_instagram_password)
    String instagramPasswordPrefKey;
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
    @BindString(R.string.pref_required_login)
    String requiredLoginPrefKey;
    @BindString(R.string.pref_required_security_code)
    String requiredSecurityCodePrefKey;
    @BindString(R.string.pref_login_error_msg)
    String loginErrorMsgPrefKey;
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
    private List<String> requiredHashtags;
    private String instagramSourceUrl;
    private String instagramSourceTags;
    private String instagramUsername;
    private String instagramPassword;
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
    private InstagramWebScraper instagramWebScraper;
    private Set<InstagramPostElement> postElementSet;
    private Set<InstagramPost> instagramPostsSet;
    private List<InstagramPost> instagramPosts;
    private List<InstagramPost> newInstagramPosts;
    private boolean maxNumberOfPostsReached;
    private boolean slideStarted;
    private int lastNumberOfPosts;
    private int startPostIndex;
    private int nextSlideIndex;
    private String lastInstagramUsername;
    private String lastUserProfilePicUrl;

    // Source URL can be an user's feed or a single hashtag
    private String sourceUrl;
    private boolean fetchByHashtags;

    /**
     * Count the number of times the webView scrolls but no new content is found
     * -> it can be due to network error or the webView has scrolled to the end
     * If the count exceeds a limit -> stop scrolling (scroll timeout)
     */
    private int scrollCount;

    private final Runtime runtime = Runtime.getRuntime();
    private final String bugfenderTag = MainActivity.DEVICE_ID;

    private Context context;

    private final int MAX_REQUEST_COUNTER = 5;
    private int sendRequestCounter = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init tools
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferences = AppPreferencesUtil.getSharedPreferences();
        jsonParser = new JsonParser();

        // Bind context
        context = getContext();

        // Check for update
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String domain = AppPreferencesUtil.getSharedPreferences().getString(Constant.DOMAIN_KEY, Constant.DEFAULT_DOMAIN);
                PatchingUtil.checkForUpdate(domain);
                return null;
            }
        }.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentRootView = inflater.inflate(R.layout.fragment_image_slide, container, false);
        ButterKnife.bind(this, fragmentRootView);

        webView.getSettings().setLoadsImagesAutomatically(false);
        setServerInfo("");
        startConfigServerMsgTimer(timerMessageForServer, Constants.HIDE_SERVER_INFO_ON_WIFI_DELAY, txtTimer, true);
        getPreferences();
        initComponentsSize();
        showWatermark();

        // Hide components, show them after the images are loaded
        hideComponents();
        startNetworkStrengthScan(Constants.NETWORK_SIGNAL_SCAN_INTERVAL);

        // If both source URL and source hashtags are empty -> request user to go to settings screen to setup first
        if (instagramSourceUrl.trim().isEmpty() && instagramSourceTags.trim().isEmpty()) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(errorSourceUrlNotSet);
            return fragmentRootView;
        }
        // Source URL is not empty -> fetch by URL, filter hashtags
        else if (!instagramSourceUrl.trim().isEmpty()) {
            fetchByHashtags = false;
            sourceUrl = InstagramUtil.normalizeUserUrl(instagramSourceUrl);
        }
        // Source URL is empty + Source hashtags is not empty -> fetch by hashtags
        else {
            fetchByHashtags = true;
            // If source URL is empty, only 1 hashtag is allowed on config website
            sourceUrl = InstagramUtil.constructInstagramHashtagQueryUrl(instagramSourceTags);
        }

        instagramWebScraper = new InstagramWebScraper(webView, instagramUsername, instagramPassword, sourceUrl);
        instagramWebScraper.configWebView();
        instagramWebScraper.setInstagramLoginListener(loginListener);
        startFetchingPosts();

        return fragmentRootView;
    }

    private void startFetchingPosts() {
        if (!NetworkUtil.isWifiConnected()) {
            imgNetworkStrength.setVisibility(View.VISIBLE);
            handler.postDelayed(this::startFetchingPosts, Constants.DEFAULT_FEED_REQUEST_RETRY_INTERVAL);
        } else {
            imgNetworkStrength.setVisibility(View.GONE);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestQueue.add(new StringRequest(sourceUrl,
                        success -> {
                            instagramPosts = new ArrayList<>();
                            newInstagramPosts = new ArrayList<>();
                            nextSlideIndex = 0;

                            txtError.setVisibility(View.GONE);
                            txtProgress.setVisibility(View.VISIBLE);
                            txtProgress.setText(progressGettingFeedData);
                            progressBar.setProgress(0);
                            progressBar.setMax(numberOfPostsToDisplay);

                            // Start scraper to get posts of user
                            instagramWebScraper.setHtmlExtractionListener(initialHtmlListener);
                            instagramWebScraper.startScrapingInstagram();

                            initScraperVariables();
                            Bugfender.d(bugfenderTag, "Initial request success, web scraper will be started");
                            logMemory();
                        },
                        err -> {
                            if (err.networkResponse != null && err.networkResponse.statusCode == 404) {
                                Bugfender.e(bugfenderTag, "Initial request failed due to 404 error, Instagram source may be invalid");
                                txtError.setVisibility(View.VISIBLE);
                                txtError.setText(errorInvalidSourceUrl);
                            } else {
                                // Retry
                                sendRequestCounter++;
                                if (sendRequestCounter < MAX_REQUEST_COUNTER) {
                                    Bugfender.e(bugfenderTag, "Initial request failed due to network error, retrying...");
                                    handler.postDelayed(this, Constants.DEFAULT_FEED_REQUEST_RETRY_INTERVAL);
                                    startConfigServerMsgTimer(timerMessageForRetry, Constants.DEFAULT_FEED_REQUEST_RETRY_INTERVAL, txtError, false);
                                } else {
                                    Bugfender.e(bugfenderTag, "No internet connection");
                                    AppPreferencesUtil.setFlagNoInternet();
                                    Intent noInternetIntent = new Intent(Constants.NO_INTERNET_ACTION);
                                    context.sendBroadcast(noInternetIntent);
                                }
                            }
                            Bugfender.e(bugfenderTag, "Initial request error: " + err.getMessage());
                        }));
                }
            });
        }
    }

    private void startRefreshTaskAfter(int ms) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bugfender.d(bugfenderTag, "Start refreshing posts");
                if (!NetworkUtil.isWifiConnected()) {
                    Bugfender.e(bugfenderTag, "Refreshing posts status: Wi-Fi unavailable");
                    imgNetworkStrength.setVisibility(View.VISIBLE);
                } else {
                    requestQueue.add(new StringRequest(sourceUrl,
                        success -> {
                            Bugfender.d(bugfenderTag, "Refresh request success");
                            initScraperVariables();
                            instagramWebScraper.setHtmlExtractionListener(refreshHtmlListener);
                            instagramWebScraper.startScrapingInstagram();
                            imgNetworkStrength.setVisibility(View.GONE);
                        },
                        // In case internet is unavailable when the task is running
                        err -> {
                            Bugfender.e(bugfenderTag, "Refresh request failed, retrying... ");
                            Bugfender.e(bugfenderTag, "Refresh request error: " + err.getMessage());
                            handler.postDelayed(this, Constants.DEFAULT_FEED_REQUEST_RETRY_INTERVAL);
                            imgNetworkStrength.setVisibility(View.VISIBLE);
                        }));
                }
            }
        }, ms);
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
        instagramPostsSet = new LinkedHashSet<>();
    }

    private void onInstagramPostRequestSuccess(InstagramPost instagramPost) {
        // If post contains excluded hashtag -> skip
        if (hasExcludedHashtags(instagramPost.getCaption())) {
            shiftStartPostIndex(instagramPost.getIndex());
            return;
        }

        // Perform required hashtags check if user use URL mode - fetch posts by URL
        // If post does not contain one of required hashtags -> skip
        if (!fetchByHashtags && !hasRequiredHashtags(instagramPost.getCaption())) {
            shiftStartPostIndex(instagramPost.getIndex());
            return;
        }

        instagramPosts.add(instagramPost);
        Picasso.get().load(instagramPost.getImgUrl()).fetch();
        Picasso.get().load(instagramPost.getUserProfilePicUrl()).fetch();

        if (!maxNumberOfPostsReached) {
            txtProgress.setText(String.format(Locale.ENGLISH, "Retrieved %d/%d posts", instagramPosts.size(), numberOfPostsToDisplay));
            progressBar.setProgress(instagramPosts.size() + 1);

            if (checkPostLimit(instagramPosts) || scrollCount >= Constants.SCROLL_COUNT_LIMIT) {
                hideProgress();
                webView.loadUrl("about:blank");
                maxNumberOfPostsReached = true;
                Bugfender.d(bugfenderTag, "Finished fetching posts");
                logMemory();
            }
        }

        // Sort posts using the order of the posts in HTML document
        Collections.sort(instagramPosts);

        // Start the slide when the first image is retrieved
        if (!slideStarted && !instagramPosts.isEmpty()
                && ((instagramPosts.get(0).getIndex() == startPostIndex) || maxNumberOfPostsReached)) {
            slideStarted = true;
            startImagePresentation();
            startRefreshTaskAfter(refreshInterval);
        }
    }

    private boolean checkPostLimit(List<InstagramPost> posts) {
        return posts.size() >= numberOfPostsToDisplay;
    }

    private void startImagePresentation() {
        if (imagePresentationLoader != null) {
            handler.removeCallbacks(imagePresentationLoader);
        }

        imagePresentationLoader = new Runnable() {
            @Override
            public void run() {
                if (instagramPosts.isEmpty()) return;

                int index = nextSlideIndex++;

                if (index > numberOfPostsToDisplay - 1 || index > instagramPosts.size() - 1) {
                    // If index is out of bounds -> run the slide from the beginning
                    index = 0;
                    nextSlideIndex = 1;
                }

                InstagramPost post = instagramPosts.get(index);

//                Log.d("LogDisplayingPost", String.format("(%s) Post number %d, title: %s", new SimpleDateFormat("dd/MM - HH:mm:ss").format(new Date()), index, post.getImgUrl()));

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

                // Check if username and profile pic is new (in case user uses hashtags mode)
                // If new -> reload username and profile pic
                if (!post.getUsername().equals(lastInstagramUsername)) {
                    lastInstagramUsername = post.getUsername();
                    txtUsername.setText(lastInstagramUsername);
                }

                if (!post.getUserProfilePicUrl().equals(lastUserProfilePicUrl)) {
                    lastUserProfilePicUrl = post.getUserProfilePicUrl();
                    Picasso.get()
                            .load(lastUserProfilePicUrl)
                            .fit()
                            .centerCrop()
                            .into(imgProfile);
                }

                showComponents();

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
        instagramSourceUrl = sharedPreferences.getString(instagramSourcePrefKey, "");
        instagramSourceTags = sharedPreferences.getString(instagramSourceTagsPrefKey, "");
        instagramUsername = sharedPreferences.getString(instagramUsernamePrefKey, "");
        instagramPassword = sharedPreferences.getString(instagramPasswordPrefKey, "");
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
        refreshInterval = getIntValueFromPref(refreshIntervalPrefKey, Constants.DEFAULT_REFRESH_INTERVAL) * 60 * 1000;

        String excludedHashtagsString = sharedPreferences.getString(excludedHashtagsPrefKey, "");

        excludedHashtags = excludedHashtagsString.isEmpty() ? new ArrayList<>()
                : Arrays.asList(excludedHashtagsString.split(","));
        requiredHashtags = instagramSourceTags.isEmpty() ? new ArrayList<>()
                : Arrays.asList(instagramSourceTags.split(","));
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

    private void setServerInfo(String route) {
        WifiManager wifiManager = (WifiManager) InstagramApplicationContext.context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        String ssid = info.getSSID();
        final String formatedIpAddress = String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        String serverStatus = "Status: Online | ";
        String wifiSsid = String.format(Locale.ENGLISH, "Connected WiFi SSID: %s%n", ssid);
        String configServerIp;
        configServerIp = String.format(Locale.ENGLISH, "Config server: %s:%d/%s", formatedIpAddress, Constants.WEB_SERVER_PORT, route);
        String serverInfo = serverStatus + wifiSsid + configServerIp;
        txtServerInfo.setText(serverInfo);
    }

    private boolean hasExcludedHashtags(String caption) {
        if (excludedHashtags.isEmpty()) return false;

        for (String hashtag : excludedHashtags) {
            if (caption != null && caption.toLowerCase().contains(hashtag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRequiredHashtags(String caption) {
        if (requiredHashtags.isEmpty()) return true;

        for (String hashtag : requiredHashtags) {
            if (caption != null && caption.toLowerCase().contains(hashtag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks the post index to avoid infinite scrolling
     * After #SCROLL_COUNT_LIMIT times of scrolls, if no posts are added -> terminate the scrape process
     */
    private boolean checkInfiniteSroll(int postIndex) {
        int lastPostIndex = 0;

        if (!instagramPosts.isEmpty()) {
            lastPostIndex = instagramPosts.get(instagramPosts.size() - 1).getIndex();
        }

        if (postIndex > (lastPostIndex + Constants.INFINITE_SCROLL_POST_COUNT)) {
            return true;
        } else {
            return false;
        }
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

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(imagePresentationLoader);
    }

    BroadcastReceiver submitSecurityCodeReceiver;
    IntentFilter submitSecurityCodeAction;

    BroadcastReceiver getNewSecurityCodeReceiver;
    IntentFilter getNewSecurityCodeAction;

    private void toggleDisplayingWebview(boolean isDisplayed) {
        if (isDisplayed) {
            webView.setVisibility(View.VISIBLE);
            txtLoginError.setVisibility(View.VISIBLE);

            if (txtServerInfo.getVisibility() == View.GONE) {
                txtServerInfo.setVisibility(View.VISIBLE);
                txtTimer.setVisibility(View.VISIBLE);
                setServerInfo("authorize");
                startConfigServerMsgTimer(timerMessageForServer, Constants.HIDE_SERVER_INFO_ON_WIFI_DELAY, txtTimer, true);
            }

            if (submitSecurityCodeReceiver == null) {
                submitSecurityCodeReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String code = intent.getStringExtra("securityCode");
                        instagramWebScraper.submitSecurityCode(code);
                    }
                };
                submitSecurityCodeAction = new IntentFilter(Constants.SUBMIT_SECURITY_CODE_ACTION);
                MainActivity.self.registerReceiver(submitSecurityCodeReceiver, submitSecurityCodeAction);
            }

            if (getNewSecurityCodeReceiver == null) {
                getNewSecurityCodeReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        instagramWebScraper.getNewSecurityCode();
                    }
                };
                getNewSecurityCodeAction = new IntentFilter(Constants.REQUEST_GET_NEW_SECURITY_CODE_ACTION);
                MainActivity.self.registerReceiver(getNewSecurityCodeReceiver, getNewSecurityCodeAction);
            }
        } else {
            webView.setVisibility(View.INVISIBLE);
            txtLoginError.setVisibility(View.GONE);

            if (submitSecurityCodeReceiver != null) {
                BroadcastReceiverUtil.unregisterReceiver(MainActivity.self, submitSecurityCodeReceiver);
            }

            if (getNewSecurityCodeReceiver != null) {
                BroadcastReceiverUtil.unregisterReceiver(MainActivity.self, getNewSecurityCodeReceiver);
            }
        }
    }

    private InstagramWebScraper.InstagramLoginListener loginListener = (success, loginCode, loginErrorReason) -> {
        if (success) {
            SharedPreferences.Editor prefEditor = sharedPreferences.edit();
            prefEditor.putBoolean(requiredLoginPrefKey, false);
            prefEditor.apply();
            toggleDisplayingWebview(false);
            startFetchingPosts();
        } else {
            Bugfender.e(bugfenderTag, "Login failed");
            if (loginCode == InstagramLogin.LOGIN_CHALLENGE_CODE) {
                toggleDisplayingWebview(true);
                sharedPreferences.edit().putBoolean(requiredSecurityCodePrefKey, true).apply();
            } else {
                toggleDisplayingWebview(false);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(requiredLoginPrefKey, true);
                prefEditor.putString(loginErrorMsgPrefKey, loginErrorReason);
                prefEditor.apply();

                handler.removeCallbacks(imagePresentationLoader);

                Intent loginFailedIntent = new Intent(Constants.LOGIN_FAILED_ACTION);
                context.sendBroadcast(loginFailedIntent);
            }
        }
    };

    private InstagramWebScraper.HtmlExtractionListener initialHtmlListener = html -> {
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean(requiredLoginPrefKey, false);
        prefEditor.apply();
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

                if (checkInfiniteSroll(index)) {
                    hideProgress();
                    return;
                }
                // If the element is requested for info once -> won't be processed
                if (!postElement.isRequested()) {
                    String postHref = postElement.getElement().attr("href");

                    requestQueue.add(new StringRequest("https://instagram.com" + postHref,
                        // Success listener
                        instagramPostHtml -> {
                            //Mark the element as requested -> won't be processed in the next iteration
                            postElement.setRequested(true);
                            InstagramPost post = InstagramUtil.parseInstagramPostHtml(instagramPostHtml, index, postHref);

                            int beforeAddSize = instagramPostsSet.size();
                            instagramPostsSet.add(post);
                            int afterAddSize = instagramPostsSet.size();
                            if (beforeAddSize < afterAddSize) {
                                onInstagramPostRequestSuccess(post);
                            }
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
            instagramWebScraper.continueGettingPosts(Constants.FIRST_SCROLL_DELAY);
        } else {
            // Delay to wait for requests to be finished -> avoid requesting redundantly
            instagramWebScraper.continueGettingPosts(Constants.NEXT_SCROLLS_DELAY);
        }
    };

    private InstagramWebScraper.HtmlExtractionListener refreshHtmlListener = html -> {
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean(requiredLoginPrefKey, false);
        prefEditor.apply();
        if (maxNumberOfPostsReached || scrollCount >= Constants.SCROLL_COUNT_LIMIT) {
            Bugfender.d(bugfenderTag, "Finished fetching posts");
            logMemory();
            webView.loadUrl("about:blank");

            if (!newInstagramPosts.isEmpty()) {
                Collections.sort(newInstagramPosts);
                instagramPosts.clear();
                instagramPosts.addAll(newInstagramPosts);
            }
            startRefreshTaskAfter(refreshInterval);
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

                if (checkInfiniteSroll(index)) {
                    hideProgress();
                    return;
                }
                // If the element is requested for info once -> won't be processed
                if (!postElement.isRequested()) {
                    String postHref = postElement.getElement().attr("href");

                    requestQueue.add(new StringRequest("https://instagram.com" + postHref,
                            // Success listener
                            instagramPostHtml -> {
                                //Mark the element as requested -> won't be processed in the next iteration
                                postElement.setRequested(true);
                                InstagramPost post = InstagramUtil.parseInstagramPostHtml(instagramPostHtml, index, postHref);

                                int beforeAddSize = instagramPostsSet.size();
                                instagramPostsSet.add(post);
                                int afterAddSize = instagramPostsSet.size();
                                if (beforeAddSize < afterAddSize) {
                                    if (!hasExcludedHashtags(post.getCaption()) && hasRequiredHashtags(post.getCaption())) {
                                        newInstagramPosts.add(post);
                                        Picasso.get().load(post.getImgUrl()).fetch();
                                        Picasso.get().load(post.getUserProfilePicUrl()).fetch();
                                        maxNumberOfPostsReached = checkPostLimit(newInstagramPosts);
                                    }
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
            instagramWebScraper.continueGettingPosts(Constants.FIRST_SCROLL_DELAY);
        } else {
            // Delay to wait for requests to be finished -> avoid requesting redundantly
            instagramWebScraper.continueGettingPosts(Constants.NEXT_SCROLLS_DELAY);
        }
    };

    private void startNetworkStrengthScan(int intervalInMs) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int networkStrength = NetworkUtil.getNetworkStrength(5);
                    String packageName = BuildConfig.APPLICATION_ID;
                    String drawableName = "ic_signal_wifi_" + networkStrength + "_bar_black_48dp";

                    int drawableId = getResources().getIdentifier(packageName + ":drawable/" + drawableName, null, null);
                    imgNetworkStrength.setImageDrawable(getResources().getDrawable(drawableId));

                    if (imgNetworkStrength.getVisibility() == View.GONE) {
                        imgNetworkStrength.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
//                    Bugfender.e(bugfenderTag, "Failed while scanning network strength");
                } finally {
                    handler.postDelayed(this, intervalInMs);
                }
            }
        });
    }

    private void logMemory() {
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
//        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

//        log.debug(msg);
//        log.debug("usedMemInMB: " + usedMemInMB + "MB");
//        log.debug("maxHeapSizeInMB: " + maxHeapSizeInMB + "MB");
//        log.debug("availHeapSizeInMB: " + availHeapSizeInMB + "MB");

        Bugfender.d(bugfenderTag, "Heap usage: " + usedMemInMB + "/" + maxHeapSizeInMB + "MB");
    }
}