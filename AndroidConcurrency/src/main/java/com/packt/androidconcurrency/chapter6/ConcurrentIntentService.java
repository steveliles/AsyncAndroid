package com.packt.androidconcurrency.chapter6;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.packt.androidconcurrency.LaunchActivity;

import java.util.concurrent.ExecutorService;

/**
 * A Service that implements Intent-driven behavior - like IntentService,
 * but uses an ExecutorService to perform the background work instead of
 * IntentService's single worker thread, giving complete control over the
 * level of concurrency to the sub-class through its configuration of
 * the ExecutorService.
 *
 * Also like IntentService, ConcurrentIntentService will stop itself
 * when all pending tasks have completed, so there is no need for
 * clients to ever ask the service to stop.
 */
public abstract class ConcurrentIntentService extends Service {

    public static final String REQUEST_ID = "request_id";

    private final CompletionHandler handler = new CompletionHandler();
    private final SparseArray<Runnable> active = new SparseArray<Runnable>();
    private final ExecutorService executor;

    public ConcurrentIntentService(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Runs in the background using the ExecutorService.
     *
     * When all requests have been handled, this Service stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * To prevent duplication of work, set an IntExtra on the Intent
     * containing a REQUEST_ID which in some way represents or
     * identifies the task - for example a hashcode of a URL that is
     * to be downloaded. If a REQUEST_ID is _currently_ being processed,
     * intents which arrive with the same id will be dropped.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    protected abstract void onHandleIntent(Intent intent);

    @Deprecated
    @Override
    public void onStart(Intent intent, int startId) {
        int requestId = intent.getIntExtra(REQUEST_ID, -1);
        if (requestId == -1)
            requestId = intent.hashCode(); // extremely unlikely to clash with other requestId's

        if (active.get(requestId) == null) {
            Runnable task = newRunnable(intent);
            active.put(requestId, task);
            executor.execute(task);
        } else {
            Log.i(LaunchActivity.TAG, "request " + requestId + " currently running, not re-scheduling.");
        }
    }

    private Runnable newRunnable(final Intent intent) {
        return new Runnable(){
            @Override
            public void run() {
                try {
                    onHandleIntent(intent);
                } finally {
                    handler.sendMessage(Message.obtain(handler));
                }
            }
        };
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LaunchActivity.TAG, "stopping " + getClass().getName());
        executor.shutdown();
        Log.d(LaunchActivity.TAG, "stopped " + getClass().getName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CompletionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (active.size() == 0) {
                Log.d(LaunchActivity.TAG, "no more tasks, stopping");
                stopSelf();
            } else {
                Log.d(LaunchActivity.TAG, active.size() + " active tasks");
            }
        }
    }
}
