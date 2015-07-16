package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebSiteActivity extends AppCompatActivity {

    public final static String TARGET_PAGE_EXTRA = "TargetPage";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_site);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebView webView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(false);
        webView.addJavascriptInterface(new WebAppInterface(getApplicationContext()),
                AppConstants.JAVASCRIPT_INTERFACE);
        webView.setWebViewClient(new SmartWishListWebViewClient());
        String url;
        if (BuildConfig.DEBUG) {
            url = BuildConfig.LOCAL_WEB_SITE_URL;
        } else {
            url = AppConstants.WEB_SITE_URL;
        }
        String page = getIntent().getStringExtra(TARGET_PAGE_EXTRA);
        if (page != null) {
            url += page;
        }
        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = (WebView) findViewById(R.id.web_view);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private class SmartWishListWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (BuildConfig.DEBUG && url.startsWith(BuildConfig.LOCAL_WEB_SITE_URL)) {
                return false;
            } else if (url.startsWith(AppConstants.WEB_SITE_URL) ||
                    url.startsWith(AppConstants.GOOGLE_API_URL) ||
                    url.startsWith(AppConstants.GOOGLE_ACCOUNTS_URL) ||
                    url.startsWith(AppConstants.GOOGLE_STATIC_URL) ||
                    url.startsWith(AppConstants.GOOGLE_OAUTH_URL) ||
                    url.startsWith(AppConstants.SMART_WISH_LIST_API)) {
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        }
    }
}
