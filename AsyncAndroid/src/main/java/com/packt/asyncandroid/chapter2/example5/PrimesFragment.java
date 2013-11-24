package com.packt.asyncandroid.chapter2.example5;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.math.BigInteger;

public class PrimesFragment extends Fragment {

    private AsyncListener<Integer,BigInteger> listener;
    private PrimesTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        task = new PrimesTask();
        task.execute(2000);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (AsyncListener<Integer,BigInteger>)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void cancel() {
        task.cancel(false);
    }

    class PrimesTask extends AsyncTask<Integer, Integer, BigInteger> {
        @Override
        protected void onPreExecute() {
            if (listener != null) listener.onPreExecute();
        }

        @Override
        protected BigInteger doInBackground(Integer... params) {
            int primeToFind = params[0];
            BigInteger prime = new BigInteger("2");
            for (int i=0; i<primeToFind; i++) {
                prime = prime.nextProbablePrime();

                int percentComplete = (int)((i * 100f)/primeToFind);
                publishProgress(percentComplete);

                if (isCancelled())
                    break;
            }
            return prime;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (listener != null) listener.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(BigInteger result) {
            if (listener != null) listener.onPostExecute(result);
        }

        @Override
        protected void onCancelled(BigInteger result) {
            if (listener != null) listener.onCancelled(result);
        }
    }
}
