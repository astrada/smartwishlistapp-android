package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListCheckResult;
import com.crashlytics.android.Crashlytics;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class AppInitialization {

    private static final int CLIENT_ID_LENGTH = 36;
    private static final int TOKEN_LENGTH = 64;
    private static final int DEFAULT_REGION_LENGTH = 2;
    private static final int HAS_ACCOUNT_LENGTH = 1;
    private static final int QR_CODE_LENGTH = CLIENT_ID_LENGTH + TOKEN_LENGTH +
            DEFAULT_REGION_LENGTH + HAS_ACCOUNT_LENGTH;
    private static final int TOKEN_OFFSET = CLIENT_ID_LENGTH;
    private static final int DEFAULT_REGION_OFFSET = TOKEN_OFFSET + TOKEN_LENGTH;
    private static final int HAS_ACCOUNT_OFFSET = DEFAULT_REGION_OFFSET + DEFAULT_REGION_LENGTH;

    private final Activity activity;
    private GcmInitialization gcmInitialization;

    public AppInitialization(Activity activity) {
        this.activity = activity;
    }

    public GcmInitialization getGcmInitialization() {
        return gcmInitialization;
    }

    public void initializeApp() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(activity, new Crashlytics());
        }
        if (BuildConfig.DEBUG) {
            AppPreferences preferences = new AppPreferences(activity);
            preferences.beginEdit();
            preferences.setClientId(AppSettings.DEBUG_CLIENT_ID);
            preferences.setToken(AppSettings.DEBUG_TOKEN);
            preferences.setDefaultRegion(AppSettings.DEBUG_DEFAULT_REGION);
            preferences.setHasAccount(AppSettings.DEBUG_HAS_ACCOUNT);
            preferences.apply();
        }
        gcmInitialization = new GcmInitialization(activity);
        gcmInitialization.initializeGcm();
    }

    public void storeStateFromQrCode(Intent intent) {
        String scanResult = intent.getExtras().getString("SCAN_RESULT");
        if (scanResult.length() == QR_CODE_LENGTH) {
            String clientId = scanResult.substring(0, CLIENT_ID_LENGTH).toLowerCase();
            String token = scanResult.substring(TOKEN_OFFSET, DEFAULT_REGION_OFFSET).toLowerCase();
            String defaultRegion = scanResult.substring(DEFAULT_REGION_OFFSET,
                    HAS_ACCOUNT_OFFSET).toUpperCase();
            String hasAccount = scanResult.substring(HAS_ACCOUNT_OFFSET);
            AppPreferences preferences = new AppPreferences(activity);
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
            preferences.apply();
            CheckClientIdTask task = new CheckClientIdTask();
            task.execute(clientId);
        } else {
            // TODO
            Log.d(AppConstants.LOG_TAG, "Invalid QR code");
        }
    }

    private class CheckClientIdTask extends AsyncTask<String, Void, SmartWishListCheckResult> {

        private final Smartwishlist service;

        public CheckClientIdTask() {
            this.service = AppConstants.getApiServiceHandle(activity);
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
