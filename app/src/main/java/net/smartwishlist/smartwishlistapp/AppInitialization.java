package net.smartwishlist.smartwishlistapp;

import android.app.Activity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class AppInitialization {

    private final Activity activity;
    private AppPreferences preferences;
    private GcmInitialization gcmInitialization;

    public AppInitialization(Activity activity) {
        this.activity = activity;
        preferences = new AppPreferences(activity);
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    public GcmInitialization getGcmInitialization() {
        return gcmInitialization;
    }

    public boolean needSetup() {
        return preferences.getClientId() == null;
    }

    public void initializeApp() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(activity, new Crashlytics());
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
        gcmInitialization = new GcmInitialization(activity, preferences);
        gcmInitialization.initializeGcm();
    }

    public void toggleNotifications(boolean on) {
        if (on != preferences.getNotificationEnabled()) {
            ApiService.SaveUserTask task = new ApiService.SaveUserTask(activity);
            task.execute(on);
        }
    }
}
