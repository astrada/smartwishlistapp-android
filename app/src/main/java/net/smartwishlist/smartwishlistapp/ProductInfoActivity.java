package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProductInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        final long PLACEHOLDER = -1;
        long id = getIntent().getLongExtra(AppStorage.NotificationContract._ID, PLACEHOLDER);
        if (id != PLACEHOLDER) {
            ShowProductInfoTask task = new ShowProductInfoTask();
            task.execute(id);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_info, menu);
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

    public void onClickBuyButton(View view) {
        String url = (String) view.getTag();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            // TODO
            Log.d(AppConstants.LOG_TAG, "No product URL");
        }
    }

    private class ShowProductInfoTask extends AppStorage.LoadTriggerDataTask {

        public ShowProductInfoTask() {
            super(ProductInfoActivity.this);
        }

        @Override
        protected void onPostExecute(SmartWishListNotificationTriggerData data) {
            TextView productTitle = (TextView) findViewById(R.id.productTitle);
            TextView productPrice = (TextView) findViewById(R.id.productPrice);
            TextView productTargetPrice = (TextView) findViewById(R.id.productTargetPrice);
            CheckBox productAvailability = (CheckBox) findViewById(R.id.productAvailability);
            CheckBox productSoldByAmazon = (CheckBox) findViewById(R.id.productSoldByAmazon);
            TextView productAdded = (TextView) findViewById(R.id.productAdded);
            Button buyButton = (Button) findViewById(R.id.buyButton);

            productTitle.setText(data.getItem().getTitle());
            productPrice.setText(data.getItem().getFormattedPrice());
            productTargetPrice.setText(String.format("%s %.2f",
                    data.getItem().getCurrency(),
                    data.getPriceThreshold()));
            productAvailability.setChecked(data.getItem().getAvailable());
            productSoldByAmazon.setChecked(data.getItem().getSoldByAmazon());
            DateFormat dateFormat = SimpleDateFormat.getDateInstance();
            productAdded.setText(dateFormat.format(
                    new Date(Math.round(data.getCreationDate() * 1000.0))));
            buyButton.setTag(data.getItem().getProductUrl());

            DownloadLeftDrawableTask task = new DownloadLeftDrawableTask(productTitle);
            task.execute(data.getItem().getImageUrl());
        }
    }

    private class DownloadLeftDrawableTask extends DownloadImageTask {

        private TextView textView;

        public DownloadLeftDrawableTask(TextView textView) {
            super(null);
            this.textView = textView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
            textView.setCompoundDrawablesWithIntrinsicBounds(bitmapDrawable, null, null, null);
        }
    }
}
