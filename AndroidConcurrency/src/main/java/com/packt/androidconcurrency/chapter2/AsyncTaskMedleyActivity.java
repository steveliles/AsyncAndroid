package com.packt.androidconcurrency.chapter2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;

/**
 * Collects together all of the AsyncTask examples in one
 * Activity for easy comparison.
 */
public class AsyncTaskMedleyActivity extends Activity {

    private static final long TIMEOUT = 3000L;

    private TextView resultView;
    private AsyncTask<Void,Integer,BigInteger> cancellableTask;
    private ProgressDialog progress;
    private boolean neverAgainVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch2_medley_layout);

        resultView = (TextView) findViewById(R.id.result);

        Button doOnMainThreadButton = (Button) findViewById(R.id.do_on_main_thread);
        doOnMainThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // execute findHighestProbablyPrime directly in the callback,
                // which means it will run on the main thread
                PrimeCalculator primes = new PrimeCalculator(TIMEOUT);
                BigInteger prime = primes.findHighestProbablePrime();
                updateUserInterfaceAfterCompletion(prime);
            }
        });

        Button doInBackgroundButton = (Button) findViewById(R.id.do_in_background);
        doInBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calculate the highest probable prime in the background using AsyncTask
                newDoInBackgroundTask().execute();
            }
        });

        Button clearThenDoInBackgroundButton = (Button) findViewById(R.id.clear_then_do_in_background);
        clearThenDoInBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use onPreExecute to update the user-interface to show that we're
                // about to do something, then do processing in the background
                newDoInBackgroundWithFeedbackTask().execute();
            }
        });

        Button doInBackgroundWithProgressButton = (Button) findViewById(R.id.do_in_background_with_progress);
        doInBackgroundWithProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use onPreExecute to update the user-interface to show that we're
                // about to do something, do the processing in the background while
                // publishing progress updates to show that work is progressing
                newDoInBackgroundWithProgressTask().execute();
            }
        });

        Button doInBackgroundWithCancelButton = (Button) findViewById(R.id.do_in_background_with_cancel);
        doInBackgroundWithCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use onPreExecute to update the user-interface to show that we're
                // about to do something, do processing in the background while
                // publishing progress updates to show that work is progressing, and
                // allow cancellation to terminate processing early
                cancellableTask = newDoInBackgroundWithCancelTask();
                cancellableTask.execute();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        progress = null;

        if (Build.VERSION.SDK_INT >= 11) {
            neverAgainVisible = isChangingConfigurations() || isFinishing();
        } else {
            neverAgainVisible = isFinishing();
        }

        if (cancellableTask != null) {
            cancellableTask.cancel(false);
            cancellableTask = null;
        }
    }

    protected void cancel() {
        if (cancellableTask != null)
            cancellableTask.cancel(false);
    }

    private void updateUserInterfaceAfterCompletion(BigInteger result) {
        maybeLogUselessUIUpdateAndMemoryLeak();
        resultView.setText(result.toString());
        dismissProgress();
    }

    private void updateUserInterfaceAfterCancellation(BigInteger result) {
        maybeLogUselessUIUpdateAndMemoryLeak();
        resultView.setText("cancelled at " + result.toString());
        dismissProgress();
    }

    private void showWorkStarting(boolean progressable, boolean cancellable) {
        maybeLogUselessUIUpdateAndMemoryLeak();
        if (progress == null)
            progress = showProgressDialog(progressable, cancellable);
    }

    private void showProgress(int percent) {
        maybeLogUselessUIUpdateAndMemoryLeak();
        if (progress != null)
            progress.setProgress(percent);
    }

    private void maybeLogUselessUIUpdateAndMemoryLeak() {
        if (neverAgainVisible)
            Log.w("androidconcurrency", "useless view update - wasting battery-life and memory leaked til this task finishes!");
    }

    private ProgressDialog showProgressDialog(boolean determinate, boolean cancellable) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.calculating);
        if (determinate)
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (cancellable) {
            dialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncTaskMedleyActivity.this.cancel();
                    }
                });
        }
        dialog.show();
        return dialog;
    }

    private void dismissProgress() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    private AsyncTask<Void, Void, BigInteger> newDoInBackgroundTask() {
        return new AsyncTask<Void, Void, BigInteger>() {
            @Override
            protected BigInteger doInBackground(Void... params) {
                PrimeCalculator primes = new PrimeCalculator(TIMEOUT);
                return primes.findHighestProbablePrime();
            }

            @Override
            protected void onPostExecute(BigInteger result) {
                updateUserInterfaceAfterCompletion(result);
            }
        };
    }

    private AsyncTask<Void, Void, BigInteger> newDoInBackgroundWithFeedbackTask() {
        return new AsyncTask<Void, Void, BigInteger>() {
            @Override
            protected void onPreExecute() {
                showWorkStarting(false, false);
            }

            @Override
            protected BigInteger doInBackground(Void... params) {
                PrimeCalculator primes = new PrimeCalculator(TIMEOUT);
                return primes.findHighestProbablePrime();
            }

            @Override
            protected void onPostExecute(BigInteger result) {
                updateUserInterfaceAfterCompletion(result);
            }
        };
    }

    private AsyncTask<Void, Integer, BigInteger> newDoInBackgroundWithProgressTask() {
        return new AsyncTask<Void, Integer, BigInteger>() {
            @Override
            protected void onPreExecute() {
                showWorkStarting(true, false);
                showProgress(0);
            }

            @Override
            protected BigInteger doInBackground(Void... params) {
                PrimeCalculator primes = new PrimeCalculator(TIMEOUT);
                return primes.findHighestProbablePrime(
                    new PrimeCalculator.ProgressListener() {
                        private int progress;
                        public void onProgress(int update) {
                            if (update > progress) {
                                publishProgress(update);
                            }
                            progress = update;
                        }
                    }
                );
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                showProgress(values[0]);
            }

            @Override
            protected void onPostExecute(BigInteger result) {
                updateUserInterfaceAfterCompletion(result);
            }
        };
    }

    private AsyncTask<Void, Integer, BigInteger> newDoInBackgroundWithCancelTask() {
        return new AsyncTask<Void, Integer, BigInteger>() {
            @Override
            protected void onPreExecute() {
                showWorkStarting(true, true);
                showProgress(0);
            }

            @Override
            protected BigInteger doInBackground(Void... params) {
                PrimeCalculator primes = new PrimeCalculator(TIMEOUT);
                return primes.findHighestProbablePrime(
                    new PrimeCalculator.ProgressListener() {
                        private int progress;
                        public void onProgress(int update) {
                            if ((update > progress) && !isCancelled()) {
                                publishProgress(update);
                            }
                            progress = update;
                        }
                    },
                    new PrimeCalculator.Status() {
                        public boolean isCalculationCancelled() {
                            return isCancelled();
                        }
                    }
                );
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                showProgress(values[0]);
            }

            @Override
            protected void onPostExecute(BigInteger result) {
                // only runs if the task is NOT cancelled before it completes
                updateUserInterfaceAfterCompletion(result);
            }

            @Override
            protected void onCancelled(BigInteger result) {
                // only runs if the task is cancelled before it completes,
                // and if the platform is running API-level 11 or greater
                updateUserInterfaceAfterCancellation(result);
            }

            @Override
            protected void onCancelled() {
                // only runs if the task is cancelled before it completes,
                // and if the platform is running API-level 10 or below
                updateUserInterfaceAfterCancellation(BigInteger.ZERO);
            }
        };
    }
}
