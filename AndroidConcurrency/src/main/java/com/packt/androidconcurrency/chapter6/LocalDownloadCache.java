package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * Very simple cache that uses a constant expiry time
 * set as a constructor parameter.
 */
public class LocalDownloadCache {

    private File dir;
    private long expiry;

    public LocalDownloadCache(long expiry, Context ctx) {
        this.expiry = expiry;
        dir = ctx.getCacheDir();
    }

    public boolean exists(URL downloadURL) {
        File f = getCacheFile(downloadURL);
        if (f.exists()) {
            if (System.currentTimeMillis() - f.lastModified() > expiry) {
                f.delete();
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public Uri get(URL downloadURL)
    throws IOException {
        File f = getCacheFile(downloadURL);
        return Uri.fromFile(f);
    }

    public OutputStream getOutputStream(URL downloadURL)
    throws IOException {
        File f = getCacheFile(downloadURL);
        return new BufferedOutputStream(new FileOutputStream(f));
    }

    private File getCacheFile(URL downloadURL) {
        String name = downloadURL.getFile();
        name = name.substring(name.lastIndexOf("/")+1, name.length());
        return new File(dir, downloadURL.hashCode() + "_" + name);
    }
}
