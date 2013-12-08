package com.packt.asyncandroid.chapter6.example6;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.util.SparseArray;

import com.packt.asyncandroid.LaunchActivity;
import com.packt.asyncandroid.chapter6.ConcurrentDownloadService;

/**
 * A simple class that encapsulates invoking the ConcurrentDownloadService
 * with a URL to download, collecting the response from the download service
 * and doing some data-handling work in the background using AsyncTask, for
 * example to load the downloaded data into a Bitmap object off the main thread.
 *
 * This class is modelled on AsyncTask, so it should feel very familiar.
 *
 * It can be used like this:
 *
 * <pre>
 * new DownloadTask&lt;Bitmap&gt;(url) {
 *     @Override
 *     public Bitmap convertInBackground(Uri data) throws Exception {
 *         InputStream in = null;
 *         try {
 *             return BitmapFactory.decodeStream(in=openStream(data));
 *         } finally {
 *             Streams.close(in);
 *         }
 *     }
 *
 *     @Override
 *     public void onSuccess(Bitmap data) {
 *         ImageView image = (ImageView) findViewById(R.id.img);
 *         image.setImageBitmap(data);
 *     }
 * }.execute(context);
 * </pre>
 *
 * IMPORTANT: To avoid memory leaks caused by DownloadTask's lingering after the
 * enclosing Activity has finished or restarted, invoke DownloadTask.clearCallbacks()
 * from onPause or onStop in the Activity.
 *
 * See NasaImageOfTheDayActivity for two example uses:
 *  - download an RSS, then use the post-download background step to parse the XML
 *    before handing over to the main thread for display.
 *  - download images referenced by the RSS, then use the post-download step to
 *    load the images into Bitmap objects and hand them over to the main thread
 *    for display.
 *
 * @param <T> the target data-type that this task will convert downloaded data to.
 */
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
        Log.w(LaunchActivity.TAG, "startDownload failed: " + url);
    }

    public void onError(Exception exc) {
        Log.e(LaunchActivity.TAG, "conversion failed: " + url, exc);
    }

    public void execute(final Context ctx) {
        if (!isMainThread()) {
            // post to the main thread so that we're always
            // starting the service from the main thread and
            // so that we're always interacting with the 'tasks'
            // array from the main thread (so no sync needed).
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DownloadTask.this.execute(ctx);
                }
            });
        } else {
            int requestId = ConcurrentDownloadService
                .startDownload(url, ctx, messenger);
            tasks.put(requestId, this);
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
