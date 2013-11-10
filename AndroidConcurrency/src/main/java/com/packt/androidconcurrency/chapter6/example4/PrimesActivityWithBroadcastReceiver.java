package com.packt.androidconcurrency.chapter6.example4;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;

public class PrimesActivityWithBroadcastReceiver extends Activity {

    private static final int RESULT_MSG = "result".hashCode();

    private static PrimesHandler handler = new PrimesHandler();
    private static BroadcastReceiver receiver;

    private PrimesServiceWithBroadcast service;
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
                    Toast.makeText(PrimesActivityWithBroadcastReceiver.this, "service not bound", 5000).show();
                } else {
                    TextView input = (TextView) findViewById(R.id.prime_to_find);
                    String[] values = input.getText().toString().split(",");
                    for (String value : values) {
                        if (value.trim().matches("[1-9]+[0-9]*")) {
                            service.calculateNthPrime(Integer.parseInt(value));
                        } else {
                            Toast.makeText(PrimesActivityWithBroadcastReceiver.this, "not a number!", 5000).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.attach((LinearLayout)findViewById(R.id.results));

        receiver = new NthPrimeReceiver(handler);
        IntentFilter filter = new IntentFilter(
            PrimesServiceWithBroadcast.PRIMES_BROADCAST);
        LocalBroadcastManager.getInstance(this).
            registerReceiver(receiver, filter);

        bindService(
            new Intent(this, PrimesServiceWithBroadcast.class),
            connection = new Connection(),
            Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        service = null;
        unbindService(connection);

        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(receiver);

        handler.detach();
    }

    private class Connection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((PrimesServiceWithBroadcast.Access)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }

    private static class NthPrimeReceiver extends BroadcastReceiver {
        private Handler handler;
        public NthPrimeReceiver(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(
                PrimesServiceWithBroadcast.RESULT);
            intent.putExtra(PrimesServiceWithBroadcast.HANDLED, true);
            handler.sendMessage(Message.obtain(handler, RESULT_MSG, result));
        }
    }

    private static class PrimesHandler extends Handler {
        private LinearLayout view;

        @Override
        public void handleMessage(Message message) {
            if (view != null) {
                TextView text = new TextView(view.getContext());
                text.setText(message.obj.toString());
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
