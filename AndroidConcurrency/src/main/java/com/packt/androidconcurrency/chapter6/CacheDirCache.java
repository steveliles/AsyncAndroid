package com.packt.androidconcurrency.chapter6;

import android.content.Context;

import com.packt.androidconcurrency.chapter6.DownloadService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class CacheDirCache implements DownloadService.Cache {

    private File dir;

    public CacheDirCache(Context ctx) {
        dir = ctx.getCacheDir();
    }

    @Override
    public boolean exists(URL downloadURL) {
        File f = getCacheFile(downloadURL);
        return f.exists();
    }

    @Override
    public InputStream getInputStream(URL downloadURL)
    throws IOException {
        File f = getCacheFile(downloadURL);
        return new BufferedInputStream(new FileInputStream(f));
    }

    @Override
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
