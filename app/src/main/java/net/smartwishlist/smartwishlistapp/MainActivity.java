package net.smartwishlist.smartwishlistapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private AppInitialization appInitialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appInitialization = new AppInitialization(this);
        appInitialization.initializeApp();
        if (appInitialization.needSetup()) {
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            appInitialization.getGcmInitialization().initializeGcmToken();
        }

        setContentView(R.layout.activity_main);
        initializeInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkConnectivity();
    }

    private void initializeInterface() {
        boolean enabled = appInitialization.getPreferences().getNotificationEnabled();
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.notificationToggleButton);
        toggleButton.setChecked(enabled);
    }

    private void checkConnectivity() {
        if (!ApiService.isConnected(this)) {
            Toast toast = Toast.makeText(this,
                    R.string.no_internet_connection,
                    Toast.LENGTH_LONG);
            toast.show();
        }
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

    public void onClickNotificationSwitch(View view) {
        boolean isOn = ((ToggleButton) view).isChecked();
        appInitialization.getPreferences().setNotificationEnabled(isOn);
    }

    public void onClickShowWishLists(View view) {
        Intent intent = new Intent(this, WebSiteActivity.class);
        intent.putExtra(WebSiteActivity.TARGET_PAGE_EXTRA, AppConstants.MY_WISH_LISTS_PAGE);
        startActivity(intent);
    }

    public void onClickReset(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // TODO
        builder.setMessage(getString(R.string.confirmation))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (appInitialization.getGcmInitialization().deleteGcmToken()) {
                            appInitialization.getPreferences().resetAll();
                            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this,
                                    R.string.error_during_reset,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}
