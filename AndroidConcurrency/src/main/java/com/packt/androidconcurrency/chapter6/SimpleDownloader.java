package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.net.Uri;

import com.packt.androidconcurrency.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleDownloader {

    private LocalDownloadCache cache;
    private ConcurrentHashMap<String,String> locks;

    public SimpleDownloader(Context ctx){
        cache = new LocalDownloadCache(ctx);
        locks = new ConcurrentHashMap<String,String>();
    }

    public Uri download(String from) throws IOException {
        locks.putIfAbsent(from,from);
        from = locks.get(from);
        try {
            synchronized(from) {
                if (!cache.exists(from)) {
                   doDownload(from);
                }
                return cache.get(from);
            }
        } finally {
            locks.remove(from);
        }
    }

    private void doDownload(String from) throws IOException {
        URL url = new URL(from);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            InputStream in = conn.getInputStream();
            OutputStream out = cache.getOutputStream(from);
            int length;
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
                out.flush();
            }
            Streams.close(in, out);
        } finally {
            conn.disconnect();
        }
    }
}
