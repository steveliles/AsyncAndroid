package com.packt.androidconcurrency.chapter7.example5;

import android.app.Application;
import android.content.Intent;

public class AwakeApplication extends Application {

    private static AwakeApplication instance;

    public static AwakeApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private AwakeIntentService.Locks locks = new AwakeIntentService.Locks();

    public void startServiceWithWakeLock(Intent intent) {
        locks.startServiceWithWakeLock(this, intent);
    }

    public void releaseWakeLock() {
        locks.releaseLock();
    }
}
