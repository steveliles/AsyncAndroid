package com.packt.androidconcurrency.chapter7.example5;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.packt.androidconcurrency.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(ctx)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(ctx.getString(R.string.ch7_ex5))
                .setContentText("Broadcast received, starting Service");
        NotificationManager nm = (NotificationManager)
            ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(R.string.ch7_ex5, builder.build());

        int primeToFind = intent.getIntExtra(AwakePrimesIntentService.PARAM, 2);
        Intent service = new Intent(ctx, AwakePrimesIntentService.class);
        service.putExtra(AwakePrimesIntentService.PARAM, primeToFind);

        AwakeIntentService.startServiceWithWakeLock(ctx, service);
    }
}
