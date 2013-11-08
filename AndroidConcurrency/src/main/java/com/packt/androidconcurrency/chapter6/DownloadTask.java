package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.util.SparseArray;

import com.packt.androidconcurrency.LaunchActivity;

public abstract class DownloadTask<T> {

    private static final Handler handler = new DownloadTaskHandler(Looper.getMainLooper());
    private static final Messenger messenger = new Messenger(handler);
    private static final SparseArray<DownloadTask> tasks = new SparseArray<DownloadTask>();

    /**
     * Must be called when Activity restarts or finishes to avoid
     * memory leaks for the duration of pending downloads.
     */
    public static void clearCallbacks() {
        if (!isMainThread())
            throw new RuntimeException(
                "DownloadTask.clearCallbacks must be called on the main thread!");
        tasks.clear();
    }

    private String url;

    public DownloadTask(String url) {
        this.url = url;
    }

    public abstract T convertInBackground(Uri data)
    throws Exception;

    public abstract void onSuccess(T result);

    public void onFailure() {
        Log.w(LaunchActivity.TAG, "download failed: " + url);
    }

    public void onError(Exception exc) {
        Log.e(LaunchActivity.TAG, "conversion failed: " + url, exc);
    }

    public void execute(Context ctx) {
        if (!isMainThread()) {
            // post to the main thread so that we're always
            // starting the service from the main thread and
            // so that we're always interacting with the 'tasks'
            // array from the main thread (so no sync needed).
            final Context app = ctx.getApplicationContext();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DownloadTask.this.execute(app);
                }
            });
        } else {
            Intent intent = new Intent(ctx, ConcurrentDownloadService.class);
            intent.putExtra(ConcurrentDownloadService.DOWNLOAD_FROM_URL, url);
            intent.putExtra(ConcurrentDownloadService.REQUEST_ID, url.hashCode());
            intent.putExtra(ConcurrentDownloadService.MESSENGER, messenger);
            ctx.startService(intent);
            tasks.put(url.hashCode(), this);
        }
    }

    private static boolean isMainThread() {
        Thread c = Thread.currentThread();
        Thread m = Looper.getMainLooper().getThread();
        return c.equals(m);
    }

    private static class DownloadTaskHandler extends Handler {
        public DownloadTaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadTask task = tasks.get(msg.arg1);
            if (task != null) {
                try {
                    if (ConcurrentDownloadService.SUCCESSFUL == msg.what) {
                        convert(task, (Uri)msg.obj);
                    } else {
                        task.onFailure();
                    }
                } finally {
                    tasks.remove(msg.arg1);
                }
            }
        }

        private <T> void convert(final DownloadTask<T> task, final Uri uri) {
            AsyncTask<Void,Void,T> at = new AsyncTask<Void,Void,T>(){
                private Exception e;

                @Override
                protected T doInBackground(Void... params) {
                    try {
                        return task.convertInBackground(uri);
                    } catch (Exception exc) {
                        e = exc;
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(T t) {
                    if (e == null) {
                        task.onSuccess(t);
                    } else {
                        task.onError(e);
                    }
                }
            };

            if (Build.VERSION.SDK_INT >= 11) {
                at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                at.execute();
            }
        }
    }
}
