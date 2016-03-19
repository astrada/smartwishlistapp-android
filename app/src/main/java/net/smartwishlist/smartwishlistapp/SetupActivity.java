package net.smartwishlist.smartwishlistapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class SetupActivity extends AppCompatActivity {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;
    private static final String BS_PACKAGE = "com.google.zxing.client.android";

    private long lastClickTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        if (!hasCamera()) {
            View qrCodeLayout = findViewById(R.id.qr_code_layout);
            assert qrCodeLayout != null;
            qrCodeLayout.setVisibility(View.GONE);
        }
    }

    public void scanQrCode(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        Intent intent = new Intent(BS_PACKAGE + ".SCAN");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (!isQrCodeReaderInstalled(intent)) {
            showDownloadDialog();
        } else {
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            try {
                startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
            } catch (Exception e) {
                AppLogging.logException(e);
                Toast toast = Toast.makeText(SetupActivity.this,
                        R.string.qr_code_install_error, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private Boolean isQrCodeReaderInstalled(Intent intent) {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> availableApps = pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return availableApps != null && availableApps.size() > 0;
    }

    private void showDownloadDialog() {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
        downloadDialog.setTitle(getString(R.string.qr_code_title));
        downloadDialog.setMessage(getString(R.string.qr_code_message));
        downloadDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
                    return;
                }
                lastClickTimestamp = SystemClock.elapsedRealtime();
                Uri uri = Uri.parse("market://details?id=" + BS_PACKAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    AppLogging.logException(e);
                    Toast toast = Toast.makeText(SetupActivity.this,
                            R.string.qr_code_app_error, Toast.LENGTH_LONG);
                    toast.show();
                    Button scan = (Button) findViewById(R.id.button_scan_qr_code);
                    assert scan != null;
                    scan.setEnabled(false);
                }
            }
        });
        downloadDialog.setNegativeButton(android.R.string.no, null);
        downloadDialog.setCancelable(true);
        downloadDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
            if (resultCode == RESULT_OK) {
                QrCodeInitialization qrCodeInitialization = new QrCodeInitialization();
                if (qrCodeInitialization.storeStateFromQrCode(intent)) {
                    Toast toast = Toast.makeText(this,
                            R.string.qr_code_found,
                            Toast.LENGTH_LONG);
                    toast.show();
                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                    return;
                }
            }
            Toast toast = Toast.makeText(this,
                    R.string.qr_code_not_found,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void openWebSite(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, WebSiteActivity.class);
        intent.putExtra(WebSiteActivity.TARGET_PAGE_EXTRA, AppConstants.SEARCH_PAGE);
        WebSiteActivity.addLanguageToWebSiteIntent(intent);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this)
                .addParentStack(WebSiteActivity.class)
                .addNextIntent(intent);
        taskStackBuilder.startActivities();
        finish();
    }

    public void openHelp(View view) {
        if (SystemClock.elapsedRealtime() - lastClickTimestamp < AppConstants.CLICK_DELAY) {
            return;
        }
        lastClickTimestamp = SystemClock.elapsedRealtime();
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private boolean hasCamera() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    private class QrCodeInitialization {
        private static final int CLIENT_ID_LENGTH = 36;
        private static final int TOKEN_LENGTH = 64;
        private static final int DEFAULT_REGION_LENGTH = 2;
        private static final int HAS_ACCOUNT_LENGTH = 1;
        private static final int QR_CODE_LENGTH = CLIENT_ID_LENGTH + TOKEN_LENGTH +
                DEFAULT_REGION_LENGTH + HAS_ACCOUNT_LENGTH;
        private static final int TOKEN_OFFSET = CLIENT_ID_LENGTH;
        private static final int DEFAULT_REGION_OFFSET = TOKEN_OFFSET + TOKEN_LENGTH;
        private static final int HAS_ACCOUNT_OFFSET = DEFAULT_REGION_OFFSET + DEFAULT_REGION_LENGTH;

        public QrCodeInitialization() {
        }

        public boolean storeStateFromQrCode(Intent intent) {
            try {
                String scanResult = intent.getExtras().getString("SCAN_RESULT");
                if (scanResult != null && scanResult.length() == QR_CODE_LENGTH) {
                    String clientId = scanResult.substring(0, CLIENT_ID_LENGTH).toLowerCase();
                    String token = scanResult.substring(TOKEN_OFFSET,
                            DEFAULT_REGION_OFFSET).toLowerCase();
                    String defaultRegion = scanResult.substring(DEFAULT_REGION_OFFSET,
                            HAS_ACCOUNT_OFFSET).toUpperCase();
                    String hasAccount = scanResult.substring(HAS_ACCOUNT_OFFSET);
                    Context context = getApplicationContext();
                    AppPreferences preferences = new AppPreferences(context);
                    preferences.beginEdit();
                    preferences.setClientId(clientId);
                    preferences.setToken(token);
                    preferences.setDefaultRegion(defaultRegion);
                    preferences.setNotificationEnabled(true);
                    if ("1".equals(hasAccount)) {
                        preferences.setHasAccount(Boolean.toString(true));
                    } else {
                        preferences.setHasAccount(Boolean.toString(false));
                    }
                    preferences.apply();
                    AppLogging.logDebug("storeStateFromQrCode: clientId=[" + clientId + "]");
                    ApiService.CheckClientIdTask task =
                            new ApiService.CheckClientIdTask(context);
                    task.execute(clientId);
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                AppLogging.logException(e);
                return false;
            }
        }
    }
}
