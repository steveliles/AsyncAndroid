package com.packt.androidconcurrency.chapter7.example3;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.packt.androidconcurrency.R;

public class AlarmSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch7_example3_layout);

        Button go = (Button) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.prime_to_find);
                String value = input.getText().toString();
                if (value.matches("[1-9]+[0-9]*")) {
                    Intent intent = new Intent("primes_async_receiver");
                    intent.putExtra(
                        AsyncBroadcastReceiver.PRIME_TO_FIND, Integer.parseInt(value));
                    final PendingIntent pending = PendingIntent.getBroadcast(
                        AlarmSettingActivity.this,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager am = (AlarmManager)
                            getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC, System.currentTimeMillis()+30000L, pending);
                }
            }
        });
    }

}
