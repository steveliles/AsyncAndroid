package com.packt.androidconcurrency.chapter5.example1;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;

public class PrimesIntentService extends IntentService {

    public static final String PRIMES_BROADCAST = "androidconcurrency.chapter5.example1.BROADCAST";
    public static final String PARAM = "prime_to_find";
    public static final String RESULT = "nth_prime";
    public static final String HANDLED = "intent_handled";

    public PrimesIntentService() {
        super("primes");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int primeToFind = intent.getIntExtra(PARAM, -1);
        if (primeToFind < 2) {
            invalidRequest();
        } else {
            calculateNthPrime(primeToFind);
        }
    }

    private void calculateNthPrime(int primeToFind) {
        BigInteger prime = new BigInteger("2");
        for (int i=0; i<primeToFind; i++) {
            prime = prime.nextProbablePrime();
        }

        if (!broadcastResultHandled(prime.toString()))
            notifyResult(primeToFind, prime.toString());
    }

    private void invalidRequest() {
        broadcastResultHandled("Please provide a prime index greater than 2");
    }

    private boolean broadcastResultHandled(String result) {
        Intent intent = new Intent(PRIMES_BROADCAST);
        intent.putExtra(RESULT, result);
        LocalBroadcastManager.getInstance(this).
            sendBroadcastSync(intent);
        return intent.getBooleanExtra(HANDLED, false);
    }

    private void notifyResult(int primeToFind, String result) {
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
