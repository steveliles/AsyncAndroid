package com.packt.androidconcurrency.chapter5.example1;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;

import java.math.BigInteger;

public class PrimesIntentService extends IntentService {

    public static final String PARAM = "prime_to_find";
    public static final String MESSENGER = "messenger";
    public static final int INVALID = "invalid".hashCode();
    public static final int RESULT = "nth_prime".hashCode();

    public PrimesIntentService() {
        super("primes");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int primeToFind = intent.getIntExtra(PARAM, -1);
        Messenger messenger = intent.getParcelableExtra(MESSENGER);
        try {
            if (primeToFind < 2) {
                messenger.send(Message.obtain(null, INVALID));
            } else {
                messenger.send(Message.obtain(null, RESULT, calculateNthPrime(primeToFind)));
            }
        } catch (RemoteException anExc) {
            Log.e(LaunchActivity.TAG, "Unable to send message", anExc);
        }
    }

    private BigInteger calculateNthPrime(int primeToFind) {
        BigInteger prime = new BigInteger("2");
        for (int i=0; i<primeToFind; i++) {
            prime = prime.nextProbablePrime();
        }
        return prime;
    }
}
