package com.packt.androidconcurrency.chapter6.example2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.chapter6.example1.PrimesAsyncTaskIntentService;

public class PrimesActivity extends Activity implements ServiceConnection {

    private static PrimesHandler handler = new PrimesHandler();
    private static Messenger me = new Messenger(handler);

    private Messenger service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, PrimesAsyncTaskMessengerService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.ch6_example2_layout);

        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView input = (TextView) findViewById(R.id.prime_to_find);
                String[] values = input.getText().toString().split(",");
                for (String value : values) {
                    if (value.trim().matches("[1-9]+[0-9]*")) {
                        triggerService(Integer.parseInt(value.trim()));
                    } else {
                        Toast.makeText(PrimesActivity.this, "not a number!", 5000).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.attach((LinearLayout)findViewById(R.id.result));
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.detach();

        if (isFinishing())
            unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    private void triggerService(Integer primeToFind) {
        try {
            Message msg = Message.obtain();
            msg.what = PrimesAsyncTaskMessengerService.FIND_PRIME;
            msg.obj = primeToFind;
            msg.replyTo = me;
            service.send(msg);
        } catch (RemoteException exc) {
            Log.e(LaunchActivity.TAG, "unable to send message to service", exc);
        }
    }

    public static class PrimesHandler extends Handler {
        private LinearLayout view;

        @Override
        public void handleMessage(Message message) {
            if (message.what == PrimesAsyncTaskIntentService.RESULT) {
                if (view != null) {
                    TextView text = new TextView(view.getContext());
                    text.setText(message.arg1 + "th prime: " + message.obj.toString());
                    view.addView(text);
                } else {
                    Log.i(LaunchActivity.TAG, "received a result, ignoring because we're detached");
                }
            } else if (message.what == PrimesAsyncTaskIntentService.INVALID) {
                if (view != null) {
                    TextView text = new TextView(view.getContext());
                    text.setText("Invalid request");
                    view.addView(text);
                }
            } else {
                super.handleMessage(message);
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
