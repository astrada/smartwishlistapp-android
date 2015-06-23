package net.smartwishlist.smartwishlistapp;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class AppInitialization {

    private final Context context;
    private final AppPreferences preferences;

    public AppInitialization(Context context) {
        this.context = context;
        preferences = new AppPreferences(context);
    }

    public void initializeApp() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(context, new Crashlytics());
        }
        if (BuildConfig.DEBUG) {
            preferences.beginEdit();
            preferences.setClientId(BuildConfig.DEBUG_CLIENT_ID);
            preferences.setToken(BuildConfig.DEBUG_TOKEN);
            preferences.setDefaultRegion(BuildConfig.DEBUG_DEFAULT_REGION);
            preferences.setHasAccount(BuildConfig.DEBUG_HAS_ACCOUNT);
            preferences.setNotificationEnabled(BuildConfig.DEBUG_NOTIFICATION_ENABLED);
            preferences.apply();
        } else if (!needSetup()) {
            Crashlytics.setString(AppConstants.CLIENT_ID_TAG, preferences.getClientId());
        }
    }

    public boolean needSetup() {
        return preferences.getClientId() == null;
    }
}
