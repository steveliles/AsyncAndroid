package com.packt.androidconcurrency.chapter2.example1;

import android.os.AsyncTask;
import android.widget.TextView;

import java.math.BigInteger;

public class PrimesTask extends AsyncTask<Integer, Void, BigInteger> {
    private TextView resultView;

    public PrimesTask(TextView resultView) {
        this.resultView = resultView;
    }

    @Override
    protected BigInteger doInBackground(Integer... params) {
        int primeToFind = params[0];
        BigInteger prime = new BigInteger("2");
        for (int i=0; i<primeToFind; i++) {
            prime = prime.nextProbablePrime();
        }
        return prime;
    }

    @Override
    protected void onPostExecute(BigInteger result) {
        resultView.setText(result.toString());
    }
}
