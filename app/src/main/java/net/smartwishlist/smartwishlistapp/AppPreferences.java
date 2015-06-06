package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;

public class AppPreferences {

    private static final String PREFS_NAME = AppConstants.APP_NAMESPACE + ".prefs";
    private static final String CLIENT_ID_PROP = "ClientId";
    private static final String TOKEN_PROP = "Token";
    private static final String DEFAULT_REGION_PROP = "DefaultRegion";
    private static final String HAS_ACCOUNT_PROP = "HasAccount";
    private static final String LAST_SERVER_POLL_PROP = "LastServerPoll";
    private static final String LAST_VIEWED_NOTIFICATIONS_PROP = "LastViewedNotifications";
    private static final String PENDING_MESSAGES_PROP = "PendingMessages";
    private static final String NOTIFICATION_ENABLED_PROP = "NotificationEnabled";
    private static final String GCM_TOKEN_SENT_PROP = "GcmTokenSent";

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor currentEditor;

    public AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getClientId() {
        return getStringPreference(CLIENT_ID_PROP);
    }

    public void setClientId(String clientId) {
        setStringPreference(CLIENT_ID_PROP, clientId);
        if (!BuildConfig.DEBUG) {
            Crashlytics.setString(AppConstants.CLIENT_ID_TAG, BuildConfig.DEBUG_CLIENT_ID);
        }
    }

    public String getToken() {
        return getStringPreference(TOKEN_PROP);
    }

    public void setToken(String token) {
        setStringPreference(TOKEN_PROP, token);
    }

    public String getDefaultRegion() {
        return getStringPreference(DEFAULT_REGION_PROP);
    }

    public void setDefaultRegion(String region) {
        setStringPreference(DEFAULT_REGION_PROP, region);
    }

    public String getHasAccount() {
        return getStringPreference(HAS_ACCOUNT_PROP);
    }

    public void setHasAccount(String hasAccount) {
        setStringPreference(HAS_ACCOUNT_PROP, hasAccount);
    }

    public double getLastServerPoll() {
        return getDoublePreference(LAST_SERVER_POLL_PROP);
    }

    public void setLastServerPoll(double timestamp) {
        setDoublePreference(LAST_SERVER_POLL_PROP, timestamp);
    }

    public double getLastViewedNotifications() {
        return getDoublePreference(LAST_VIEWED_NOTIFICATIONS_PROP);
    }

    public void setLastViewedNotifications(double timestamp) {
        setDoublePreference(LAST_VIEWED_NOTIFICATIONS_PROP, timestamp);
    }

    public int getPendingMessages() {
        return getIntPreference(PENDING_MESSAGES_PROP);
    }

    public void setPendingMessages(int n) {
        setIntPreference(PENDING_MESSAGES_PROP, n);
    }

    public boolean getNotificationEnabled() {
        return getBooleanPreference(NOTIFICATION_ENABLED_PROP);
    }

    public void setNotificationEnabled(boolean flag) {
        setBooleanPreference(NOTIFICATION_ENABLED_PROP, flag);
    }

    public Boolean isGcmTokenSent() {
        return getBooleanPreference(GCM_TOKEN_SENT_PROP);
    }

    public void setGcmTokenSent(Boolean flag) {
        setBooleanPreference(GCM_TOKEN_SENT_PROP, flag);
    }

    @SuppressLint("CommitPrefEdits")
    public void beginEdit() {
        currentEditor = sharedPreferences.edit();
    }

    public void apply() {
        currentEditor.apply();
        currentEditor = null;
    }

    public void resetAll() {
        beginEdit();
        setClientId(null);
        setToken(null);
        setDefaultRegion(null);
        setHasAccount(null);
        setNotificationEnabled(false);
        apply();
    }

    private String getStringPreference(String key) {
        return sharedPreferences.getString(key, null);
    }

    private void setStringPreference(String key, String value) {
        if (currentEditor == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        } else {
            currentEditor.putString(key, value);
        }
    }

    private double getDoublePreference(String key) {
        return Double.longBitsToDouble(sharedPreferences.getLong(key, Double.doubleToLongBits(0.0)));
    }

    private void setDoublePreference(String key, double value) {
        if (currentEditor == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, Double.doubleToRawLongBits(value));
            editor.apply();
        } else {
            currentEditor.putLong(key, Double.doubleToRawLongBits(value));
        }
    }

    private int getIntPreference(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    private void setIntPreference(String key, int value) {
        if (currentEditor == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        } else {
            currentEditor.putInt(key, value);
        }
    }

    private boolean getBooleanPreference(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    private void setBooleanPreference(String key, boolean value) {
        if (currentEditor == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } else {
            currentEditor.putBoolean(key, value);
        }
    }
}
