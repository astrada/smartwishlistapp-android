package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class AppContextSingleton {
    private static final int CACHE_MAX_SIZE = 20;

    private static AppContextSingleton instance;
    private static Context applicationContext;

    private RequestQueue requestQueue;
    private final ImageLoader imageLoader;

    private AppContextSingleton(Context context) {
        applicationContext = context.getApplicationContext();
        requestQueue = getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new LruImageCache());
    }

    public static synchronized AppContextSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new AppContextSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private static class LruImageCache implements ImageLoader.ImageCache {
        private final LruCache<String, Bitmap>
                cache = new LruCache<>(CACHE_MAX_SIZE);

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    }
}
