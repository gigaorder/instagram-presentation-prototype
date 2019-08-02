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

public class SettingsFragment extends PreferenceFragmentCompat {
    private RequestQueue requestQueue;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        requestQueue = Volley.newRequestQueue(getContext());

        EditTextPreference instagramSourcePref = getPreferenceScreen().findPreference("instagram_source");
        instagramSourcePref.setOnPreferenceChangeListener(sourceUrlPreferenceValidator);

        Preference btnBack = findPreference(getString(R.string.pref_back_button));
        btnBack.setOnPreferenceClickListener(btnBackListener);
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

    private Preference.OnPreferenceClickListener btnBackListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            getActivity().onBackPressed();

            return true;
        }
    };
}
