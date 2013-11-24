package com.packt.asyncandroid.chapter2;

import java.math.BigInteger;

public class PrimeCalculator {

    interface ProgressListener {
        public void onProgress(int percent);
    }

    interface Status {
        public boolean isCalculationCancelled();
    }

    private ProgressMeter meter;

    public PrimeCalculator(long timeoutMillis) {
        meter = new ProgressMeter(timeoutMillis);
    }

    public BigInteger findHighestProbablePrime() {
        return findHighestProbablePrime(new NoOpProgressListener());
    }

    public BigInteger findHighestProbablePrime(ProgressListener listener) {
        return findHighestProbablePrime(listener, new UnknownCancellationStatus());
    }

    public BigInteger findHighestProbablePrime(ProgressListener listener, Status status) {
        meter.start();
        BigInteger prime = new BigInteger("2");

        while(!meter.isComplete() && !status.isCalculationCancelled()) {
            prime = prime.nextProbablePrime();
            listener.onProgress(meter.calculateProgress());
        }

        return prime;
    }

    private static class ProgressMeter
    {
        long startTime, onePercent;
        int percentComplete;

        public ProgressMeter(long timeoutMillis) {
            onePercent = timeoutMillis / 100L;
            percentComplete = 0;
        }

        public void start() {
            startTime = System.currentTimeMillis();
        }

        public int calculateProgress() {
            long now = System.currentTimeMillis();
            return percentComplete = (int)((now - startTime) / onePercent);
        }

        public boolean isComplete() {
            return percentComplete >= 100;
        }
    }

    private static class NoOpProgressListener implements ProgressListener {
        public void onProgress(int aPercent){
            // deliberately doing nothing
        }
    }

    private static class UnknownCancellationStatus implements Status {
        public boolean isCalculationCancelled() {
            return false;
        }
    }
}
