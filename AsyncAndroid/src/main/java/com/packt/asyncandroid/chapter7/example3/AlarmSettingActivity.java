package com.packt.asyncandroid.chapter7.example3;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.packt.asyncandroid.R;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlarmSettingActivity extends Activity {

    private static final long THIRTY_SECONDS = TimeUnit.SECONDS.toMillis(30);

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

                    Calendar calendar = Calendar.getInstance();
                    if (calendar.get(Calendar.HOUR_OF_DAY) >= 13) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, 13);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    System.out.println(calendar.getTime());

                    am.set(AlarmManager.RTC, System.currentTimeMillis()+THIRTY_SECONDS, pending);
                }
            }
        });
    }

}
