package com.packt.androidconcurrency.chapter2.example5;

public interface AsyncListener<Progress, Result> {
    void onPreExecute();
    void onProgressUpdate(Progress... progress);
    void onPostExecute(Result result);
    void onCancelled(Result result);
}
