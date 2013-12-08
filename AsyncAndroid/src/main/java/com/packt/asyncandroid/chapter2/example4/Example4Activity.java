package com.packt.asyncandroid.chapter2.example4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.asyncandroid.R;

public class Example4Activity extends Activity {

    private PrimesTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch2_example_layout);
        ((TextView)findViewById(R.id.title)).setText(R.string.ch2_ex4);
        ((TextView)findViewById(R.id.description)).setText(R.string.ch2_ex4_desc);

        Button goButton = (Button) findViewById(R.id.go);
        final TextView resultView = (TextView) findViewById(R.id.result);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = new PrimesTask(Example4Activity.this, resultView);
                task.execute(500);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (task != null)
            task.cancel(false);
    }
}
