package com.packt.asyncandroid.chapter6;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.packt.asyncandroid.LaunchActivity;

import java.util.concurrent.Executor;

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
    private final Executor executor;
    private int counter;

    public ConcurrentIntentService(Executor executor) {
        this.executor = executor;
    }

    /**
     * Runs in the background using the ExecutorService.
     *
     * When all requests have been handled, this Service stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    protected abstract void onHandleIntent(Intent intent);

    @Deprecated
    @Override
    public void onStart(final Intent intent, int startId) {
        counter++;
        executor.execute(new Runnable(){
            @Override
            public void run() {
                try {
                    onHandleIntent(intent);
                } finally {
                    handler.sendMessage(Message.obtain(handler));
                }
            }
        });
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CompletionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (--counter == 0) {
                Log.i(LaunchActivity.TAG, "no more tasks, stopping");
                stopSelf();
            } else {
                Log.i(LaunchActivity.TAG, counter + " active tasks");
            }
        }
    }
}
