package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class WebAppInterface {

    private static final String CLIENT_ID_KEY = "clientId";
    private static final String TOKEN_KEY = "token";
    private static final String DEFAULT_REGION_KEY = "defaultRegion";
    private static final String HAS_ACCOUNT_KEY = "hasAccount";

    private final AppPreferences appPreferences;

    public WebAppInterface(Context context) {
        appPreferences = new AppPreferences(context);
    }

    @JavascriptInterface
    public String getItemFromStorage(String key) {
        switch (key) {
            case CLIENT_ID_KEY:
                return appPreferences.getClientId();
            case TOKEN_KEY:
                return appPreferences.getToken();
            case DEFAULT_REGION_KEY:
                return appPreferences.getDefaultRegion();
            case HAS_ACCOUNT_KEY:
                return appPreferences.getHasAccount();
            default:
                Log.d(AppConstants.LOG_TAG, "getItemFromStorage: " + key);
                break;
        }
        return null;
    }

    @JavascriptInterface
    public void setItemInStorage(String key, String value) {
        switch (key) {
            case CLIENT_ID_KEY:
                appPreferences.setClientId(value);
            case TOKEN_KEY:
                appPreferences.setToken(value);
            case DEFAULT_REGION_KEY:
                appPreferences.setDefaultRegion(value);
            case HAS_ACCOUNT_KEY:
                appPreferences.setHasAccount(value);
            default:
                Log.d(AppConstants.LOG_TAG, "setItemInStorage: " + key + "=" + value);
                break;
        }
    }

    @JavascriptInterface
    public void clearItemsInStorage() {
        Log.d(AppConstants.LOG_TAG, "clearItemsInStorage");
        appPreferences.setClientId(null);
        appPreferences.setToken(null);
        appPreferences.setDefaultRegion(null);
        appPreferences.setHasAccount(null);
    }
}
