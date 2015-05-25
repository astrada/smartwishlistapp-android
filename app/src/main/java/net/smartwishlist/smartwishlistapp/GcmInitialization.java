package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListRegisterGcmDeviceParameters;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmInitialization {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final Activity activity;
    private final Context applicationContext;
    private GoogleCloudMessaging gcm;
    private String registrationId;
    private AppPreferences preferences;

    public GcmInitialization(Activity activity) {
        this.activity = activity;
        this.applicationContext = activity.getApplicationContext();
    }

    public void initializeGcm() {
        preferences = new AppPreferences(applicationContext);
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(activity);
            registrationId = getRegistrationId(applicationContext);

            if (registrationId == null) {
                registerInBackground();
            }
        } else {
            Log.d(AppConstants.LOG_TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(AppConstants.LOG_TAG, "This device is not supported.");
                // TODO: error message
                activity.finish();
            }
            return false;
        }
        return true;
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = AppConstants.Version.getAppVersion(context);
        preferences.beginEdit();
        preferences.setGcmRegistrationId(regId);
        preferences.setApplicationVersion(appVersion);
        preferences.apply();
    }

    @Nullable
    private String getRegistrationId(Context context) {
        String registrationId = preferences.getGcmRegistrationId();
        if (registrationId == null) {
            Log.d(AppConstants.LOG_TAG, "Registration not found.");
            return null;
        }
        int registeredVersion = preferences.getApplicationVersion();
        int currentVersion = AppConstants.Version.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(AppConstants.LOG_TAG, "App version changed.");
            return null;
        }
        return registrationId;
    }

    private void registerInBackground() {
        RegisterInBackgroundTask task = new RegisterInBackgroundTask();
        task.execute();
    }

    private class RegisterInBackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String msg;
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(applicationContext);
                }
                registrationId = gcm.register(AppSettings.SENDER_ID);
                msg = "Device registered, registration ID=" + registrationId;

                sendRegistrationIdToBackend();

                storeRegistrationId(applicationContext, registrationId);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // TODO
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            // TODO
            Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void sendRegistrationIdToBackend() {
        RegisterGcmDeviceTask task = new RegisterGcmDeviceTask();
        task.execute(registrationId);
    }

    private class RegisterGcmDeviceTask extends AsyncTask<String, Void, Void> {

        private final Smartwishlist service;

        public RegisterGcmDeviceTask() {
            this.service = AppConstants.getApiServiceHandle(applicationContext);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String clientId = preferences.getClientId();
                String token = preferences.getToken();
                double timestamp = System.currentTimeMillis() / 1000.0;
                String registrationId = strings[0];
                String signature = ApiSignature.generateRequestSignature(
                        token, registrationId, timestamp);
                SmartWishListRegisterGcmDeviceParameters parameters =
                        new SmartWishListRegisterGcmDeviceParameters();
                parameters.setRegistrationId(registrationId);
                Smartwishlist.AppNotifications.Register request =
                        service.appNotifications().register(clientId, timestamp, signature,
                                parameters);
                request.setIsApp(true);
                request.execute();
            } catch (IOException e) {
                Log.d(AppConstants.LOG_TAG, e.getMessage(), e);
            }
            return null;
        }
    }
}
