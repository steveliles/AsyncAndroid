package com.packt.androidconcurrency.chapter6;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.util.SparseArray;

public abstract class DownloadTask {

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

    public abstract void onSuccess(Uri data);

    public abstract void onFailure();

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
            Intent intent = new Intent(ctx, DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_FROM_URL, url);
            intent.putExtra(DownloadService.REQUEST_ID, url.hashCode());
            intent.putExtra(DownloadService.MESSENGER, messenger);
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
                    if (DownloadService.SUCCESSFUL == msg.what) {
                        task.onSuccess((Uri)msg.obj);
                    } else {
                        task.onFailure();
                    }
                } finally {
                    tasks.remove(msg.arg1);
                }
            }
        }
    }
}
