package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.fabric.sdk.android.Fabric;

public class AppInitialization {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final Context context;
    private final AppPreferences preferences;

    public AppInitialization(Context context) {
        this.context = context;
        this.preferences = new AppPreferences(context);
    }

    public AppInitialization(Context context, AppPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    public static boolean checkPlayServices(Activity activity) {
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

    public void initializeApp() {
        Crashlytics kit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(context, kit);

        if (BuildConfig.DEBUG) {
            preferences.beginEdit();
//            preferences.setClientId(BuildConfig.DEBUG_CLIENT_ID);
//            preferences.setToken(BuildConfig.DEBUG_TOKEN);
//            preferences.setDefaultRegion(BuildConfig.DEBUG_DEFAULT_REGION);
//            preferences.setHasAccount(BuildConfig.DEBUG_HAS_ACCOUNT);
//            preferences.setNotificationEnabled(BuildConfig.DEBUG_NOTIFICATION_ENABLED);
            preferences.apply();
        }
        if (!needSetup()) {
            Crashlytics.setString(AppConstants.CLIENT_ID_TAG, preferences.getClientId());
        }
    }

    public boolean needSetup() {
        return preferences.getClientId() == null;
    }

    public void sendTokenToServer() {
        if (!preferences.isGcmTokenSent()
                && preferences.getClientId() != null) {
            ApiService.GetTokenAndSendToServerAsyncTask task =
                    new ApiService.GetTokenAndSendToServerAsyncTask(context);
            task.execute();
        }
    }

    public void deleteTokenFromServer(String oldClientId) {
        if (preferences.isGcmTokenSent()
                && oldClientId != null) {
            ApiService.DeleteTokenFromServerAsyncTask task =
                    new ApiService.DeleteTokenFromServerAsyncTask(oldClientId,
                            context);
            task.execute();
        }
    }

    public void modifyClientId(String newClientId) {
        String oldClientId = preferences.getClientId();
        preferences.setClientId(newClientId);
        if (!BuildConfig.DEBUG) {
            if (newClientId != null) {
                Crashlytics.setString(AppConstants.CLIENT_ID_TAG, newClientId);
            } else {
                Crashlytics.setBool(AppConstants.RESET_CLIENT_ID_TAG, true);
            }
        }
        if (newClientId == null) {
            deleteTokenFromServer(oldClientId);
        }
        else if (!newClientId.equals(oldClientId)) {
            // Re-enable notifications when Client ID changes
            preferences.setNotificationEnabled(true);

            // Re-send registration token to server
            preferences.setGcmTokenSent(false);
            sendTokenToServer();
        }
    }

    public void resetClientId() {
        modifyClientId(null);
    }
}
