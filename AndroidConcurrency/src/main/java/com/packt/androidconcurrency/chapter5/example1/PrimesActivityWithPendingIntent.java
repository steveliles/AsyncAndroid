package com.packt.androidconcurrency.chapter5.example1;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.R;

public class PrimesActivityWithPendingIntent extends Activity{

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
                    Toast.makeText(PrimesActivityWithPendingIntent.this, "not a number!", 5000).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PrimesIntentServiceWithPendingIntent.RESULT_CODE) {
            String result = data.getStringExtra(
                PrimesIntentServiceWithPendingIntent.RESULT);
            TextView view = (TextView)findViewById(R.id.result);
            view.setText(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void triggerIntentService(int primeToFind) {
        PendingIntent pendingResult = createPendingResult(
            PrimesIntentServiceWithPendingIntent.RESULT_CODE,
            new Intent(), 0);
        Intent intent = new Intent(this, PrimesIntentServiceWithPendingIntent.class);
        intent.putExtra(PrimesIntentServiceWithPendingIntent.PARAM, primeToFind);
        intent.putExtra(PrimesIntentServiceWithPendingIntent.PENDING_RESULT, pendingResult);
        startService(intent);
    }
}
