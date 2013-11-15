package com.packt.androidconcurrency.chapter6.example3;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;

import java.math.BigInteger;

public class MessageSendingPrimesService extends Service {

    public static final int RESULT = "nth_prime".hashCode();

    public class Access extends Binder {
        public MessageSendingPrimesService getService() {
            return MessageSendingPrimesService.this;
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

    public void calculateNthPrime(final int n, final Messenger messenger) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                BigInteger prime = new BigInteger("2");
                for (int i=0; i<n; i++) {
                    prime = prime.nextProbablePrime();
                }
                try {
                    messenger.send(Message.obtain(null, RESULT, prime.toString()));
                } catch (RemoteException exc) {
                    Log.e(LaunchActivity.TAG, "unable to send msg", exc);
                }
                return null;
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
