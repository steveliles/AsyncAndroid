package com.packt.androidconcurrency.chapter3.example2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.androidconcurrency.R;

/**
 * Illustrates creating a Handler attached to the main thread's Looper,
 * and posting delayed Runnable's to the Handler for the main thread to
 * execute in the future.
 *
 * Notice that we do NOT create any background threads in this activity -
 * everything runs on the main thread, and yet we do not risk ANR's and
 * the user-interface remains responsive - this is the beauty of Handler
 * (well, android.os.Looper, really) in a single-threaded environment.
 */
public class PostDelayedActivity extends Activity {

    private Handler handler;
    private Runnable tada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch3_example2_layout);

        // create a Handler bound to the main thread
        handler = new Handler();

        final TextView resultView = (TextView) findViewById(R.id.result);

        // create an instance of the runnable and keep a reference to it
        // so that we can post it for execution but also so that we can
        // cancel it if necessary
        tada = new TadaRunnable(resultView);

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove existing runnable if there
                // is one pending
                if (tada != null)
                    handler.removeCallbacks(tada);

                resultView.setText(R.string.wait);

                // request the main thread to execute our
                // runnable in 3 seconds time
                handler.postDelayed(tada, 3000);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(tada);
                resultView.setText(R.string.cancelled);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // prevent short-term memory leak by removing
        // pending tasks
        if (tada != null) {
            handler.removeCallbacks(tada);
        }
    }

    private static class TadaRunnable implements Runnable {
        private TextView resultView;

        public TadaRunnable(TextView resultView) {
            this.resultView = resultView;
        }

        @Override
        public void run() {
            // we'll be posting this runnable onto the main thread's
            // queue, so it is fine to update the user-interface here.
            resultView.setText(R.string.tada);
        }
    }
}
