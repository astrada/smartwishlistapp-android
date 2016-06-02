package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListRegisterGcmDeviceParameters;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class GoogleServicesHelper {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public GoogleServicesHelper() {
    }

    public static boolean checkPlayServices(Activity activity) {
        if (BuildConfig.DEBUG) {
            return true;
        } else {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(resultCode)) {
                    googleApiAvailability.getErrorDialog(activity, resultCode,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    AppLogging.logError("This device is not supported.");
                    activity.finish();
                }
                return false;
            }
            return true;
        }
    }

    public static class GetTokenAndSendToServerTask
            extends SyncTaskWithExponentialBackoff {

        private final Context context;
        private final AppPreferences preferences;

        public GetTokenAndSendToServerTask(Context context) {
            this.context = context;
            preferences = new AppPreferences(context);
        }

        @Override
        protected void tryDoing() throws IOException {
            getGcmTokenAndSendToServer();
        }

        @Override
        protected void handleFailure(Exception e) {
            AppLogging.logException(e);
        }

        private void getGcmTokenAndSendToServer() throws IOException {
            String registrationId = FirebaseInstanceId.getInstance().getToken();
            if (registrationId != null) {
                String clientId = preferences.getClientId();
                if (clientId == null) {
                    return;
                }

                String token = preferences.getToken();
                double timestamp = ApiSignature.getTimestamp();
                String signature = ApiSignature.generateRequestSignature(
                        token, registrationId, timestamp);
                SmartWishListRegisterGcmDeviceParameters parameters =
                        new SmartWishListRegisterGcmDeviceParameters();
                parameters.setRegistrationId(registrationId);
                Smartwishlist.AppNotifications.Register request =
                        ApiService.getApiServiceHandle(context).appNotifications().register(
                                clientId, timestamp, signature, parameters);
                request.setIsApp(Boolean.TRUE);
                request.execute();
            }
        }
    }

    public static class GetTokenAndSendToServerAsyncTask
            extends AsyncTask<Void, Void, Void> {

        private final Context context;

        public GetTokenAndSendToServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GetTokenAndSendToServerTask task = new GetTokenAndSendToServerTask(context);
            task.doSynchronized();
            return null;
        }
    }
}
