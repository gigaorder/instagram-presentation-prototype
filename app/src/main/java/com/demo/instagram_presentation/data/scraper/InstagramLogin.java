package com.demo.instagram_presentation.data.scraper;

import android.os.Build;
import android.os.Handler;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bugfender.sdk.Bugfender;
import com.demo.instagram_presentation.InstagramApplicationContext;
import com.demo.instagram_presentation.activity.MainActivity;
import com.demo.instagram_presentation.util.Constants;

public class InstagramLogin {
    private final String TAG = InstagramApplicationContext.DEVICE_ID;

    public static final int LOGIN_SUCCESS = 0;

    public static final int ACCOUNT_INFO_INCORRECT_CODE = -1;
    private final String ACCOUNT_INFO_INCORRECT_MSG = "Login username or password is not correct.\nPlease provide correct info on config website.";

    public static final int ACCOUNT_INFO_EMPTY_CODE = -2;
    private final String ACCOUNT_INFO_EMPTY_MSG = "Require instagram account to get post data.\nPlease provide login info on config website.";

    public static final int LOGIN_CHALLENGE_CODE = -3;
    private final String LOGIN_CHALLENGE_MSG = "Login action requires security code.\nPlease try another account.";

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
                                executingLoginListener.onFinish(new LoginStatus(false, ACCOUNT_INFO_EMPTY_CODE, ACCOUNT_INFO_EMPTY_MSG));
                            } else {
                                executingLoginListener.onFinish(new LoginStatus(false, ACCOUNT_INFO_INCORRECT_CODE, ACCOUNT_INFO_INCORRECT_MSG));
                            }
                        });
                    }, 1000);
                } else if (firstStepLoginSuccess && !webViewUrl.contains("challenge")) {
                    // when login success , this page will redirect to homepage
                    executingLoginListener.onFinish(new LoginStatus(true, LOGIN_SUCCESS, ""));
                } else {
                    // when instagram prevent ddos, this page will redirect to page challenge (security code required)
                    // this code will be called multiple times until challenge page is loaded fully
                    executingLoginListener.onFinish(new LoginStatus(false, LOGIN_CHALLENGE_CODE, LOGIN_CHALLENGE_MSG));
                    sendSecurityCodeToEmail();
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

    public interface JobFinishedStatus {
        void finish(boolean success);
    }

    public interface ExecutingLoginListener {
        void onFinish(LoginStatus loginStatus);
    }

    class LoginStatus {
        boolean success;
        int code;
        String message;

        public LoginStatus(boolean success, int code, String message) {
            this.success = success;
            this.code = code;
            this.message = message;
        }
    }

    private void sendSecurityCodeToEmail() {
        String clickSendEmailButtonScript = "(function () {\n" +
                "  try {\n" +
                "    var receiveEmailBtn = document.forms[0].getElementsByTagName('button')[0];\n" +
                "    if (receiveEmailBtn === undefined) {\n" +
                "      return false\n" +
                "    } else {\n" +
                "      receiveEmailBtn.click();\n" +
                "      return true;\n" +
                "    }\n" +
                "  } catch (e) {\n" +
                "    return false;\n" +
                "  }\n" +
                "})();";

        webView.evaluateJavascript(clickSendEmailButtonScript, (clickSuccess) -> {
            if (clickSuccess.equals("false")) {
                String loginChallengeUrl = webView.getUrl();
                webView.loadUrl(loginChallengeUrl);
            } else {
                String checkSendEmailSuccessScript = "(function () {return document.getElementById('form_error') ? false : true})();";
                webView.evaluateJavascript(checkSendEmailSuccessScript, (sendEmailSuccess) -> {
                    if (sendEmailSuccess.equals("fasle")) {
                        new Handler().postDelayed(this::sendSecurityCodeToEmail, 1000);
                    }
                });
            }
        });
    }

    public void getNewSecurityCode() {
        webView.evaluateJavascript("(function () { _replay() })()", (__) -> {});
    }

    public void executeSubmitSecurityCodeWithJSCode(String code) {
        String submitSecurityCodeScript = String.format("(function () {\n" +
                "  var forms = document.forms;\n" +
                "  var codeField;\n" +
                "  var submitBtn;\n" +
                "  for (i = 0; i < forms.length; i++) {\n" +
                "    if (forms[i].security_code !== undefined) {\n" +
                "      codeField = forms[i].security_code;\n" +
                "      submitBtn = forms[i].getElementsByTagName('button')[0];\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  if (codeField === undefined || submitBtn === undefined) {\n" +
                "    return false;\n" +
                "  }\n" +
                "\n" +
                "  var event = new Event('input', { bubbles: true });\n" +
                "  event.simulated = true;\n" +
                "\n" +
                "  var lastCodeValue = codeField.value;\n" +
                "  codeField.value = '%s';\n" +
                "\n" +
                "  var codeTracker = codeField._valueTracker;\n" +
                "  if (codeTracker) {\n" +
                "    codeTracker.setValue(lastCodeValue)\n" +
                "  }\n" +
                "  codeField.dispatchEvent(event);\n" +
                "  submitBtn.click();\n" +
                "  return true;\n" +
                "})();", code);

        webView.evaluateJavascript(submitSecurityCodeScript, (__) -> {});
    }

}
