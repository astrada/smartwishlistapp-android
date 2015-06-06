package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GcmInitialization {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final Activity activity;
    private final AppPreferences preferences;

    public GcmInitialization(Activity activity, AppPreferences preferences) {
        this.activity = activity;
        this.preferences = preferences;
    }

    public void initializeGcm() {
        if (!preferences.isGcmTokenSent() && checkPlayServices()) {
            Intent intent = new Intent(activity, GcmRegistrationIntentService.class);
            activity.startService(intent);
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
