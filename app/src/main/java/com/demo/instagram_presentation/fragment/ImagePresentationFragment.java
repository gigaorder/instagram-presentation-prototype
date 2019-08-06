package com.demo.instagram_presentation.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.model.InstagramFeedData;
import com.demo.instagram_presentation.model.InstagramPost;
import com.demo.instagram_presentation.model.InstagramUser;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.InstagramUtil;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagePresentationFragment extends Fragment {
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
    @BindView(R.id.fragment_present_btnExit)
    TextView btnExit;

    @BindString(R.string.source_url_not_set)
    String errorSourceUrlNotSet;
    @BindString(R.string.invalid_source_url)
    String errorInvalidSourceUrl;
    @BindString(R.string.feed_request_error)
    String errorFeedRequestFailed;
    @BindString(R.string.progress_getting_user_info)
    String progressGettingUserInfo;
    @BindString(R.string.progress_getting_feed_data)
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

    private RequestQueue requestQueue;
    private Runnable imagePresentationLoader;
    private final Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private JsonParser jsonParser;
    private Gson gson;

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

    private long rootContainerLastClickTime = -1;

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
        View fragmentRootView = inflater.inflate(R.layout.fragment_image_presentation, container, false);
        ButterKnife.bind(this, fragmentRootView);

        fragmentRootView.setOnClickListener(rootContainerClickListener);

        getPreferences();
        initComponentsSize();

        // Hide components, show them after the images are loaded
        hideComponents();

        // If source URL is not set -> request user to go to settings screen to setup first
        if (instagramSourceUrl == null) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(errorSourceUrlNotSet);
        } else {
            // Else, get user's id from source URL
            txtError.setVisibility(View.GONE);

            txtProgress.setText(progressGettingUserInfo);
            progressBar.setProgress(0);
            txtProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            String userInfoRequestUrl = InstagramUtil.constructInstagramUserInfoUrl(instagramSourceUrl);

            // Add request to get user info to queue
            requestQueue.add(new StringRequest(
                    userInfoRequestUrl,
                    userInfoResponseSuccessListener,
                    userInfoResponseErrorListener
            ));
        }

        btnExit.setOnClickListener(view -> getActivity().finish());

        return fragmentRootView;
    }

    // Double click listener
    private View.OnClickListener rootContainerClickListener = view -> {
        if (rootContainerLastClickTime != -1 &&
                System.currentTimeMillis() - rootContainerLastClickTime < 200) {

            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(MainActivity.FRAGMENT_CONTAINER_ID, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
        }

        rootContainerLastClickTime = System.currentTimeMillis();
    };

    private Response.Listener<String> userInfoResponseSuccessListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            txtError.setVisibility(View.GONE);

            String userInfoString = jsonParser.parse(response)
                    .getAsJsonObject().get("graphql")
                    .getAsJsonObject().get("user")
                    .toString();

            InstagramUser user = gson.fromJson(userInfoString, InstagramUser.class);

            txtUsername.setText(user.getFullName());
            Picasso.get().load(user.getProfilePicUrl()).into(imgProfile);

            // After getting user's id, create feed data request URL and send request
            String feedRequestUrl = InstagramUtil.contructFeedRequestUrl(user.getId());

            txtProgress.setText(progressGettingFeedData);
            progressBar.setProgress(1);

            requestQueue.add(new StringRequest(
                    feedRequestUrl,
                    feedResponseSuccessListenter,
                    feedResponseErrorListener));
        }
    };

    private Response.ErrorListener userInfoResponseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(errorInvalidSourceUrl);
        }
    };

    private Response.Listener<String> feedResponseSuccessListenter = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            txtError.setVisibility(View.GONE);

            // Get the nested object as String inside the original response
            String instagramFeedDataString = jsonParser.parse(response)
                    .getAsJsonObject().get("data")
                    .getAsJsonObject().get("user")
                    .getAsJsonObject().get("edge_owner_to_timeline_media")
                    .getAsJsonObject().toString();

            // Extract the image URLs from the retrieved instagram feed data
            InstagramFeedData instagramFeedData = gson.fromJson(instagramFeedDataString, InstagramFeedData.class);
            final List<InstagramPost> postsToDisplay = filterPostsToDisplay(instagramFeedData.getPosts());

            txtProgress.setText(progressDone);
            progressBar.setProgress(2);

            startImagePresentation(postsToDisplay);
        }
    };

    private Response.ErrorListener feedResponseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(errorFeedRequestFailed);
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        requestQueue.cancelAll(request -> true);
        handler.removeCallbacks(imagePresentationLoader);
    }

    private void startImagePresentation(final List<InstagramPost> posts) {
        if (imagePresentationLoader != null) {
            handler.removeCallbacks(imagePresentationLoader);
        }

        imagePresentationLoader = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= posts.size()) {
                    index = 0;
                }

                InstagramPost post = posts.get(index++);
                Picasso.get()
                        .load(post.getDisplayUrl())
                        .fit()
                        .centerCrop()
                        .into(imgMain);

                DecimalFormat numberFormatter = new DecimalFormat("#,###");
                String noOfLikes = numberFormatter.format(post.getNumberOfLikes()) + " likes";
                String noOfComments = numberFormatter.format(post.getNumberOfComments()) + " comments";

                txtNumberOfLikes.setText(noOfLikes);
                txtNumberOfComments.setText(noOfComments);
                txtPostDescription.setText(post.getPostDescription());

                showComponents();

                progressBar.setVisibility(View.GONE);
                txtProgress.setVisibility(View.GONE);
                progressBar.setProgress(0);

                handler.postDelayed(this, presentInterval);
            }
        };

        handler.post(imagePresentationLoader);
    }

    private List<InstagramPost> filterPostsToDisplay(List<InstagramPost> posts) {
        List<InstagramPost> postsToDisplay = new ArrayList<>();

        for (InstagramPost post : posts) {
            boolean shouldBeAdded = true;

            // Check type: currently only get images and skip videos
            if (!post.getType().equalsIgnoreCase(Constants.INSTAGRAM_IMAGE_TYPE_NAME)) {
                continue;
            }

            // Check with excluded hashtags
            if (excludedHashtags != null) {
                for (String excludedHashtag : excludedHashtags) {
                    if (post.getPostDescription().toLowerCase().contains(excludedHashtag.toLowerCase())) {
                        shouldBeAdded = false;
                        break;
                    }
                }
            }

            if (shouldBeAdded) {
                postsToDisplay.add(post);
            }

            // Check maximum number of posts
            if (postsToDisplay.size() == numberOfPostsToDisplay) {
                break;
            }
        }

        return postsToDisplay;
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
        imgMainWidth = getIntValueFromPref(imgMainWidthPrefKey, 0); //Width is initialized as screen's width in MainActivity
        imgMainHeight = getIntValueFromPref(imgMainHeightPrefKey, 0); //Height is initialized as 3/4 of screen's width in MainActivity
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
}