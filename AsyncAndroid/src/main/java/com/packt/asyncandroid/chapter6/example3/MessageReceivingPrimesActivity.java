package com.packt.asyncandroid.chapter6.example3;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.asyncandroid.LaunchActivity;
import com.packt.asyncandroid.R;

public class MessageReceivingPrimesActivity extends Activity {

    private static PrimesHandler handler = new PrimesHandler();
    private static Messenger messenger = new Messenger(handler);

    private MessageSendingPrimesService service;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch6_example_layout);
        ((TextView)findViewById(R.id.title)).setText(R.string.ch6_ex3);
        ((TextView)findViewById(R.id.description)).setText(R.string.ch6_ex3_desc);

        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (service == null) {
                    Toast.makeText(MessageReceivingPrimesActivity.this, "service not bound", 5000).show();
                } else {
                    TextView input = (TextView) findViewById(R.id.prime_to_find);
                    String[] values = input.getText().toString().split(",|\\s");
                    for (String value : values) {
                        if (value.trim().matches("[1-9]+[0-9]*")) {
                            service.calculateNthPrime(Integer.parseInt(value), messenger);
                        } else {
                            Toast.makeText(MessageReceivingPrimesActivity.this, "not a number!", 5000).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.attach((LinearLayout)findViewById(R.id.results));
        bindService(
                new Intent(this, MessageSendingPrimesService.class),
                connection = new Connection(),
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        service = null;
        unbindService(connection);
        handler.detach();
    }

    private class Connection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MessageSendingPrimesService.Access)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }

    private static class PrimesHandler extends Handler {
        private LinearLayout view;

        @Override
        public void handleMessage(Message message) {
            if (message.what == MessageSendingPrimesService.RESULT) {
                if (view != null) {
                    TextView text = new TextView(view.getContext());
                    text.setText(message.obj.toString());
                    view.addView(text);
                } else {
                    Log.i(LaunchActivity.TAG, "received a result, ignoring because we're detached");
                }
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
