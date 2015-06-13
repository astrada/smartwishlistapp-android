package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;


public class NotificationActivity extends AppCompatActivity
        implements NotificationItemFragment.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    @Override
    public void onItemSelected(long id) {
        OpenProductUrlTask task = new OpenProductUrlTask();
        task.execute(id);
    }

    public void onClickInfoButton(View view) {
        long id = (long) view.getTag();
        Intent intent = new Intent(this, ProductInfoActivity.class);
        intent.putExtra(AppStorage.NotificationContract._ID, id);
        startActivity(intent);
    }

    private class OpenProductUrlTask extends AppStorage.LoadTriggerDataTask {

        public OpenProductUrlTask() {
            super(NotificationActivity.this);
        }

        @Override
        protected void onPostExecute(SmartWishListNotificationTriggerData data) {
            String url = data.getItem().getProductUrl();
            if (url != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                // TODO
                Log.d(AppConstants.LOG_TAG, "No product URL");
            }
        }
    }
}
