package net.smartwishlist.smartwishlistapp;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;

public class AppConstants {

    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    public static final int LOGO_COLOR = 0xe9620f;

    public static final int NO_ERRORS = 0;

    public static final String APP_NAMESPACE = "net.smartwishlist.smartwishlistapp";

    public static final String APP_NAME = "SmartWishListApp";

    public static final String VERSION = "0.1";

    public static final String LOG_TAG = APP_NAME;

    public static final String WEB_SITE_URL = "https://www.smartwishlist.net/";

    public static final String JAVASCRIPT_INTERFACE = "AndroidAppJsInterface";

    /**
     * Retrieves a Smartwishlist api service handle to access the API.
     */
    public static Smartwishlist getApiServiceHandle() {
        Smartwishlist.Builder smartwishlist = new Smartwishlist.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, null);

        if (BuildConfig.DEBUG) {
            smartwishlist.setRootUrl(AppSettings.LOCAL_API_URL);
        }
        smartwishlist.setApplicationName(APP_NAME + " v" + VERSION);
        return smartwishlist.build();
    }
}
