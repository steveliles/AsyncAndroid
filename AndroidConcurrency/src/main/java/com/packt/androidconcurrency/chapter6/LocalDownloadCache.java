package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Very simple cache that uses a constant expiry time of 5 minutes.
 * Note that nothing is expired from the cache unless it is requested
 * again, which is very poor behaviour from a cache - this is  NOT a
 * production quality download cache!
 */
public class LocalDownloadCache {

    public static final long FIVE_MINUTES = 5L * 60L * 1000L;

    private File dir;

    public LocalDownloadCache(Context ctx) {
        dir = ctx.getCacheDir();
    }

    public boolean exists(String url) {
        File f = getCacheFile(url);
        if (f.exists()) {
            if (System.currentTimeMillis() - f.lastModified() > FIVE_MINUTES) {
                f.delete();
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public Uri get(String url)
    throws IOException {
        File f = getCacheFile(url);
        return Uri.fromFile(f);
    }

    public OutputStream getOutputStream(String url)
    throws IOException {
        File f = getCacheFile(url);
        return new BufferedOutputStream(new FileOutputStream(f));
    }

    private File getCacheFile(String url) {
        String name = url.substring(url.lastIndexOf("/")+1, url.length());
        return new File(dir, url.hashCode() + "_" + name);
    }
}
