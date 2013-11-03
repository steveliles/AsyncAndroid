package com.packt.androidconcurrency.chapter6;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A service that uses a Messenger to receive work through a Handler, and
 * executes that work using AsyncTask.
 */
public abstract class AsyncTaskMessengerService extends Service {

    private final List<AsyncTask<Void,Void,Void>> active =
            new ArrayList<AsyncTask<Void,Void,Void>>();

    private final Messenger messenger;
    private boolean bound;

    public AsyncTaskMessengerService() {
        messenger = new Messenger(new AsyncTaskHandler());
    }

    protected abstract void doInBackground(int what, int arg1, Object obj, Messenger replyTo);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        bound = true;
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // all clients have departed
        bound = false;
        if (active.isEmpty()) {
            Log.i(LaunchActivity.TAG, "no more clients or tasks, stopping.");
            stopSelf();
        } else {
            Log.i(LaunchActivity.TAG, "no more clients, will stop when all tasks complete.");
        }
        return true;
    }

    public class AsyncTaskHandler extends Handler {

        @Override
        public final void handleMessage(Message msg) {
            execute(newAsyncTask(
                msg.what, msg.arg1, msg.obj, msg.replyTo));
        }

        private AsyncTask<Void,Void,Void> newAsyncTask(
            final int what, final int arg1,
            final Object obj, final Messenger replyTo) {
            return new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    AsyncTaskMessengerService.this.doInBackground(
                        what, arg1, obj, replyTo);
                    return null;
                }

                @Override
                protected void onCancelled() {
                    onComplete();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    onComplete();
                }

                private void onComplete() {
                    active.remove(this);
                    if (active.isEmpty()) {
                        if (!bound) {
                            Log.i(LaunchActivity.TAG, "no more clients or tasks, stopping.");
                            stopSelf();
                        }
                    }
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
