package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nonnull;

public class ProductInfoFragment extends Fragment {

    private static final long PLACEHOLDER_ID = -1;

    public ProductInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_info, container, false);

        long id = getArguments().getLong(AppStorage.NotificationContract._ID, PLACEHOLDER_ID);
        updateContent(id);

        Button buyButton = (Button) rootView.findViewById(R.id.button_buy);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuyLink(view);
            }
        });

        return rootView;
    }

    public void updateContent(long notificationId) {
        if (notificationId != PLACEHOLDER_ID) {
            ShowProductInfoTask task = new ShowProductInfoTask();
            task.execute(notificationId);
        }
    }

    private void openBuyLink(View view) {
        String url = (String) view.getTag();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            AppLogging.logError("ProductInfoFragment.openBuyLink: No product URL");
        }
    }

    private class ShowProductInfoTask extends AppStorage.LoadTriggerDataTask {

        public ShowProductInfoTask() {
            super(getActivity().getApplicationContext());
        }

        @Override
        protected void onPostExecute(SmartWishListNotificationTriggerData data) {
            View rootView = getView();
            if (rootView != null) {
                final TextView productTitle = (TextView) rootView.findViewById(R.id.product_title);
                TextView productPrice = (TextView) rootView.findViewById(R.id.product_price);
                TextView productTargetPrice = (TextView) rootView.findViewById(R.id.product_target_price);
                CheckBox productAvailability = (CheckBox) rootView.findViewById(R.id.product_availability);
                CheckBox productSoldByAmazon = (CheckBox) rootView.findViewById(R.id.product_sold_by_amazon);
                TextView productAdded = (TextView) rootView.findViewById(R.id.product_added_date);
                TextView priceDrop = (TextView) rootView.findViewById(R.id.price_drop);
                TextView store = (TextView) rootView.findViewById(R.id.store);
                LinearLayout priceDropLayout = (LinearLayout) rootView.findViewById(R.id.price_drop_layout);
                Button buyButton = (Button) rootView.findViewById(R.id.button_buy);

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
                if (data.getPriceDrop() != null &&
                        data.getPriceDrop() != 0.0) {
                    priceDropLayout.setVisibility(View.VISIBLE);
                    TextView priceDropLabel = (TextView) rootView.findViewById(R.id.price_drop_label);
                    if (data.getPriceDrop() > 0.0) {
                        priceDropLabel.setText(R.string.price_drop_label);
                        priceDrop.setText(formatPriceDrop(
                                data.getPriceDrop(),
                                data.getPriceDropPercentage(),
                                data.getItem().getCurrency()));
                    } else {
                        priceDropLabel.setText(R.string.price_increase_label);
                        priceDrop.setText(formatPriceDrop(
                                -data.getPriceDrop(),
                                -data.getPriceDropPercentage(),
                                data.getItem().getCurrency()));
                    }
                } else {
                    priceDropLayout.setVisibility(View.GONE);
                }
                store.setText(data.getItem().getRegion());
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
                NetworkImageManager.getInstance(getActivity()).addToRequestQueue(request);
            }
        }
    }

    @Nonnull
    private static String formatPriceDrop(Double priceDrop, Double priceDropPercentage,
                                          String currency) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %,.2f",
                currency,
                priceDrop));
        if (priceDropPercentage != null) {
            builder.append(String.format(" (%,.2f%%)", priceDropPercentage));
        }
        return builder.toString();
    }
}
