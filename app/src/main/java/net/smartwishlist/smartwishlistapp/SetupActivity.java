package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        if (!hasCamera()) {
            View qrCodeLayout = findViewById(R.id.qr_code_layout);
            qrCodeLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void scanQrCode(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
            if (resultCode == RESULT_OK) {
                QrCodeInitialization qrCodeInitialization = new QrCodeInitialization();
                if (qrCodeInitialization.storeStateFromQrCode(intent)) {
                    Toast toast = Toast.makeText(this,
                            R.string.qr_code_found,
                            Toast.LENGTH_SHORT);
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
        Intent intent = new Intent(this, WebSiteActivity.class);
        startActivity(intent);
        finish();
    }

    public void openHelp(View view) {
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
                    if (hasAccount.equals("1")) {
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
