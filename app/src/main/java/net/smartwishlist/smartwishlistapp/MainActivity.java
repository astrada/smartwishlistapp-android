package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;

    private AppInitialization appInitialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appInitialization = new AppInitialization(this);
        appInitialization.initializeApp();

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appInitialization.getGcmInitialization().checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onClickQrCode(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
            if (resultCode == RESULT_OK) {
                appInitialization.storeStateFromQrCode(intent);
            } else {
                // TODO
                Log.d(AppConstants.LOG_TAG, "QR code not detected");
            }
        }
    }

    public void onClickStartService(View view) {
        Intent serviceIntent = new Intent(this, DataPullService.class);
        startService(serviceIntent);
    }

    public void onClickStartSite(View view) {
        Intent intent = new Intent(this, WebSiteActivity.class);
        startActivity(intent);
    }
}
