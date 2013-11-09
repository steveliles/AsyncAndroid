package com.packt.androidconcurrency.chapter5.example5;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.packt.androidconcurrency.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Posts an image directly as the body of the http request.
 * Note that this is NOT a multi-part upload.
 */
public class ImageUploader {

    interface ProgressCallback {
        void onProgress(int max, int progress);
        void onComplete(String msg);
    }

    private static final int ONE_MINUTE = 60000;
    private static final String TAG = "androidconcurrency";

    private ContentResolver content;

    public ImageUploader(ContentResolver content) {
        this.content = content;
    }

    public boolean upload(Uri data, ProgressCallback callback) {
        HttpURLConnection conn = null;
        try {
            URL destination = new URL("http://devnullupload.appspot.com/upload");
            int len = getContentLength(data);
            conn = (HttpURLConnection) destination.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setReadTimeout(ONE_MINUTE);
            conn.setConnectTimeout(ONE_MINUTE);
            conn.setRequestMethod("POST");
            conn.setFixedLengthStreamingMode(len);

            conn.setRequestProperty("Content-Type", "image/jpg");
            conn.setRequestProperty("Content-Length", len + "");
            conn.setRequestProperty("Filename", data.getLastPathSegment() + ".jpg");

            InputStream in = null;
            OutputStream out = null;

            try {
                pump(
                    in = content.openInputStream(data),
                    out = conn.getOutputStream(),
                    callback, len);
            } finally {
                Streams.close(in, out);
            }

            if ((conn.getResponseCode() >= 200) && (conn.getResponseCode() < 400)) {
                Log.i(TAG, "Uploaded Successfully!");
                return true;
            } else {
                Log.w(TAG, "Upload failed with return-code " + conn.getResponseCode());
                return false;
            }
        } catch (IOException exc) {
            Log.e(TAG, "upload failed", exc);
            return false;
        } finally {
            conn.disconnect();
        }
    }

    private int getContentLength(Uri uri)
    throws IOException {
        ParcelFileDescriptor pfd = null;
        try {
            pfd = content.openFileDescriptor(uri, "r");
            return (int)pfd.getStatSize();
        } finally {
            if (pfd != null)
                pfd.close();
        }
    }

    private void pump(InputStream in, OutputStream out, ProgressCallback callback, int len)
            throws IOException {
        try {
            int length,i=0,size=256;
            byte[] buffer = new byte[size];

            while ((length = in.read(buffer)) > -1) {
                out.write(buffer, 0, length);
                out.flush();
                callback.onProgress(len, ++i*size);
            }
        } finally {
            Streams.close(in, out);
        }
    }
}
