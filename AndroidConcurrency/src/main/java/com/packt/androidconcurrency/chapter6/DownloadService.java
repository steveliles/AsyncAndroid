package com.packt.androidconcurrency.chapter6;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends AsyncTaskIntentService {

    public static final String REQUEST_ID = "request_id";
    public static final String DOWNLOAD_FROM_URL = "from_url";
    public static final String MESSENGER = "messenger";
    public static final int SUCCESSFUL = "download_successful".hashCode();
    public static final int FAILED = "download_failed".hashCode();
    public static final long FIVE_MINUTES = 5L * 60L * 1000L;

    private LocalDownloadCache cache;

    @Override
    public void onCreate() {
        super.onCreate();
        cache = new LocalDownloadCache(FIVE_MINUTES, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Messenger messenger = intent.getParcelableExtra(MESSENGER);
        int requestId = intent.getIntExtra(REQUEST_ID, -1);
        try {
            URL from = new URL(intent.getStringExtra(DOWNLOAD_FROM_URL));
            if (!cache.exists(from))
                download(from);
            sendMessage(messenger, SUCCESSFUL, requestId, cache.get(from));
        } catch (IOException exc) {
            sendMessage(messenger, FAILED, requestId, null);
        }
    }

    private void download(URL from) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)from.openConnection();
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

    private void sendMessage(Messenger messenger, int status, int requestId, Uri data) {
        try {
            messenger.send(
                Message.obtain(null, status, requestId, 0, data));
        } catch (RemoteException exc) {
            Log.e(LaunchActivity.TAG, "unable to send success message to client.");
        }
    }
}
