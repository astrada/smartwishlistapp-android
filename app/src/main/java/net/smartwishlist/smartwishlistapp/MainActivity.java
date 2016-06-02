package net.smartwishlist.smartwishlistapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_TYPE_TEXT_PLAIN = "text/plain";

    private AppInitialization appInitialization;
    private long lastClickTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        appInitialization = new AppInitialization(getApplicationContext());
        appInitialization.initializeApp();

        GoogleServicesHelper.checkPlayServices(this);
        if (checkInitialization()) {
            receiveData();

            setContentView(R.layout.activity_main);
            if (!BuildConfig.DEBUG) {
                Button refresh = (Button) findViewById(R.id.button_refresh);
                assert refresh != null;
                refresh.setVisibility(View.GONE);
            }
        }
    }

    private void receiveData() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && ACTION_TYPE_TEXT_PLAIN.equals(type)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && sharedText.matches(".*https?://www.amazon\\..*")) {
                int urlStart = sharedText.indexOf("http");
                String keywords = sharedText.substring(urlStart);
                AppPreferences preferences = new AppPreferences(getApplicationContext());
                String clientId = preferences.getClientId();
                String token = preferences.getToken();
                if (clientId == null || token == null) {
                    Toast toast = Toast.makeText(this, R.string.invalid_client_id,
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                double timestamp = ApiSignature.getTimestamp();
                String signature = ApiSignature.generateRequestSignature(
                        token, keywords, timestamp);
                Uri.Builder uriBuilder =
                        Uri.parse(AppConstants.SEARCH_RESULTS_PAGE).buildUpon();
                uriBuilder.appendQueryParameter("clientId", clientId);
                uriBuilder.appendQueryParameter("keywords", keywords);
                uriBuilder.appendQueryParameter("timestamp",
                        String.format(Locale.US, "%.3f", timestamp));
                uriBuilder.appendQueryParameter("signature", signature);
                String url = uriBuilder.build().toString();
                Intent webSiteActivityIntent = new Intent(this, WebSiteActivity.class);
                webSiteActivityIntent.putExtra(WebSiteActivity.TARGET_PAGE_EXTRA, url);
                WebSiteActivity.addLanguageToWebSiteIntent(webSiteActivityIntent);
                startActivity(webSiteActivityIntent);
            } else {
                Toast toast = Toast.makeText(this, R.string.action_send_error,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GoogleServicesHelper.checkPlayServices(this)
                && checkInitialization()) {
            checkConnectivity();
        }
    }

    private boolean checkInitialization() {
        boolean needSetup = appInitialization.needSetup();
        if (needSetup) {
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
        }
        return !needSetup;
    }

    private boolean checkConnectivity() {
        boolean connected = ApiService.isConnected(getApplicationContext());
        if (!connected) {
            Toast toast = Toast.makeText(this,
                    R.string.no_internet_connection,
                    Toast.LENGTH_LONG);
            toast.show();
        }
        return connected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openMyWishLists(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, WebSiteActivity.class);
        intent.putExtra(WebSiteActivity.TARGET_PAGE_EXTRA, AppConstants.MY_WISH_LISTS_PAGE);
        WebSiteActivity.addLanguageToWebSiteIntent(intent);
        startActivity(intent);
    }

    public void openSearchPage(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, WebSiteActivity.class);
        intent.putExtra(WebSiteActivity.TARGET_PAGE_EXTRA, AppConstants.SEARCH_PAGE);
        WebSiteActivity.addLanguageToWebSiteIntent(intent);
        startActivity(intent);
    }

    public void reset(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation))
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
                            return;
                        }
                        lastClickTimestamp = SystemClock.elapsedRealtime();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void refresh(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        ApiService.FetchAppNotificationsTask task = new ApiService.FetchAppNotificationsTask(this);
        task.execute();
    }
}
