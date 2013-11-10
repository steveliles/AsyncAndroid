package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Very simple cache - most definitely not advised for
 * production use!
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
            if (expired(f)) {
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

    private boolean expired(File f) {
        return System.currentTimeMillis() - f.lastModified() > FIVE_MINUTES;
    }

    private File getCacheFile(String url) {
        String name = url.substring(url.lastIndexOf("/")+1, url.length());
        return new File(dir, url.hashCode() + "_" + name);
    }
}
