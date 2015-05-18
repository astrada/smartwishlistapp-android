package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;


public class NotificationActivity extends AppCompatActivity
        implements NotificationItemFragment.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
