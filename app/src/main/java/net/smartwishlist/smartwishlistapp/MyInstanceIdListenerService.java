package net.smartwishlist.smartwishlistapp;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIdListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        ApiService.GetTokenAndSendToServerSyncTask task =
                new ApiService.GetTokenAndSendToServerSyncTask(this);
        task.doSynchronized();
    }
}
