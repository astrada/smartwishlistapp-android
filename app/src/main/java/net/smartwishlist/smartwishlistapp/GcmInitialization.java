package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmInitialization {

    private static final String TAG = "GcmInitialization";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public GcmInitialization() {
    }

    public void initializeGcmToken(Activity activity) {
        AppPreferences preferences = new AppPreferences(activity.getApplicationContext());
        if (!preferences.isGcmTokenSent() && checkPlayServices(activity)) {
            Intent intent = new Intent(activity, GcmRegistrationIntentService.class);
            activity.startService(intent);
        }
    }

    public boolean deleteGcmToken(Context context) {
        try {
            synchronized (TAG) {
                InstanceID.getInstance(context).deleteInstanceID();
                AppPreferences preferences = new AppPreferences(context);
                preferences.setGcmTokenSent(false);
                return true;
            }
        } catch (IOException e) {
            AppLogging.logException(e);
            return false;
        }
    }

    private static boolean checkPlayServices(Activity activity) {
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
