package com.packt.androidconcurrency.chapter6.example3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.packt.androidconcurrency.chapter6.CacheDirCache;
import com.packt.androidconcurrency.chapter6.DownloadService;

import java.io.IOException;
import java.io.InputStream;

public class BitmapDownloadService extends DownloadService<Bitmap> {
    @Override
    protected Cache initCache() {
        return new CacheDirCache(getApplicationContext());
    }

    @Override
    protected Bitmap convert(Uri data) {
        try {
            return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(data));
        } catch (IOException exc) {
            return null;
        }
    }
}
