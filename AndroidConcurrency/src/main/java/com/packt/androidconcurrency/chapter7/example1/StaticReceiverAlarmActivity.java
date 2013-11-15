package com.packt.androidconcurrency.chapter7.example1;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.packt.androidconcurrency.R;

public class StaticReceiverAlarmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example1_layout);

        Intent intent = new Intent("static-receiver");
        final PendingIntent pending = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
