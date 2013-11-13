package com.packt.androidconcurrency.chapter7.example1;

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
                .setContentTitle(context.getString(R.string.ch7_ex1))
                .setContentText("Result!");
        NotificationManager nm = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
    }
}
