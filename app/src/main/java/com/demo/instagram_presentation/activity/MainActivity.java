package com.demo.instagram_presentation.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.fragment.ImagePresentationFragment;
import com.demo.instagram_presentation.fragment.SettingsFragment;
import com.demo.instagram_presentation.util.Constants;

public class MainActivity extends AppCompatActivity {
    public final static int FRAGMENT_CONTAINER_ID = R.id.settings_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(FRAGMENT_CONTAINER_ID, new ImagePresentationFragment())
                .commit();
    }
}