package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListCheckResult;
import com.crashlytics.android.Crashlytics;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;

    private static final int CLIENT_ID_LENGTH = 36;
    private static final int TOKEN_LENGTH = 64;
    private static final int DEFAULT_REGION_LENGTH = 2;
    private static final int HAS_ACCOUNT_LENGTH = 1;
    private static final int QR_CODE_LENGTH = CLIENT_ID_LENGTH + TOKEN_LENGTH +
            DEFAULT_REGION_LENGTH + HAS_ACCOUNT_LENGTH;
    private static final int TOKEN_OFFSET = CLIENT_ID_LENGTH;
    private static final int DEFAULT_REGION_OFFSET = TOKEN_OFFSET + TOKEN_LENGTH;
    private static final int HAS_ACCOUNT_OFFSET = DEFAULT_REGION_OFFSET + DEFAULT_REGION_LENGTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            AppPreferences preferences = new AppPreferences(this);
            preferences.beginEdit();
            preferences.setClientId(AppSettings.DEBUG_CLIENT_ID);
            preferences.setToken(AppSettings.DEBUG_TOKEN);
            preferences.setDefaultRegion(AppSettings.DEBUG_DEFAULT_REGION);
            preferences.setHasAccount(AppSettings.DEBUG_HAS_ACCOUNT);
            preferences.commit();
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onClickQrCode(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
            if (resultCode == RESULT_OK) {
                String scanResult = intent.getExtras().getString("SCAN_RESULT");
                if (scanResult.length() == QR_CODE_LENGTH) {
                    String clientId = scanResult.substring(0, CLIENT_ID_LENGTH).toLowerCase();
                    String token = scanResult.substring(TOKEN_OFFSET, DEFAULT_REGION_OFFSET).toLowerCase();
                    String defaultRegion = scanResult.substring(DEFAULT_REGION_OFFSET,
                            HAS_ACCOUNT_OFFSET).toUpperCase();
                    String hasAccount = scanResult.substring(HAS_ACCOUNT_OFFSET);
                    AppPreferences preferences = new AppPreferences(this);
                    preferences.beginEdit();
                    preferences.setClientId(clientId);
                    preferences.setToken(token);
                    preferences.setDefaultRegion(defaultRegion);
                    preferences.setNotificationEnabled(true);
                    if (hasAccount.equals("1")) {
                        preferences.setHasAccount("true");
                    } else {
                        preferences.setHasAccount("false");
                    }
                    preferences.commit();
                    CheckClientIdTask task = new CheckClientIdTask();
                    task.execute(clientId);
                } else {
                    // TODO
                    Log.d(AppConstants.LOG_TAG, "Invalid QR code");
                }
            } else {
                // TODO
                Log.d(AppConstants.LOG_TAG, "QR code not detected");
            }
        }
    }

    public void onClickStartService(View view) {
        Intent serviceIntent = new Intent(this, DataPullService.class);
        startService(serviceIntent);
    }

    public void onClickStartSite(View view) {
        Intent intent = new Intent(this, WebSiteActivity.class);
        startActivity(intent);
    }

    private class CheckClientIdTask extends AsyncTask<String, Void, SmartWishListCheckResult> {

        private final Smartwishlist service;

        public CheckClientIdTask() {
            this.service = AppConstants.getApiServiceHandle();
        }

        protected SmartWishListCheckResult doInBackground(String... strings) {
            SmartWishListCheckResult result = null;
            try {
                Smartwishlist.Client.CheckId checkId = service.client().checkId(strings[0]);
                checkId.setIsApp(true);
                result = checkId.execute();
            } catch (IOException e) {
                Log.d(AppConstants.LOG_TAG, e.getMessage(), e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(SmartWishListCheckResult smartWishListCheckResult) {
            Boolean isValid = smartWishListCheckResult.getValid();
            if (!isValid) {
                // TODO
                Log.d(AppConstants.LOG_TAG, "Invalid clientID");
            }
        }
    }
}
