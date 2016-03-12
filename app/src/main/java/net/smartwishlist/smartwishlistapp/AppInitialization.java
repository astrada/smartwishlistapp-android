package net.smartwishlist.smartwishlistapp;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

public class AppInitialization {

    private final Context context;
    private final AppPreferences preferences;

    public AppInitialization(Context context) {
        this.context = context;
        preferences = new AppPreferences(context);
    }

    public void initializeApp() {
        Crashlytics kit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(context, kit);
        Crashlytics.setString(AppConstants.CLIENT_ID_TAG, preferences.getClientId());

        if (BuildConfig.DEBUG) {
            preferences.beginEdit();
            preferences.setClientId(BuildConfig.DEBUG_CLIENT_ID);
            preferences.setToken(BuildConfig.DEBUG_TOKEN);
            preferences.setDefaultRegion(BuildConfig.DEBUG_DEFAULT_REGION);
            preferences.setHasAccount(BuildConfig.DEBUG_HAS_ACCOUNT);
            preferences.setNotificationEnabled(BuildConfig.DEBUG_NOTIFICATION_ENABLED);
            preferences.apply();
        }
    }

    public boolean needSetup() {
        return preferences.getClientId() == null;
    }
}
