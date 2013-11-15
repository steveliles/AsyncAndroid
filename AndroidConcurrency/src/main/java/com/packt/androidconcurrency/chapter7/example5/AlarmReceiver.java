package com.packt.androidconcurrency.chapter7.example5;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.packt.androidconcurrency.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(context.getString(R.string.ch7_ex5))
                .setContentText("Broadcast received, starting Service");
        NotificationManager nm = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(R.string.ch7_ex5, builder.build());

        int primeToFind = intent.getIntExtra(WakeLockPrimesIntentService.PARAM, 2);
        Intent service = new Intent(context, WakeLockPrimesIntentService.class);
        service.putExtra(WakeLockPrimesIntentService.PARAM, primeToFind);

        // create and acquire partial wakelock, associating it with the
        // service Intent so that the service can release the lock...
        WakeLockPrimesIntentService.newWakeLock(
            context, PowerManager.PARTIAL_WAKE_LOCK,
            WakeLockPrimesIntentService.class.getSimpleName(), service);

        context.startService(service);
    }
}
