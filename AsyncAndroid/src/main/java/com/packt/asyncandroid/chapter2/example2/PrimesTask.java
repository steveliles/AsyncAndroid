package com.packt.asyncandroid.chapter2.example2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.math.BigInteger;

import com.packt.asyncandroid.R;

public class PrimesTask extends AsyncTask<Integer, Void, BigInteger> {
    private Context ctx;
    private ProgressDialog progress;
    private TextView resultView;

    public PrimesTask(Context ctx, TextView resultView) {
        this.ctx = ctx;
        this.resultView = resultView;
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(ctx);
        progress.setTitle(R.string.calculating);
        progress.setCancelable(false);
        progress.show();
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
        progress.dismiss();
    }
}
