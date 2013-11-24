package com.packt.asyncandroid.chapter7.example6;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.asyncandroid.LaunchActivity;
import com.packt.asyncandroid.R;

/**
 * Starts an Activity from an Alarm.
 */
public class SetActivityAlarmActivity extends Activity {

    private static final String ALARMED = "alarmed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example6_layout);

        TextView v = (TextView) findViewById(R.id.started_by_alarm);
        if (getIntent().getBooleanExtra(ALARMED, false)) {
            v.setText(getString(R.string.was_started_by_alarm));
        } else {
            v.setText(getString(R.string.was_not_started_by_alarm));
        }

        Button getActivity = (Button) findViewById(R.id.get_activity);
        getActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                    SetActivityAlarmActivity.this,
                    SetActivityAlarmActivity.class);
                intent.putExtra(ALARMED, true);

                final PendingIntent pending = PendingIntent.getActivity(
                    SetActivityAlarmActivity.this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= 19) {
                    am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000L, pending);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000L, pending);
                }
            }
        });

        Button getActivities = (Button) findViewById(R.id.get_activities);
        getActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent first = new Intent(
                    SetActivityAlarmActivity.this,
                    LaunchActivity.class);
                first.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                Intent second = new Intent(
                    SetActivityAlarmActivity.this,
                    SetActivityAlarmActivity.class);
                second.putExtra(ALARMED, true);

                final PendingIntent pending = PendingIntent.getActivities(
                    SetActivityAlarmActivity.this, 0,
                    new Intent[]{first, second},
                    PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager)
                    getSystemService(ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= 19) {
                    am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000L, pending);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000L, pending);
                }
            }
        });
    }
}
