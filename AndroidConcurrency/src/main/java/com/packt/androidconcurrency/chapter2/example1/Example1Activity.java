package com.packt.androidconcurrency.chapter2.example1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.androidconcurrency.R;

public class Example1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch2_example_layout);

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // find the nth probable prime in the background using AsyncTask
                new PrimesTask((TextView) findViewById(R.id.result)).execute(500);
            }
        });
    }
}
