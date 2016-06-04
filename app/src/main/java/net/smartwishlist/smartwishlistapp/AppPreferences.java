package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {

    private static final String CLIENT_ID_PROP = "clientId";
    private static final String TOKEN_PROP = "token";
    private static final String DEFAULT_REGION_PROP = "defaultRegion";
    private static final String HAS_ACCOUNT_PROP = "hasAccount";
    private static final String LAST_VIEWED_NOTIFICATIONS_PROP = "lastViewedNotifications";
    private static final String PENDING_MESSAGES_PROP = "pendingMessages";
    private static final String NOTIFICATION_ENABLED_PROP = "notificationEnabled";
    private static final String GCM_TOKEN_SENT_PROP = "gcmTokenSent";

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor currentEditor;

    public AppPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getClientId() {
        return getStringPreference(CLIENT_ID_PROP);
    }

    public void setClientId(String clientId) {
        setStringPreference(CLIENT_ID_PROP, clientId);
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

    public boolean isGcmTokenSent() {
        return getBooleanPreference(GCM_TOKEN_SENT_PROP);
    }

    public void setGcmTokenSent(boolean flag) {
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
        setGcmTokenSent(false);
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
