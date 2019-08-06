package com.demo.instagram_presentation.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.util.InstagramUtil;
import com.demo.instagram_presentation.util.ScreenUtil;

import butterknife.BindString;
import butterknife.ButterKnife;

public class SettingsFragment extends PreferenceFragmentCompat {
    private RequestQueue requestQueue;

    @BindString(R.string.pref_instagram_source)
    String instagramSourcePrefKey;
    @BindString(R.string.pref_img_main_width)
    String imgMainWidthPrefKey;
    @BindString(R.string.pref_img_main_height)
    String imgMainHeightPrefKey;
    @BindString(R.string.pref_img_main_width_summary)
    String imgMainWidthPrefSummary;
    @BindString(R.string.pref_img_main_height_summary)
    String imgMainHeightPrefSummary;
    @BindString(R.string.pref_back_button)
    String btnBackPrefKey;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        ButterKnife.bind(this, getActivity());

        requestQueue = Volley.newRequestQueue(getContext());

        int screenHeight = ScreenUtil.getScreenHeight(getActivity());
        int screenWidth = ScreenUtil.getScreenWidth(getActivity());

        EditTextPreference instagramSourcePref = getPreferenceScreen().findPreference(instagramSourcePrefKey);
        EditTextPreference imgMainWidth = getPreferenceScreen().findPreference(imgMainWidthPrefKey);
        EditTextPreference imgMainHeight = getPreferenceScreen().findPreference(imgMainHeightPrefKey);
        Preference btnBack = findPreference(btnBackPrefKey);

        instagramSourcePref.setOnPreferenceChangeListener(sourceUrlPreferenceValidator);
        btnBack.setOnPreferenceClickListener(btnBackListener);
        imgMainWidth.setSummary(String.format(imgMainWidthPrefSummary, screenWidth));
        imgMainHeight.setSummary(String.format(imgMainHeightPrefSummary, screenHeight));
    }

    private Preference.OnPreferenceChangeListener sourceUrlPreferenceValidator = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, final Object newValue) {
            String userInfoRequestUrl = InstagramUtil.constructInstagramUserInfoUrl(newValue.toString());

            StringRequest instagramUserInfoRequest = new StringRequest(
                    userInfoRequestUrl,
                    null,
                    error -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Invalid URL");
                        builder.setMessage(newValue + " is not a valid Instagram user URL");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                    }
            );

            requestQueue.add(instagramUserInfoRequest);
            return true;
        }
    };

    private Preference.OnPreferenceClickListener btnBackListener = preference -> {
        getActivity().onBackPressed();
        return true;
    };
}
