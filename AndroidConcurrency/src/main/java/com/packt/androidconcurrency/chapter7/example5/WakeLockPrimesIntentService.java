package com.packt.androidconcurrency.chapter7.example5;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WakeLockPrimesIntentService extends IntentService {

    private static final String WAKELOCK = "primes_wake_lock";
    public static final String PARAM = "prime_to_find";
    public static final String RESULT = "nth_prime";

    private static final Object guard = new Object();
    private static PowerManager.WakeLock lock;

    public static PowerManager.WakeLock acquireLock(Context ctx) {
        synchronized(guard) {
            if (lock == null) {
                PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
                lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK);
                lock.setReferenceCounted(true);
                lock.acquire();
            }
        }
        return lock;
    }

    public static void releaseLock() {
        synchronized(guard) {
            if ((lock != null) && (lock.isHeld())) {
                lock.release();
            }
        }
    }

    public WakeLockPrimesIntentService() {
        super("primes");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            int n = intent.getIntExtra(PARAM, -1);
            BigInteger prime = calculateNthPrime(n);
            notifyUser(n, prime.toString());
        } finally {
            releaseLock();
        }
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
