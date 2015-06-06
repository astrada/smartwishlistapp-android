package net.smartwishlist.smartwishlistapp;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class AppLogging {

    public static void logException(Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(AppConstants.LOG_TAG, e.getMessage(), e);
        } else {
            Crashlytics.logException(e);
        }
    }

    public static void logError(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(AppConstants.LOG_TAG, message);
        } else {
            Crashlytics.log(Log.ERROR, AppConstants.LOG_TAG, message);
        }
    }
}
