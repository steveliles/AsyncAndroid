package com.packt.asyncandroid.chapter5.example4;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.asyncandroid.R;

public class BroadcastReceivingPrimesActivity extends Activity {

    private static final int RESULT_MSG = "result".hashCode();

    private static PrimesHandler handler = new PrimesHandler();
    private static BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch5_example3_layout);

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
                    Toast.makeText(BroadcastReceivingPrimesActivity.this, "not a number!", 5000).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.attach((TextView)findViewById(R.id.result));

        receiver = new NthPrimeReceiver(handler);
        IntentFilter filter = new IntentFilter(
            BroadcastingPrimesIntentService.PRIMES_BROADCAST);
        LocalBroadcastManager.getInstance(this).
            registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).
            unregisterReceiver(receiver);

        handler.detach();
    }

    private void triggerIntentService(int primeToFind) {
        Intent intent = new Intent(this, BroadcastingPrimesIntentService.class);
        intent.putExtra(BroadcastingPrimesIntentService.PARAM, primeToFind);
        startService(intent);
    }

    private static class NthPrimeReceiver extends BroadcastReceiver {
        private Handler handler;
        public NthPrimeReceiver(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(
                BroadcastingPrimesIntentService.RESULT);

            intent.putExtra(BroadcastingPrimesIntentService.HANDLED, true);
            handler.sendMessage(Message.obtain(handler, RESULT_MSG, result));
        }
    }

    private static class PrimesHandler extends Handler {
        private TextView view;

        @Override
        public void handleMessage(Message message) {
            if (message.what == RESULT_MSG) {
                if (view != null)
                    view.setText(message.obj.toString());
            } else {
                super.handleMessage(message);
            }
        }

        public void attach(TextView view) {
            this.view = view;
        }

        public void detach() {
            this.view = null;
        }
    }
}
