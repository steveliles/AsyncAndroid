package com.packt.androidconcurrency.chapter6.example4;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;

public class BroadcastReceivingPrimesActivity extends Activity {

    private static NthPrimeReceiver receiver = new NthPrimeReceiver();

    private BroadcastingPrimesService service;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch6_example1_layout);

        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (service == null) {
                    Toast.makeText(BroadcastReceivingPrimesActivity.this, "service not bound", 5000).show();
                } else {
                    TextView input = (TextView) findViewById(R.id.prime_to_find);
                    String[] values = input.getText().toString().split(",");
                    for (String value : values) {
                        if (value.trim().matches("[1-9]+[0-9]*")) {
                            service.calculateNthPrime(Integer.parseInt(value));
                        } else {
                            Toast.makeText(BroadcastReceivingPrimesActivity.this, "not a number!", 5000).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver.attach((LinearLayout)findViewById(R.id.results));

        IntentFilter filter = new IntentFilter(
            BroadcastingPrimesService.PRIMES_BROADCAST);
        LocalBroadcastManager.getInstance(this).
            registerReceiver(receiver, filter);

        bindService(
            new Intent(this, BroadcastingPrimesService.class),
            connection = new Connection(),
            Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        service = null;
        unbindService(connection);

        LocalBroadcastManager.getInstance(this).
            unregisterReceiver(receiver);

        receiver.detach();
    }

    private class Connection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((BroadcastingPrimesService.Access)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }

    private static class NthPrimeReceiver extends BroadcastReceiver {
        private LinearLayout view;

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(
                BroadcastingPrimesService.RESULT);
            if (view != null) {
                TextView text = new TextView(view.getContext());
                text.setText(result);
                view.addView(text);
            } else {
                Log.i(LaunchActivity.TAG, "received a result, ignoring because we're detached");
            }
        }

        public void attach(LinearLayout view) {
            this.view = view;
        }

        public void detach() {
            this.view = null;
        }
    }
}
