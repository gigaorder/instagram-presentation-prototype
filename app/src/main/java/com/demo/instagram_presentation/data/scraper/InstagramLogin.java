package com.demo.instagram_presentation.data.scraper;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.Constants;

public class InstagramLogin {
    private final String TAG = MainActivity.DEVICE_ID;

    private final String ACCOUNT_INFO_INCORRECT = "Login username or password is not correct.\nPlease provide correct info on config website.";
    private final String ACCOUNT_INFO_EMPTY = "Require instagram account to get post data.\nPlease provide login info on config website.";
    private final String REQUIRE_SECURITY_CODE = "Login action requires security code.\nPlease try another account.";

    private String username;
    private String password;

    private WebView webView;
    private boolean firstStepLoginSuccess;

    public InstagramLogin(String username, String password, WebView webView) {
        this.username = username;
        this.password = password;
        this.webView = webView;
    }

    public void executeLoginInstagram(ExecutingLoginListener executingLoginListener) {
        clearCookies();

        this.webView.setWebViewClient(new WebViewClient() {

            // this login-page will redirect to itself or another page like homepage or challenge
            // onPageFinished will be called multiple times because of redirect
            @Override
            public void onPageFinished(WebView view, String webViewUrl) {
                if (webViewUrl.contains(Constants.LOGIN_URL)) {
                    // this code will be called multiple times : first time or when login-page redirect to itself
                    new Handler().postDelayed(() -> {
                        executeLoginWithJSCode(verifySuccess -> {
                            if (verifySuccess) {
                                firstStepLoginSuccess = true;
                            } else if(username.isEmpty() || password.isEmpty()) {
                                executingLoginListener.onFinish(new LoginStatus(false, ACCOUNT_INFO_EMPTY));
                            } else {
                                executingLoginListener.onFinish(new LoginStatus(false, ACCOUNT_INFO_INCORRECT));
                            }
                        });
                    }, 1000);
                } else if (firstStepLoginSuccess && !webViewUrl.contains("challenge")) {
                    // when login success , this page will redirect to homepage
                    executingLoginListener.onFinish(new LoginStatus(true, null));
                } else {
                    // when instagram prevent ddos, this page will redirect to page challenge (security code required)
                    //todo : handle this case
                    executingLoginListener.onFinish(new LoginStatus(false, REQUIRE_SECURITY_CODE));
                }
            }
        });

        webView.loadUrl(Constants.LOGIN_URL);
    }

    private void executeLoginWithJSCode(JobFinishedStatus verifyAccountCallback) {
        String checkFormExistThenClickLoginBtn = String.format("(function () {\n" +
                "        var forms = document.forms;\n" +
                "        var username, password;\n" +
                "        for (var i = 0; i < forms.length; i++) {\n" +
                "          if (forms[i].username !== undefined) {\n" +
                "            username = forms[i].username;\n" +
                "          }\n" +
                "          if (forms[i].password !== undefined) {\n" +
                "            password = forms[i].password;\n" +
                "          }\n" +
                "        }\n" +
                "\n" +
                "        if (username === undefined || password === undefined) {\n" +
                "          return false;\n" +
                "        }\n" +
                "\n" +
                "        var event = new Event('input', { bubbles: true });\n" +
                "        event.simulated = true;\n" +
                "\n" +
                "        var lastUsernameValue = username.value;\n" +
                "        username.value = '%s';\n" +
                "\n" +
                "        var usernameTracker = username._valueTracker;\n" +
                "        if (usernameTracker) {\n" +
                "          usernameTracker.setValue(lastUsernameValue);\n" +
                "        }\n" +
                "        username.dispatchEvent(event);\n" +
                "\n" +
                "        var lastPasswordValue = password.value;\n" +
                "        password.value = '%s';\n" +
                "        var passwordTracker = password._valueTracker;\n" +
                "        if (passwordTracker) {\n" +
                "          passwordTracker.setValue(lastPasswordValue);\n" +
                "        }\n" +
                "        password.dispatchEvent(event);\n" +
                "\n" +
                "        var buttons = document.getElementsByTagName('button');\n" +
                "        for (var j = 0; j < buttons.length; j++) {\n" +
                "          if (buttons[j].getAttribute('type') === 'submit') {\n" +
                "            buttons[j].disabled = false;\n" +
                "            buttons[j].click();\n" +
                "            return true;\n" +
                "          }\n" +
                "        }\n" +
                "        return false;\n" +
                "      })();", username, password);

        webView.evaluateJavascript(checkFormExistThenClickLoginBtn, clickLoginBtnSuccess -> {
            if (clickLoginBtnSuccess.equals("true")) {
                new Handler().postDelayed(() -> {
                    String checkLoginSuccessScript = "(function () {return document.getElementById('slfErrorAlert') ? false : true})();";
                    webView.evaluateJavascript(checkLoginSuccessScript, loginSuccess -> verifyAccountCallback.finish(loginSuccess.equals("true")));
                }, 1000);
            } else {
                new Handler().postDelayed(() -> executeLoginWithJSCode(verifyAccountCallback), 1000);
            }
        });
    }

    private void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bugfender.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Bugfender.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(webView.getContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private interface JobFinishedStatus {
        void finish(boolean status);
    }

    public interface ExecutingLoginListener {
        void onFinish(LoginStatus loginStatus);
    }

    class LoginStatus {
        boolean success;
        String message;

        public LoginStatus(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
