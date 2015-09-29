package net.smartwishlist.smartwishlistapp;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final Object SYNC_OBJECT = new Object();

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppPreferences preferences = new AppPreferences(this);

        try {
            synchronized (SYNC_OBJECT) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(BuildConfig.GCM_SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                sendRegistrationToServer(token);
            }
        } catch (Exception e) {
            AppLogging.logException(e);
            preferences.setGcmTokenSent(false);
        }
    }

    private void sendRegistrationToServer(String token) {
        ApiService.RegisterGcmDeviceTask task =
                new ApiService.RegisterGcmDeviceTask(getApplicationContext());
        task.execute(token);
    }
}
