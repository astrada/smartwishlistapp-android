package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListRegisterGcmDeviceParameters;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmInitialization {

    private static final Object SYNC_OBJECT = new Object();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public GcmInitialization() {
    }

    public void initializeGcmToken(Activity activity) {
        AppPreferences preferences = new AppPreferences(activity.getApplicationContext());
        if (!preferences.isGcmTokenSent()
                && preferences.getClientId() != null
                && checkPlayServices(activity)) {
            Intent intent = new Intent(activity, GcmRegistrationIntentService.class);
            activity.startService(intent);
        }
    }

    public void deleteGcmToken(Activity activity) {
        DeleteGcmTokenTask task = new DeleteGcmTokenTask(activity);
        task.execute();
    }

    private static boolean checkPlayServices(Activity activity) {
        if (BuildConfig.DEBUG) {
            return true;
        } else {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(resultCode)) {
                    googleApiAvailability.getErrorDialog(activity, resultCode,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    AppLogging.logError("This device is not supported.");
                    activity.finish();
                }
                return false;
            }
            return true;
        }
    }

    public static class GetTokenAndSendToServerTask
            extends SyncTaskWithExponentialBackoff {

        private final Context context;
        private final AppPreferences preferences;

        public GetTokenAndSendToServerTask(Context context) {
            this.context = context;
            preferences = new AppPreferences(context);
        }

        @Override
        protected void tryDoing() throws IOException {
            getGcmTokenAndSendToServer();
        }

        @Override
        protected void handleFailure(Exception e) {
            preferences.setGcmTokenSent(false);
            AppLogging.logException(e);
        }

        private void getGcmTokenAndSendToServer() throws IOException {
            if (preferences.isGcmTokenSent() || preferences.getClientId() == null) {
                return;
            }

            InstanceID instanceID = InstanceID.getInstance(context);
            String registrationId = instanceID.getToken(BuildConfig.GCM_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            if (registrationId != null) {
                String clientId = preferences.getClientId();
                if (preferences.isGcmTokenSent() || clientId == null) {
                    return;
                }

                String token = preferences.getToken();
                double timestamp = ApiSignature.getTimestamp();
                String signature = ApiSignature.generateRequestSignature(
                        token, registrationId, timestamp);
                SmartWishListRegisterGcmDeviceParameters parameters =
                        new SmartWishListRegisterGcmDeviceParameters();
                parameters.setRegistrationId(registrationId);
                Smartwishlist.AppNotifications.Register request =
                        ApiService.getApiServiceHandle(context).appNotifications().register(
                                clientId, timestamp, signature, parameters);
                request.setIsApp(Boolean.TRUE);
                request.execute();
                preferences.setGcmTokenSent(true);
            } else {
                preferences.setGcmTokenSent(false);
            }
        }
    }

    public static class GetTokenAndSendToServerAsyncTask
            extends AsyncTask<Void, Void, Void> {

        private final Context context;

        public GetTokenAndSendToServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GetTokenAndSendToServerTask task = new GetTokenAndSendToServerTask(context);
            task.doSynchronized();
            return null;
        }
    }

    private static class DeleteGcmTokenTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity activity;
        private final Context context;
        private ProgressDialog progressDialog;

        public DeleteGcmTokenTask(Activity activity) {
            this.activity = activity;
            context = activity.getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(activity,
                    activity.getString(R.string.reset_progress_title),
                    activity.getString(R.string.reset_progress_dialog),
                    true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                synchronized (SYNC_OBJECT) {
                    InstanceID.getInstance(context).deleteInstanceID();
                    AppPreferences preferences = new AppPreferences(context);
                    preferences.setGcmTokenSent(false);
                    return Boolean.TRUE;
                }
            } catch (IOException e) {
                AppLogging.logException(e);
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            if (aBoolean) {
                AppPreferences preferences = new AppPreferences(context);
                preferences.resetAll();
                Intent intent = new Intent(activity, SetupActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Toast toast = Toast.makeText(context,
                        R.string.error_during_reset,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
