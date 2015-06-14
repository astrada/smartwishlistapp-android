package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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

        WebView webView = (WebView) findViewById(R.id.mainWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(false);
        webView.addJavascriptInterface(new WebAppInterface(this),
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
        WebView webView = (WebView) findViewById(R.id.mainWebView);
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
            } else if (url.startsWith(AppConstants.WEB_SITE_URL)) {
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        }
    }
}
