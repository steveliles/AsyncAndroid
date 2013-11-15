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

public class WakeLockPrimesIntentService extends IntentService {

    private static final String WAKELOCK = "wake_lock";

    private static final SparseArray<PowerManager.WakeLock> locks =
        new SparseArray<PowerManager.WakeLock>();

    /**
     * Creates a wakelock, acquires it, and adds a reference to it into the
     * given Intent. When this Intent is sent to the Service, the Service
     * can retrieve the lock and acquire it in turn, to ensure there is no
     * gap between wake locks.
     *
     * @param ctx a context to use to create the lock
     * @param flags for the wakelock - see PowerManager
     * @param intent which will be dispatched to the service - an Extra will
     *               be added to this intent allowing the service to find the
     *               correct lock.
     * @return WakeLock which has already been acquired
     */
    public static PowerManager.WakeLock newWakeLock(
        Context ctx, int flags, String tag, Intent intent) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock lock = pm.newWakeLock(flags, tag);
        locks.put(lock.hashCode(), lock);
        intent.putExtra(WAKELOCK, lock.hashCode());
        return lock;
    }

    /**
     * Get the wakelock associated with this intent, if there is one.
     * @throws RuntimeException if there is no wakelock associated with the intent.
     * @param intent
     * @return WakeLock
     */
    private static PowerManager.WakeLock getWakeLock(Intent intent) {
        int lockId = intent.getIntExtra(WAKELOCK, -1);
        PowerManager.WakeLock lock = locks.get(lockId);
        if (lock == null)
            throw new RuntimeException("Intent does not reference a lock!");
        return lock;
    }

    public static final String PARAM = "prime_to_find";
    public static final String RESULT = "nth_prime";

    public WakeLockPrimesIntentService() {
        super("primes");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PowerManager.WakeLock lock = getWakeLock(intent);
        lock.acquire();
        try {
            int n = intent.getIntExtra(PARAM, -1);
            BigInteger prime = calculateNthPrime(n);
            notifyUser(n, prime.toString());
        } finally {
            locks.remove(lock.hashCode());
            lock.release();
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
