package com.packt.androidconcurrency.chapter6.example4;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;

public class PrimesServiceWithBroadcast extends Service {

    public static final String PRIMES_BROADCAST = "com.packt.CH6_PRIMES_BROADCAST";
    public static final String HANDLED = "intent_handled";
    public static final String RESULT = "nth_prime";

    public class Access extends Binder {
        public PrimesServiceWithBroadcast getService() {
            return PrimesServiceWithBroadcast.this;
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

    public void calculateNthPrime(final int n) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                BigInteger prime = new BigInteger("2");
                for (int i=0; i<n; i++)
                    prime = prime.nextProbablePrime();
                if (!broadcastResult(prime.toString()))
                    notifyUser(n, prime.toString());
                return null;
            }
        }.execute();
    }

    private boolean broadcastResult(String result) {
        Intent intent = new Intent(PRIMES_BROADCAST);
        intent.putExtra(RESULT, result);
        LocalBroadcastManager.getInstance(this).
                sendBroadcastSync(intent);
        return intent.getBooleanExtra(HANDLED, false);
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
