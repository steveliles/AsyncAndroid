package com.packt.androidconcurrency.chapter7.example1;

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

import com.packt.androidconcurrency.R;

public class AlarmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example1_layout);

        Intent intent = new Intent(this, AlarmReceiver.class);
        final PendingIntent pending = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Button schedule = (Button)findViewById(R.id.schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC, System.currentTimeMillis()+5000L, pending);
            }
        });

        Button unschedule = (Button)findViewById(R.id.unschedule);
        unschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
                am.cancel(pending);
            }
        });
    }
}
