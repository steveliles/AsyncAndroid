package com.packt.androidconcurrency.chapter6.example4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.chapter6.CacheDirCache;
import com.packt.androidconcurrency.chapter6.DownloadService;

import java.io.IOException;
import java.io.InputStream;

public class ImageDownloadService extends DownloadService<Bitmap> {

    private BitmapFactory.Options opts;

    public ImageDownloadService() {
        opts = new BitmapFactory.Options();
        opts.inSampleSize = 8;
    }

    @Override
    protected Cache initCache() {
        return new CacheDirCache(getApplicationContext());
    }

    @Override
    protected Bitmap convert(Uri data) {
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(data);
            return BitmapFactory.decodeStream(in, null, opts);
        } catch (IOException exc) {
            Log.e(LaunchActivity.TAG, "bad uri?", exc);
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exc) {
                    Log.w(LaunchActivity.TAG, "closing stream", exc);
                }
            }
        }
    }
}
