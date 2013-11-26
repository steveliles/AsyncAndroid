package com.packt.asyncandroid;

import android.os.AsyncTask;
import android.util.Log;

public abstract class SafeAsyncTask<R>
extends AsyncTask<Void, Void, SafeAsyncTask.Result<R>> {

    static class Result<T> {
        private T result;
        private Exception exc;
    }

    protected abstract R doSafelyInBackground(Void... params) throws Exception;

    @Override
    protected final Result<R> doInBackground(Void... params) {
        Result<R> result = new Result<R>();
        try {
            result.result = doSafelyInBackground(params);
        } catch (Exception exc) {
            result.exc = exc;
        }
        return result;
    }

    @Override
    protected final void onPostExecute(Result<R> result) {
        if (result.exc != null) {
            onCompletedWithException(result.exc);
        } else {
            onCompletedWithResult(result.result);
        }
    }

    @Override
    protected final void onCancelled(Result<R> result) {
        onCancelledWithResult(result.result);
    }

    // override me if you want to handle exceptions
    protected void onCompletedWithException(Exception exc) {
        Log.e(LaunchActivity.TAG, exc.getMessage(), exc);
    }

    // override me if you want to handle the result
    protected void onCompletedWithResult(R result) {
    }

    // override me if you to handle the partial result
    // but _don't_ call super if you override
    protected void onCancelledWithResult(R result) {
        onCancelled();
    }
}
