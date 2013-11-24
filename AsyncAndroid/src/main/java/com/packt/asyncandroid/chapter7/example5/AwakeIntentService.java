package com.packt.asyncandroid.chapter7.example5;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public abstract class AwakeIntentService extends IntentService {

    public static void startServiceWithWakeLock(Context ctx, Intent service) {
        WakefulBroadcastReceiver.startWakefulService(ctx, service);
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
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
