package com.packt.androidconcurrency.chapter3.example1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;

/**
 * Illustrates creating a Handler attached to the main thread's Looper,
 * and posting Runnable's to the Handler for the main thread to execute.
 */
public class ExplicitHandlerPrimesActivity extends Activity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch3_example1_layout);

        // create a Handler bound to the main thread
        handler = new Handler();

        final TextView resultView = (TextView) findViewById(R.id.result);

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // find the nth probable prime in the
                // background using a simple thread
                resultView.setText(R.string.calculating);
                new PrimeCalculator(500, resultView, handler).start();
            }
        });
    }

    private static class PrimeCalculator extends Thread {

        private int primeToFind;
        private TextView resultView;
        private Handler handler;

        public PrimeCalculator(int primeToFind, TextView resultView, Handler handler) {
            this.handler = handler;
            this.resultView = resultView;
            this.primeToFind = primeToFind;
            // make sure to reduce the thread priority so we
            // don't starve the main thread!
            setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run() {
            BigInteger prime = new BigInteger("2");
            for (int i=0; i<primeToFind; i++) {
                prime = prime.nextProbablePrime();
            }
            postResultToMainThread(prime);
        }

        private void postResultToMainThread(final BigInteger result) {
            handler.post(new Runnable(){
                @Override
                public void run() {
                    resultView.setText(result.toString());
                }
            });
        }
    }
}
