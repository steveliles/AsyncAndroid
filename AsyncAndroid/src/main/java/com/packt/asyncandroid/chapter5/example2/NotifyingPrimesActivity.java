package com.packt.asyncandroid.chapter5.example2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.asyncandroid.R;
import com.packt.asyncandroid.chapter5.example4.BroadcastingPrimesIntentService;

/**
 * This activity starts the IntentService but doesn't expect to
 * receive a result, so its nice and simple. The result will be
 * posted as a system-notification by the IntentService itself.
 */
public class NotifyingPrimesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch5_example_layout);
        ((TextView)findViewById(R.id.title)).setText(R.string.ch5_ex2);
        ((TextView)findViewById(R.id.description)).setText(R.string.ch5_ex2_desc);

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
                    Toast.makeText(NotifyingPrimesActivity.this, "not a number!", 5000).show();
                }
            }
        });
    }

    private void triggerIntentService(int primeToFind) {
        Intent intent = new Intent(this, BroadcastingPrimesIntentService.class);
        intent.putExtra(BroadcastingPrimesIntentService.PARAM, primeToFind);
        startService(intent);
    }
}
