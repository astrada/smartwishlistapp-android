package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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

    public void onClickBuyButton(View view) {
        String url = (String) view.getTag();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            AppLogging.logError("ProductInfoActivity.onClickBuyButton: No product URL");
        }
    }

    private class ShowProductInfoTask extends AppStorage.LoadTriggerDataTask {

        public ShowProductInfoTask() {
            super(ProductInfoActivity.this);
        }

        @Override
        protected void onPostExecute(SmartWishListNotificationTriggerData data) {
            final TextView productTitle = (TextView) findViewById(R.id.productTitle);
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
                    new Date(Math.round(data.getCreationDate() *
                            AppConstants.ONE_SECOND_IN_MILLISECONDS))));
            buyButton.setTag(data.getItem().getProductUrl());

            ImageRequest request = new ImageRequest(data.getItem().getImageUrl(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                                    bitmap);
                            productTitle.setCompoundDrawablesWithIntrinsicBounds(
                                    bitmapDrawable, null, null, null);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            productTitle.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.not_available_image, 0, 0, 0);
                        }
                    });
            NetworkImageManager.getInstance(ProductInfoActivity.this).addToRequestQueue(request);
        }
    }
}
