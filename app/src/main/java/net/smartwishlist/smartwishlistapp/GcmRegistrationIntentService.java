package net.smartwishlist.smartwishlistapp;

import android.app.IntentService;
import android.content.Intent;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GcmInitialization.GetTokenAndSendToServerTask task =
                new GcmInitialization.GetTokenAndSendToServerTask(this);
        task.doSynchronized();
    }
}
