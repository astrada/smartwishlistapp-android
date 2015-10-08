package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
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

    private static class DeleteGcmTokenTask extends AsyncTask<Void, Void, Boolean> {

        private final Activity activity;
        private final Context context;

        public DeleteGcmTokenTask(Activity activity) {
            this.activity = activity;
            context = activity.getApplicationContext();
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
