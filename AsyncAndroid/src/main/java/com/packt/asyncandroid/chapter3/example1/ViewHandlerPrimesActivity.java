package com.packt.asyncandroid.chapter3.example1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.asyncandroid.R;

import java.math.BigInteger;

/**
 * Illustrates using the post(Runnable) method of objects extending from
 * android.view.View, posting Runnable's to the view for the main thread
 * to execute.
 */
public class ViewHandlerPrimesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch3_example1a_layout);

        final TextView resultView = (TextView) findViewById(R.id.result);

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // find the nth probable prime in the
                // background using a simple thread
                resultView.setText(R.string.calculating);
                new PrimeCalculator(500, resultView).start();
            }
        });
    }

    private static class PrimeCalculator extends Thread {

        private int primeToFind;
        private TextView resultView;

        public PrimeCalculator(int primeToFind, TextView resultView) {
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
            resultView.post(new Runnable(){
                @Override
                public void run() {
                    resultView.setText(result.toString());
                }
            });
        }
    }
}
