package com.packt.asyncandroid.chapter1.example2;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packt.asyncandroid.R;

public class ChoreographerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch1_example2_layout);

        ViewGroup root = (ViewGroup) findViewById(R.id.root);

        root.addView(new TextView(this){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                long sleep = (long)(Math.random() * 1000L);
                setText("" + sleep);
                try {
                    Thread.sleep(sleep);
                } catch (Exception exc) {}
            }
        });
    }

}
