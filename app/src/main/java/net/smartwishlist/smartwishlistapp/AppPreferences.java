package net.smartwishlist.smartwishlistapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private static final String PREFS_NAME = AppConstants.APP_NAMESPACE + ".prefs";
    private static final String CLIENT_ID_PROP = "ClientId";
    private static final String TOKEN_PROP = "Token";
    private static final String DEFAULT_REGION_PROP = "DefaultRegion";
    private static final String HAS_ACCOUNT_PROP = "HasAccount";
    private static final String LAST_SERVER_POLL_PROP = "LastServerPoll";
    private static final String PENDING_MESSAGES_PROP = "PendingMessages";
    private static final String NOTIFICATION_ENABLED_PROP = "NotificationEnabled";
    private static final String GCM_REGISTRATION_ID_PROP = "GcmRegistraionId";
    private static final String APPLICATION_VERSION_PROP = "ApplicationVersion";

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

    public String getGcmRegistrationId() {
        return getStringPreference(GCM_REGISTRATION_ID_PROP);
    }

    public void setGcmRegistrationId(String gcmRegistrationId) {
        setStringPreference(GCM_REGISTRATION_ID_PROP, gcmRegistrationId);
    }

    public int getApplicationVersion() {
        return getIntPreference(APPLICATION_VERSION_PROP);
    }

    public void setApplicationVersion(int applicationVersion) {
        setIntPreference(APPLICATION_VERSION_PROP, applicationVersion);
    }

    @SuppressLint("CommitPrefEdits")
    public void beginEdit() {
        currentEditor = sharedPreferences.edit();
    }

    public void apply() {
        currentEditor.apply();
        currentEditor = null;
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
