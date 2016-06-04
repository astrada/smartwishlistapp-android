package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterface {

    private static final String CLIENT_ID_KEY = "clientId";
    private static final String TOKEN_KEY = "token";
    private static final String DEFAULT_REGION_KEY = "defaultRegion";
    private static final String HAS_ACCOUNT_KEY = "hasAccount";

    private final Context context;

    public WebAppInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public String getItemFromStorage(String key) {
        AppPreferences appPreferences = new AppPreferences(context);
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
                AppLogging.logError("getItemFromStorage: Unexpected key: " + key);
                break;
        }
        return null;
    }

    @JavascriptInterface
    public void setItemInStorage(String key, String value) {
        AppPreferences appPreferences = new AppPreferences(context);
        AppLogging.logDebug("setItemInStorage: [" + key + "]=[" + value + "]");
        switch (key) {
            case CLIENT_ID_KEY:
                AppInitialization appInitialization = new AppInitialization(context,
                        appPreferences);
                appInitialization.modifyClientId(value);
                break;
            case TOKEN_KEY:
                appPreferences.setToken(value);
                break;
            case DEFAULT_REGION_KEY:
                appPreferences.setDefaultRegion(value);
                break;
            case HAS_ACCOUNT_KEY:
                appPreferences.setHasAccount(value);
                break;
            default:
                AppLogging.logError("setItemInStorage: Unexpected key: " + key + "=" + value);
                break;
        }
    }

    @JavascriptInterface
    public void clearItemsInStorage() {
        AppPreferences appPreferences = new AppPreferences(context);
        appPreferences.resetAll();
    }
}
