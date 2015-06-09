package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

public class AppConstants {

    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    public static final int LOGO_COLOR = 0xe9620f;

    public static final int NO_ERRORS = 0;

    public static final String APP_NAMESPACE = "net.smartwishlist.smartwishlistapp";

    public static final String APP_NAME = "SmartWishListApp";

    public static final String LOG_TAG = APP_NAME;

    public static final String WEB_SITE_URL = "https://www.smartwishlist.net";

    public static final String MY_WISH_LISTS_PAGE = "/mywishlists";

    public static final String JAVASCRIPT_INTERFACE = "AndroidAppJsInterface";

    public static final String CLIENT_ID_TAG = "ClientId";

    public static final double ONE_SECOND_IN_MILLISECONDS = 1000.0;

    public static class Version {

        private static String APP_VERSION_NAME = null;
        private static int APP_VERSION_CODE = 0;

        private Version() {}

        public static String getAppVersionName(Context context) {
            if (APP_VERSION_NAME == null) {
                PackageInfo packageInfo = getPackageInfo(context);
                APP_VERSION_NAME = packageInfo.versionName;
            }
            return APP_VERSION_NAME;
        }

        public static int getAppVersion(Context context) {
            if (APP_VERSION_CODE == 0) {
                PackageInfo packageInfo = getPackageInfo(context);
                APP_VERSION_CODE = packageInfo.versionCode;
            }
            return APP_VERSION_CODE;
        }

        private static PackageInfo getPackageInfo(Context context) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                // should never happen
                throw new RuntimeException("Could not get package name: " + e);
            }
        }
    }
}
