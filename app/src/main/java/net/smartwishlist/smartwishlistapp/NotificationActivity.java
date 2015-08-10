package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;

public class NotificationActivity extends AppCompatActivity
        implements NotificationItemFragment.OnItemSelectedListener {

    public static final String REFRESH_EXTRA = "refresh";

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean refresh = intent.getBooleanExtra(REFRESH_EXTRA, false);
        if (refresh) {
            NotificationItemFragment fragment =
                    (NotificationItemFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
            if (fragment != null) {
                fragment.restartLoader();
            }
        }
    }

    public void openProductInfo(View view) {
        long id = (long) view.getTag();
        FrameLayout detailsFrame = (FrameLayout) findViewById(R.id.details_frame);
        if (detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE) {
            ProductInfoFragment fragment =
                    (ProductInfoFragment) getSupportFragmentManager().findFragmentById(R.id.details_frame);
            if (fragment == null) {
                fragment = new ProductInfoFragment();
                Bundle args = new Bundle();
                args.putLong(AppStorage.NotificationContract._ID, id);
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.details_frame,
                        fragment).commit();
            } else {
                fragment.updateContent(id);
            }
        } else {
            Intent intent = new Intent(this, ProductInfoActivity.class);
            intent.putExtra(AppStorage.NotificationContract._ID, id);
            startActivity(intent);
        }
    }

    private class OpenProductUrlTask extends AppStorage.LoadTriggerDataTask {

        public OpenProductUrlTask() {
            super(getApplicationContext());
        }

        @Override
        protected void onPostExecute(SmartWishListNotificationTriggerData data) {
            String url = data.getItem().getProductUrl();
            if (url != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                AppLogging.logError("OpenProductUrlTask: No product URL. data=" + data.toString());
            }
        }
    }
}
