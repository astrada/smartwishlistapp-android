package net.smartwishlist.smartwishlistapp;

import java.io.IOException;
import java.util.Random;

public abstract class SyncTaskWithExponentialBackoff {

    public static final int MAX_ATTEMPTS = 5;
    public static final int BACKOFF_MILLI_SECONDS = 2000;
    public static final Random random = new Random();

    private static final Object SYNC_OBJECT = new Object();

    public void doSynchronized() {
        synchronized (SYNC_OBJECT) {
            try {
                doWithExponentialBackoff();
            } catch (Exception e) {
                handleFailure(e);
            }
        }
    }

    private void doWithExponentialBackoff() throws IOException {
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            try {
                tryDoing();
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    throw e;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
                backoff *= 2;
            }
        }

    }

    protected abstract void tryDoing() throws IOException;

    protected abstract void handleFailure(Exception e);
}
