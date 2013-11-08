package com.packt.androidconcurrency.chapter6;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;

import java.io.IOException;

public class AsyncTaskDownloadService extends AsyncTaskIntentService {

    public static final String REQUEST_ID = "request_id";
    public static final String DOWNLOAD_FROM_URL = "from_url";
    public static final String MESSENGER = "messenger";
    public static final int SUCCESSFUL = "download_successful".hashCode();
    public static final int FAILED = "download_failed".hashCode();

    private SimpleDownloader downloader;

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
            Uri data = downloader.download(url);
            sendMessage(messenger, SUCCESSFUL, url.hashCode(), data);
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
