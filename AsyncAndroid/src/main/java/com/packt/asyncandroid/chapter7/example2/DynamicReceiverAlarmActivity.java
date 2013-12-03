package com.packt.asyncandroid.chapter7.example2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

import com.packt.asyncandroid.R;

import java.util.concurrent.TimeUnit;

public class DynamicReceiverAlarmActivity extends Activity {

    private static final long FIVE_SECONDS = TimeUnit.SECONDS.toMillis(5);
    private static final String MSG = "message";
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example2_layout);

        Intent intent = new Intent("dynamic-receiver");
        intent.putExtra(MSG, "Remember to try out all of the alarm examples!");
        final PendingIntent pending = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Button schedule = (Button)findViewById(R.id.schedule);
        final Button unschedule = (Button)findViewById(R.id.unschedule);
        unschedule.setEnabled(false);

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC, System.currentTimeMillis()+FIVE_SECONDS, pending);
                schedule.setEnabled(false);
                unschedule.setEnabled(true);
            }
        });

        unschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
                am.cancel(pending);
                schedule.setEnabled(true);
                unschedule.setEnabled(false);
            }
        });

        final Button register = (Button)findViewById(R.id.register);
        final Button unregister = (Button)findViewById(R.id.unregister);
        unregister.setEnabled(false);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiver == null) {
                    registerReceiver(
                        receiver = new AlarmReceiver(),
                    new IntentFilter("dynamic-receiver"));
                    register.setEnabled(false);
                    unregister.setEnabled(true);
                }
            }
        });

        unregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiver != null) {
                    unregisterReceiver(receiver);
                    register.setEnabled(true);
                    unregister.setEnabled(false);
                    receiver = null;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing() && (receiver != null)) {
            unregisterReceiver(receiver);
        }
    }

    private static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent activity = new Intent(context, DynamicReceiverAlarmActivity.class);
            activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pending = PendingIntent.getActivity(
                context, 0, activity, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setContentTitle(context.getString(R.string.ch7_ex2))
                    .setContentText(intent.getStringExtra(MSG))
                    .setContentIntent(pending)
                    .setAutoCancel(true);
            NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(R.string.ch7_ex2, builder.build());
        }
    }
}
