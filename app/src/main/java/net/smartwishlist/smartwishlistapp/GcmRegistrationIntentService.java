package net.smartwishlist.smartwishlistapp;

import android.app.IntentService;
import android.content.Intent;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListRegisterGcmDeviceParameters;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final Object SYNC_OBJECT = new Object();

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (SYNC_OBJECT) {
                long backoff = ApiService.BACKOFF_MILLI_SECONDS + ApiService.random.nextInt(1000);
                for (int i = 1; i <= ApiService.MAX_ATTEMPTS; i++) {
                    try {
                        getTokenAndSendToServer();
                        break;
                    } catch (IOException e) {
                        if (i == ApiService.MAX_ATTEMPTS) {
                            throw e;
                        }
                        try {
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            Thread.currentThread().interrupt();
                        }
                        backoff *= 2;
                    }
                }
            }
        } catch (Exception e) {
            AppLogging.logException(e);
            AppPreferences preferences = new AppPreferences(this);
            preferences.setGcmTokenSent(false);
        }
    }

    private void getTokenAndSendToServer() throws IOException {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(BuildConfig.GCM_SENDER_ID,
                GoogleCloudMessaging.INSTANCE_ID_SCOPE);
        registerGcmDevice(token);
    }

    private void registerGcmDevice(String registrationId) throws IOException {
        AppPreferences preferences = new AppPreferences(this);
        if (registrationId != null) {
            String clientId = preferences.getClientId();
            String token = preferences.getToken();
            double timestamp = ApiSignature.getTimestamp();
            String signature = ApiSignature.generateRequestSignature(
                    token, registrationId, timestamp);
            SmartWishListRegisterGcmDeviceParameters parameters =
                    new SmartWishListRegisterGcmDeviceParameters();
            parameters.setRegistrationId(registrationId);
            Smartwishlist.AppNotifications.Register request =
                    ApiService.getApiServiceHandle(this).appNotifications().register(clientId,
                            timestamp, signature, parameters);
            request.setIsApp(Boolean.TRUE);
            request.execute();
            preferences.setGcmTokenSent(true);
        } else {
            preferences.setGcmTokenSent(false);
        }
    }
}
