package com.packt.androidconcurrency.chapter1.example1;

import android.app.Activity;
import android.os.Bundle;

/**
 * DELIBERATELY trigger's an Application Not Responding dialog,
 * thereby crashing the containing app.
 */
public class ANRActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException anExc) {
            anExc.printStackTrace();
        }
    }
}
