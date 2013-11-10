package com.packt.androidconcurrency.chapter6.example2;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.packt.androidconcurrency.R;

import java.lang.ref.WeakReference;
import java.math.BigInteger;

public class LocalPrimesService extends Service {

    public interface Callback {
        public boolean onResult(BigInteger result);
    }

    public class Access extends Binder {
        public LocalPrimesService getService() {
            return LocalPrimesService.this;
        }
    };

    private final Access binder = new Access();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void calculateNthPrime(final int n, Callback activity) {
        final WeakReference<Callback> maybeCallback =
            new WeakReference<Callback>(activity);
        new AsyncTask<Void,Void,BigInteger>(){
            @Override
            protected BigInteger doInBackground(Void... params) {
                BigInteger prime = new BigInteger("2");
                for (int i=0; i<n; i++) {
                    prime = prime.nextProbablePrime();
                }
                return prime;
            }

            @Override
            protected void onPostExecute(BigInteger result) {
                Callback callback = maybeCallback.get();
                if (callback != null) {
                    if (!callback.onResult(result)) {
                        notifyUser(n, result.toString());
                    }
                } else {
                    notifyUser(n, result.toString());
                }
            }
        }.execute();
    }

    private void notifyUser(int primeToFind, String result) {
        String msg = String.format("The %sth prime is %s",
            primeToFind, result);
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(getString(R.string.primes_service))
                .setContentText(msg);
        NotificationManager nm = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(primeToFind, builder.build());
    }
}
