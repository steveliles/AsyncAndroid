package com.packt.androidconcurrency.chapter6.example3;

import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.chapter6.AsyncTaskIntentService;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class DownloadService<T> extends AsyncTaskIntentService {

    public static final String REQUEST_ID = "request_id";
    public static final String DOWNLOAD_FROM_URL = "from_url";
    public static final String MESSENGER = "messenger";

    public static final int SUCCESSFUL = "download_successful".hashCode();
    public static final int FAILED = "download_failed".hashCode();

    public interface Cache {
        public boolean exists(URL downloadURL);

        public InputStream getInputStream(URL downloadURL)
        throws IOException;

        public OutputStream getOutputStream(URL downloadURL)
        throws IOException;
    }

    private Cache cache;

    @Override
    public void onCreate() {
        super.onCreate();
        cache = initCache();
    }

    /**
     * @return a cache implementation ready for use.
     */
    protected abstract Cache initCache();

    /**
     * @param in inputstream from the cached data
     * @return a parcelable version of the data
     */
    protected abstract T convert(InputStream in);

    @Override
    protected void onHandleIntent(Intent intent) {
        Messenger messenger = intent.getParcelableExtra(MESSENGER);
        int requestId = intent.getIntExtra(REQUEST_ID, -1);

        try {
            URL from = new URL(intent.getStringExtra(DOWNLOAD_FROM_URL));
            if (!cache.exists(from)) {
                download(from);
            }
            sendSuccessMessage(messenger, requestId, cache.getInputStream(from));
        } catch (IOException exc) {
            System.out.println(exc);
            sendErrorMessage(messenger, requestId);
        }
    }

    private void download(URL from) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)from.openConnection();
        InputStream in = conn.getInputStream();
        OutputStream out = cache.getOutputStream(from);

        int length;
        byte[] buffer = new byte[1024];
        while ((length = in.read(buffer)) > -1) {
            out.write(buffer, 0, length);
            out.flush();
        }
        close(in);
        close(out);
    }

    private void sendSuccessMessage(Messenger messenger, int requestId, InputStream in) {
        try {
            Message msg = Message.obtain();
            msg.what = SUCCESSFUL;
            msg.arg1 = requestId;
            msg.obj = convert(in);
            messenger.send(msg);
        } catch (RemoteException exc) {
            Log.e(LaunchActivity.TAG, "unable to send success message to client.");
        } finally {
            close(in);
        }
    }

    private void sendErrorMessage(Messenger messenger, int requestId) {
        try {
            Message msg = Message.obtain();
            msg.what = FAILED;
            msg.arg1 = requestId;
            messenger.send(msg);
        } catch (RemoteException exc) {
            Log.e(LaunchActivity.TAG, "unable to send failure message to client.");
        }
    }

    private final void close(Closeable stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException anExc) {
            Log.w(LaunchActivity.TAG, "problem closing stream", anExc);
        }
    }
}
