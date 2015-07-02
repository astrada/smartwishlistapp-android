package net.smartwishlist.smartwishlistapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.DEBUG) {
            Button refresh = (Button) findViewById(R.id.button_refresh);
            refresh.setVisibility(View.INVISIBLE);
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        AppInitialization appInitialization = new AppInitialization(getApplicationContext());
        appInitialization.initializeApp();
        if (appInitialization.needSetup()) {
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            GcmInitialization gcmInitialization = new GcmInitialization();
            gcmInitialization.initializeGcmToken(this);
        }

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkConnectivity();
    }

    private void checkConnectivity() {
        if (!ApiService.isConnected(getApplicationContext())) {
            Toast toast = Toast.makeText(this,
                    R.string.no_internet_connection,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openMyWishLists(View view) {
        Intent intent = new Intent(this, WebSiteActivity.class);
        intent.putExtra(WebSiteActivity.TARGET_PAGE_EXTRA, AppConstants.MY_WISH_LISTS_PAGE);
        startActivity(intent);
    }

    public void reset(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GcmInitialization gcmInitialization = new GcmInitialization();
                        gcmInitialization.deleteGcmToken(MainActivity.this);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void refresh(View view) {
        ApiService.FetchAppNotificationsTask task = new ApiService.FetchAppNotificationsTask(this);
        task.execute();
    }
}
