package net.smartwishlist.smartwishlistapp;

import android.content.Intent;
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
                QrCodeInitialization qrCodeInitialization = new QrCodeInitialization();
                if (qrCodeInitialization.storeStateFromQrCode(intent)) {
                    Toast toast = Toast.makeText(this,
                            "QR code found. Now this app will receive notifications for your wish list.",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                    return;
                }
            }
            Toast toast = Toast.makeText(this,
                    "QR code not found. Please retry.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickStartSite(View view) {
        Intent intent = new Intent(this, WebSiteActivity.class);
        startActivity(intent);
    }

    public void onClickHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
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
                    AppPreferences preferences = new AppPreferences(SetupActivity.this);
                    preferences.beginEdit();
                    preferences.setClientId(clientId);
                    preferences.setToken(token);
                    preferences.setDefaultRegion(defaultRegion);
                    preferences.setNotificationEnabled(true);
                    if (hasAccount.equals("1")) {
                        preferences.setHasAccount("true");
                    } else {
                        preferences.setHasAccount("false");
                    }
                    preferences.apply();
                    ApiService.CheckClientIdTask task =
                            new ApiService.CheckClientIdTask(SetupActivity.this);
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
