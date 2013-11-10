package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ConcurrentDownloadService extends ConcurrentIntentService {

    public static final int MAX_CONCURRENT_DOWNLOADS = 3;
    public static final Executor DOWNLOAD_THREAD_POOL =
        Executors.newFixedThreadPool(
            MAX_CONCURRENT_DOWNLOADS, new ThreadFactory(){
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    Process.setThreadPriority(
                            Process.THREAD_PRIORITY_BACKGROUND);
                    t.setName("download");
                    return t;
                }
            });
    public static final String DOWNLOAD_FROM_URL = "from_url";
    public static final String MESSENGER = "messenger";
    public static final int SUCCESSFUL = "download_successful".hashCode();
    public static final int FAILED = "download_failed".hashCode();

    public static int startDownload(String url, Context ctx, Messenger messenger) {
        Intent intent = new Intent(ctx, ConcurrentDownloadService.class);
        intent.putExtra(ConcurrentDownloadService.DOWNLOAD_FROM_URL, url);
        intent.putExtra(ConcurrentDownloadService.REQUEST_ID, url.hashCode());
        intent.putExtra(ConcurrentDownloadService.MESSENGER, messenger);
        ctx.startService(intent);
        return url.hashCode();
    }

    private SimpleDownloader downloader;

    public ConcurrentDownloadService() {
        super(DOWNLOAD_THREAD_POOL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloader = new SimpleDownloader(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Messenger messenger = intent.getParcelableExtra(MESSENGER);
        String url = intent.getStringExtra(DOWNLOAD_FROM_URL);
        try {
            Uri local = downloader.download(url);
            sendMessage(messenger, SUCCESSFUL, url.hashCode(), local);
        } catch (IOException exc) {
            sendMessage(messenger, FAILED, url.hashCode(), null);
        }
    }

    private void sendMessage(Messenger messenger, int status, int requestId, Uri data) {
        try {
            messenger.send(
                 Message.obtain(null, status, requestId, 0, data));
        } catch (RemoteException exc) {
            Log.e(LaunchActivity.TAG, "unable to send success message to client.");
        }
    }
}
