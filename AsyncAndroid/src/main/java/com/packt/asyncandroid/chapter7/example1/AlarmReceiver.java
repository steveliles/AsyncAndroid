package com.packt.asyncandroid.chapter7.example1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.packt.asyncandroid.R;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String MSG = "message";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(context.getString(R.string.ch7_ex1))
                .setContentText(intent.getStringExtra(MSG));
        NotificationManager nm = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(R.string.ch7_ex1, builder.build());
    }
}
