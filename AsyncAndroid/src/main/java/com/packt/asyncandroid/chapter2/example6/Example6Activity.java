package com.packt.asyncandroid.chapter2.example6;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.asyncandroid.R;
import com.packt.asyncandroid.SafeAsyncTask;

import java.math.BigInteger;
import java.util.Random;

public class Example6Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch2_example6_layout);

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView resultView = (TextView)findViewById(R.id.result);
                new ExceptionalPrimesTask(resultView).execute();
            }
        });
    }

    private static class ExceptionalPrimesTask extends SafeAsyncTask<BigInteger> {
        private static Random random = new Random();

        private TextView resultView;

        public ExceptionalPrimesTask(TextView resultView) {
            this.resultView = resultView;
        }

        @Override
        protected BigInteger doSafelyInBackground(Void... params) throws Exception {
            if (random.nextBoolean()) {
                throw new RuntimeException("bang");
            } else {
                return new BigInteger("12345");
            }
        }

        @Override
        protected void onCompletedWithResult(BigInteger result) {
            resultView.setText(result.toString());
        }

        @Override
        protected void onCompletedWithException(Exception exc) {
            resultView.setText(exc.getMessage());
        }
    }
}
