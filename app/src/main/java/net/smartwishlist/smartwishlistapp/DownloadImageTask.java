package net.smartwishlist.smartwishlistapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlString = strings[0];
        try {
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            // TODO
            Log.d(AppConstants.LOG_TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
