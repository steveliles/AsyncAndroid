package com.packt.androidconcurrency.chapter7.example5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public abstract class AwakeIntentService extends IntentService {

    public AwakeIntentService(String name) {
        super(name);
    }

    protected abstract void doInPartialWakeLock(Intent intent);

    @Override
    protected final void onHandleIntent(Intent intent) {
        try {
            doInPartialWakeLock(intent);
        } finally {
            ((AwakeApplication)getApplication()).releaseWakeLock();
        }
    }

    public static class Locks {

        private static final String TAG = "service_wake_lock";
        private final Object guard = new Object();
        private PowerManager.WakeLock lock;

        private PowerManager.WakeLock acquireLock(Context ctx) {
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

        public void releaseLock() {
            synchronized(guard) {
                if ((lock != null) && (lock.isHeld())) {
                    lock.release();
                }
            }
        }

        public void startServiceWithWakeLock(Context ctx, Intent service) {
            acquireLock(ctx);
            ctx.startService(service);
        }
    }
}
