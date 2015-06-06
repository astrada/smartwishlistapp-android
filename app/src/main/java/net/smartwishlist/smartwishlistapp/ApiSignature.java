package net.smartwishlist.smartwishlistapp;

import java.math.BigInteger;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApiSignature {

    private static final double ONE_SECOND_IN_MILLISECONDS = 1000.0;

    public static String generateRequestSignature(String token, String payload, double timestamp) {
        try {
            String value = String.format(Locale.US, "%s%.3f", payload, timestamp);
            byte[] keyBytes = token.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(value.getBytes());
            return toHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double getTimestamp() {
        return System.currentTimeMillis() / ONE_SECOND_IN_MILLISECONDS;
    }

    private static String toHex(byte[] bytes) {
        return String.format("%064x", new BigInteger(1, bytes));
    }
}
