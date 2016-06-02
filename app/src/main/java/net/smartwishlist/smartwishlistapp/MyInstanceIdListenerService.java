package net.smartwishlist.smartwishlistapp;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIdListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        GoogleServicesHelper.GetTokenAndSendToServerTask task =
                new GoogleServicesHelper.GetTokenAndSendToServerTask(this);
        task.doSynchronized();
    }
}
