package com.packt.androidconcurrency.chapter6;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import java.util.ArrayList;
import java.util.List;

/**
 * A service that uses a Messenger to receive work through a Handler, and
 * executes that work using AsyncTask.
 */
public abstract class AsyncTaskMessengerService extends Service {

    private final Messenger messenger;

    public AsyncTaskMessengerService() {
        messenger = new Messenger(new AsyncTaskHandler());
    }

    protected abstract void doInBackground(int what, Object obj, Messenger replyTo);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public class AsyncTaskHandler extends Handler {
        private final List<AsyncTask<Void,Void,Void>> active =
            new ArrayList<AsyncTask<Void,Void,Void>>();

        @Override
        public final void handleMessage(Message msg) {
            execute(newAsyncTask(msg.what, msg.obj, msg.replyTo));
        }

        private AsyncTask<Void,Void,Void> newAsyncTask(
            final int what, final Object obj, final Messenger replyTo) {
            return new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    AsyncTaskMessengerService.this.doInBackground(what, obj, replyTo);
                    return null;
                }
            };
        }

        private void execute(AsyncTask<Void,Void,Void> task) {
            active.add(task);
            if (Build.VERSION.SDK_INT >= 11) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        }
    }
}
