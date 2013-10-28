package com.packt.androidconcurrency.chapter5.example1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.R;

public class PrimesActivity extends Activity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch5_example1_layout);

        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView input = (TextView)findViewById(R.id.prime_to_find);
                String value = input.getText().toString();
                if (value.matches("[1-9]+[0-9]*")) {
                    // if the value is a number, trigger the loader to
                    // reload when appropriate.
                    triggerIntentService(Integer.parseInt(value));
                } else {
                    Toast.makeText(PrimesActivity.this, "not a number!", 5000).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new NthPrimeReceiver(new Handler());
        IntentFilter filter = new IntentFilter(
            PrimesIntentService.PRIMES_BROADCAST);
        LocalBroadcastManager.getInstance(this).
            registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).
            unregisterReceiver(receiver);
    }

    private void triggerIntentService(int primeToFind) {
        Intent intent = new Intent(this, PrimesIntentService.class);
        intent.putExtra(PrimesIntentService.PARAM, primeToFind);
        startService(intent);
    }

    private class NthPrimeReceiver extends BroadcastReceiver {
        private Handler handler;
        public NthPrimeReceiver(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(
                PrimesIntentService.RESULT);
            intent.putExtra(PrimesIntentService.HANDLED, true);
            final String msg = String.format(
                "The result is %s", result);
            handler.post(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(PrimesActivity.this, msg, 5000).show();
                }
            });
        }
    }
}
