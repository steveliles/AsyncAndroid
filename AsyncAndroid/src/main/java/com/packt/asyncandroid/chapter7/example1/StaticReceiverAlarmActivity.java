package com.packt.asyncandroid.chapter7.example1;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.packt.asyncandroid.R;

import java.util.concurrent.TimeUnit;

public class StaticReceiverAlarmActivity extends Activity {

    private static final long FIVE_SECONDS = TimeUnit.SECONDS.toMillis(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example1_layout);

        Button schedule = (Button)findViewById(R.id.schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
                am.set(
                    AlarmManager.RTC, System.currentTimeMillis()+FIVE_SECONDS,
                    createPendingIntent());
            }
        });

        Button unschedule = (Button)findViewById(R.id.unschedule);
        unschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);
                am.cancel(createPendingIntent());
            }
        });
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent("static_receiver");
        intent.putExtra(AlarmReceiver.MSG, "Remember to try out the alarm examples!");
        return PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
