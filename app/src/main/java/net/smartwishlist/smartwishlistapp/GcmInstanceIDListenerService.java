package net.smartwishlist.smartwishlistapp;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}
