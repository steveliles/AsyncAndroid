package com.packt.androidconcurrency.chapter7.example5;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

        int primeToFind = intent.getIntExtra(AwakePrimesIntentService.PARAM, 2);
        Intent service = new Intent(context, AwakePrimesIntentService.class);
        service.putExtra(AwakePrimesIntentService.PARAM, primeToFind);

        AwakeApplication.get().startServiceWithWakeLock(service);
    }
}
