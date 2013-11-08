package com.packt.androidconcurrency.chapter6.example1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;

public class PrimesActivity extends Activity {

    private static PrimesHandler handler = new PrimesHandler();
    private static Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch6_example1_layout);

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
    }

    private void triggerService(int primeToFind) {
        Intent intent = new Intent(this, PrimesService.class);
        intent.putExtra(PrimesService.PARAM, primeToFind);
        intent.putExtra(PrimesService.MESSENGER, messenger);
        startService(intent);
    }

    private static class PrimesHandler extends Handler {
        private LinearLayout view;

        @Override
        public void handleMessage(Message message) {
            if (message.what == PrimesService.RESULT) {
                if (view != null) {
                    TextView text = new TextView(view.getContext());
                    text.setText(message.arg1 + "th prime: " + message.obj.toString());
                    view.addView(text);
                } else {
                    Log.i(LaunchActivity.TAG, "received a result, ignoring because we're detached");
                }
            } else if (message.what == PrimesService.INVALID) {
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
