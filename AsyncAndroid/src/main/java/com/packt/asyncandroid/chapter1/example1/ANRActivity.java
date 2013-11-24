package com.packt.asyncandroid.chapter1.example1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.packt.asyncandroid.R;

/**
 * DELIBERATELY trigger's an Application Not Responding dialog,
 * thereby crashing the containing app.
 */
public class ANRActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch1_example1_layout);

        makeEvil((Button)findViewById(R.id.button1));
        makeEvil((Button)findViewById(R.id.button2));
        makeEvil((Button)findViewById(R.id.button3));
    }

    private void makeEvil(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(6000L);
                } catch (InterruptedException anExc) {
                    anExc.printStackTrace();
                }
            }
        });
    }
}
