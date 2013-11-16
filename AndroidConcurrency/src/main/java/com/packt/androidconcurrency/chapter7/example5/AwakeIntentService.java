package com.packt.androidconcurrency.chapter7.example5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class AwakeIntentService extends IntentService {

    private static final String TAG = "service_wake_lock";
    private static final Object guard = new Object();
    private static PowerManager.WakeLock lock;

    private static PowerManager.WakeLock acquireLock(Context ctx) {
        synchronized(guard) {
            if (lock == null) {
                PowerManager pm = (PowerManager)
                    ctx.getSystemService(Context.POWER_SERVICE);
                lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                lock.setReferenceCounted(true);
            }
        }
        lock.acquire();
        return lock;
    }

    private static void releaseLock() {
        synchronized(guard) {
            if ((lock != null) && (lock.isHeld())) {
                lock.release();
            }
        }
    }

    public static void startServiceWithWakeLock(Context ctx, Intent service) {
        acquireLock(ctx);
        ctx.startService(service);
    }

    public AwakeIntentService(String name) {
        super(name);
    }

    protected abstract void doWithPartialWakeLock(Intent intent);

    @Override
    protected final void onHandleIntent(Intent intent) {
        try {
            doWithPartialWakeLock(intent);
        } finally {
            releaseLock();
        }
    }
}
