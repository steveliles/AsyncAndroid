package com.packt.asyncandroid.chapter6.example2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.asyncandroid.R;

import java.math.BigInteger;

public class LocalPrimesActivity extends Activity
implements LocalPrimesService.Callback {

    private LocalPrimesService service;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch6_example_layout);
        ((TextView)findViewById(R.id.title)).setText(R.string.ch6_ex2);
        ((TextView)findViewById(R.id.description)).setText(R.string.ch6_ex2_desc);

        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (service == null) {
                    Toast.makeText(LocalPrimesActivity.this, "service not bound", 5000).show();
                } else {
                    TextView input = (TextView) findViewById(R.id.prime_to_find);
                    String[] values = input.getText().toString().split(",|\\s");
                    for (String value : values) {
                        if (value.trim().matches("[1-9]+[0-9]*")) {
                            service.calculateNthPrime(Integer.parseInt(value), LocalPrimesActivity.this);
                        } else {
                            Toast.makeText(LocalPrimesActivity.this, "not a number!", 5000).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onResult(BigInteger result) {
        if (service == null) {
            return false;
        } else {
            LinearLayout results = (LinearLayout)findViewById(R.id.results);
            TextView view = new TextView(this);
            view.setText(result.toString());
            results.addView(view);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(
            new Intent(this, LocalPrimesService.class),
            connection = new Connection(),
            Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        service = null;
        unbindService(connection);
    }

    private class Connection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((LocalPrimesService.Access)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }
}
