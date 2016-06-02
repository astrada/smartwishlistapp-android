package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Locale;

public class WebSiteActivity extends AppCompatActivity {

    public final static String TARGET_PAGE_EXTRA = "TargetPage";
    public final static String TARGET_PAGE_QUERY_STRING_EXTRA = "TargetPageQueryString";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_site);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        assert progressBar != null;
        progressBar.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebView webView = (WebView) findViewById(R.id.web_view);
        assert webView != null;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(false);
        webView.addJavascriptInterface(new WebAppInterface(getApplicationContext()),
                AppConstants.JAVASCRIPT_INTERFACE);
        webView.setWebViewClient(new SmartWishListWebViewClient(progressBar));
        webView.setWebChromeClient(new SmartWishListWebChromeClient(progressBar));
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
        String queryString = getIntent().getStringExtra(TARGET_PAGE_QUERY_STRING_EXTRA);
        if (queryString != null) {
            if (url.contains("?")) {
                url += "&";
            } else {
                url += "?";
            }
            url += queryString;
        }
        webView.loadUrl(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleServicesHelper.GetTokenAndSendToServerAsyncTask task =
                new GoogleServicesHelper.GetTokenAndSendToServerAsyncTask(this);
        task.execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = (WebView) findViewById(R.id.web_view);
        assert webView != null;
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebView webView = (WebView) findViewById(R.id.web_view);
        if (webView != null) {
            ViewGroup viewGroup = (ViewGroup) webView.getParent();
            if(null != viewGroup){
                viewGroup.removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
        }
    }

    private class SmartWishListWebViewClient extends WebViewClient {

        private final ProgressBar progressBar;

        public SmartWishListWebViewClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (BuildConfig.DEBUG) {
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

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressBar.setVisibility(View.GONE);
        }
    }

    static void addLanguageToWebSiteIntent(Intent intent) {
        String language = Locale.getDefault().getLanguage();
        intent.putExtra(TARGET_PAGE_QUERY_STRING_EXTRA, "hl=" + language);
    }

    private class SmartWishListWebChromeClient extends WebChromeClient {

        private final ProgressBar progressBar;

        public SmartWishListWebChromeClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }
    }
}
