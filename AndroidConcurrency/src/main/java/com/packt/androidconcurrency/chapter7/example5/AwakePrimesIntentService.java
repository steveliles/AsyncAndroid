package com.packt.androidconcurrency.chapter7.example5;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;

public class AwakePrimesIntentService extends AwakeIntentService {

    public static final String PARAM = "prime_to_find";
    public static final String RESULT = "nth_prime";

    public AwakePrimesIntentService() {
        super("primes");
    }

    @Override
    protected void doWithPartialWakeLock(Intent intent) {
        int n = intent.getIntExtra(PARAM, -1);
        BigInteger prime = calculateNthPrime(n);
        notifyUser(n, prime.toString());
    }

    private BigInteger calculateNthPrime(int primeToFind) {
        BigInteger prime = new BigInteger("2");
        for (int i=0; i<primeToFind; i++) {
            prime = prime.nextProbablePrime();
        }
        return prime;
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
