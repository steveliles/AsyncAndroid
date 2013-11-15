package com.packt.androidconcurrency.chapter7.example4;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.chapter5.example4.BroadcastingPrimesIntentService;

/**
 * Sets an Alarm to trigger the PrimesIntentService that we built
 * in chapter 5. If the device is awake there's no real problem
 * with this - the service will be invoked and run to completion,
 * but we cannot rely on that behaviour if the device is not awake
 * when the alarm is triggered. The device will want to sleep again...
 */
public class SetAlarmToTriggerServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example4_layout);

        Button go = (Button) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.prime_to_find);
                String value = input.getText().toString();
                if (value.matches("[1-9]+[0-9]*")) {
                    Intent intent = new Intent(
                        SetAlarmToTriggerServiceActivity.this,
                        BroadcastingPrimesIntentService.class);
                    intent.putExtra(
                        BroadcastingPrimesIntentService.PARAM,
                        Integer.parseInt(value));

                    final PendingIntent pending = PendingIntent.getService(
                        SetAlarmToTriggerServiceActivity.this,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager am = (AlarmManager)
                        getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC, System.currentTimeMillis() + 30000L, pending);
                }
            }
        });
    }
}
