package net.smartwishlist.smartwishlistapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ProductInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        if (savedInstanceState == null) {
            ProductInfoFragment fragment = new ProductInfoFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.details_layout,
                    fragment).commit();
        }
    }
}
