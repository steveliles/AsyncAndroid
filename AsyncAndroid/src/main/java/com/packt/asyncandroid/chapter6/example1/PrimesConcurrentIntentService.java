package com.packt.asyncandroid.chapter6.example1;

import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.packt.asyncandroid.LaunchActivity;
import com.packt.asyncandroid.chapter6.ConcurrentIntentService;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class PrimesConcurrentIntentService extends ConcurrentIntentService {

    private static final int MAX_CONCURRENT_CALCULATIONS = 5;
    public static final String PARAM = "prime_to_find";
    public static final String MSNGR = "messenger";
    public static final int INVALID = "invalid".hashCode();
    public static final int RESULT = "nth_prime".hashCode();

    public PrimesConcurrentIntentService() {
        super(Executors.newFixedThreadPool(
            MAX_CONCURRENT_CALCULATIONS,
            new ThreadFactory(){
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.setName("download");
                    return t;
                }
            }));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int primeToFind = intent.getIntExtra(PARAM, -1);
        Messenger messenger = intent.getParcelableExtra(MSNGR);
        try {
            if (primeToFind < 2) {
                messenger.send(Message.obtain(null, INVALID));
            } else {
                messenger.send(Message.obtain(null, RESULT, primeToFind, 0, calculateNthPrime(primeToFind)));
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
