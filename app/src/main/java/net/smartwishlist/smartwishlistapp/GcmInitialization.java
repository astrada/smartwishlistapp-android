package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmInitialization {

    private static final String TAG = "GcmInitialization";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final Activity activity;
    private final AppPreferences preferences;

    public GcmInitialization(Activity activity, AppPreferences preferences) {
        this.activity = activity;
        this.preferences = preferences;
    }

    public void initializeGcmToken() {
        if (!preferences.isGcmTokenSent() && checkPlayServices()) {
            Intent intent = new Intent(activity, GcmRegistrationIntentService.class);
            activity.startService(intent);
        }
    }

    public boolean deleteGcmToken() {
        try {
            synchronized (TAG) {
                InstanceID.getInstance(activity).deleteInstanceID();
                preferences.setGcmTokenSent(false);
                return true;
            }
        } catch (IOException e) {
            AppLogging.logException(e);
            return false;
        }
    }

    private boolean checkPlayServices() {
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
