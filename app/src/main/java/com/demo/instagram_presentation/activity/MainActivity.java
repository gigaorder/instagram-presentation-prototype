package com.demo.instagram_presentation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.demo.instagram_presentation.BuildConfig;
import com.demo.instagram_presentation.R;
import com.demo.instagram_presentation.broadcast_receiver.WifiScanResultReceiver;
import com.demo.instagram_presentation.fragment.ConfigFragment;
import com.demo.instagram_presentation.fragment.ImageSlideFragment;
import com.demo.instagram_presentation.util.AppExceptionHandler;
import com.demo.instagram_presentation.util.AppPreferencesUtil;
import com.demo.instagram_presentation.util.BroadcastReceiverUtil;
import com.demo.instagram_presentation.util.Constants;
import com.demo.instagram_presentation.util.LicenseUtil;
import com.demo.instagram_presentation.util.NetworkUtil;
import com.demo.instagram_presentation.util.PermissionUtil;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindString(R.string.login_error_intent_key)
    String loginErrorMsgIntentKey;
    @BindView(R.id.main_activity_app_message)
    TextView appMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LicenseUtil.initKeyIdFile();
        AppPreferencesUtil.initSharedPreference(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(this));

        setUpServices();
        setUpUI();
    }

    private void setUpUI() {
        setFullScreen();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PermissionUtil.askForRequiredPermissions(MainActivity.this);

        boolean deviceBoot = getIntent().getBooleanExtra("deviceBoot", false);
        if (!deviceBoot && AppPreferencesUtil.isAbleToDisplaySlideshow()) {
            showFragment(new ImageSlideFragment());
        } else {
            showFragment(new ConfigFragment());
        }
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().getDecorView().setSystemUiVisibility
                ( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_activity_fragment_container, fragment).commit();
    }

    private void setUpServices() {
        NetworkUtil.initNetworkService();
        initBroadcastReceivers();

        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.TOPIC);
    }

    private WifiScanResultReceiver wifiScanResultReceiver;
    private BroadcastReceiver showImageSlideReceiver;
    private BroadcastReceiver showConfigScreenReceiver;
    private BroadcastReceiver displayAppMessageReceiver;

    private void initBroadcastReceivers() {
        showImageSlideReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent restartIntent = new Intent(MainActivity.this, MainActivity.class);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(restartIntent);
            }
        };

        showConfigScreenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showFragment(new ConfigFragment());
            }
        };

        displayAppMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                appMessage.setVisibility(View.VISIBLE);
                appMessage.setText(message);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register Broadcast receivers
        IntentFilter ifPrefChanged = new IntentFilter(Constants.PREFERENCE_CHANGED_ACTION);
        registerReceiver(showImageSlideReceiver, ifPrefChanged);
        IntentFilter ifLoginInfoChanged = new IntentFilter(Constants.LOGIN_INFO_CHANGED_ACTION);
        registerReceiver(showImageSlideReceiver, ifLoginInfoChanged);
        IntentFilter ifShowImageSlide = new IntentFilter(Constants.SHOW_IMAGE_SLIDE_ACTION);
        registerReceiver(showImageSlideReceiver, ifShowImageSlide);

        IntentFilter ifLoginFailed = new IntentFilter(Constants.LOGIN_FAILED_ACTION);
        registerReceiver(showConfigScreenReceiver, ifLoginFailed);

        wifiScanResultReceiver = new WifiScanResultReceiver();
        IntentFilter ifWifiScanResult = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanResultReceiver, ifWifiScanResult);

        IntentFilter ifNoInternet = new IntentFilter(Constants.NO_INTERNET_ACTION);
        registerReceiver(showConfigScreenReceiver, ifNoInternet);

        IntentFilter ifDisplayAppMessage = new IntentFilter(Constants.DISPLAY_APP_MESSAGE_ACTION);
        registerReceiver(displayAppMessageReceiver, ifDisplayAppMessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BroadcastReceiverUtil.unregisterReceiver(this, showImageSlideReceiver);
        BroadcastReceiverUtil.unregisterReceiver(this, wifiScanResultReceiver);
        BroadcastReceiverUtil.unregisterReceiver(this, showConfigScreenReceiver);
        BroadcastReceiverUtil.unregisterReceiver(this, displayAppMessageReceiver);
    }
}