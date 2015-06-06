package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_site, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
