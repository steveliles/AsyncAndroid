package com.packt.androidconcurrency.chapter6.example2;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.chapter6.AsyncTaskMessengerService;

import java.math.BigInteger;

public class PrimesAsyncTaskMessengerService extends AsyncTaskMessengerService {

    public static final int FIND_PRIME = "find_prime".hashCode();
    public static final int RESULT = "nth_prime".hashCode();

    @Override
    protected void doInBackground(int what, int arg1, Object obj, Messenger replyTo) {
        if (what == FIND_PRIME) {
            try {
                if (replyTo != null) {
                    BigInteger result = calculateNthPrime((Integer) obj);
                    replyTo.send(Message.obtain(null, RESULT, (Integer) obj, 0, result.toString()));
                }
            } catch (RemoteException exc) {
                Log.e(LaunchActivity.TAG, "Unable to send message", exc);
            }
        }
    }

    private BigInteger calculateNthPrime(int primeToFind) {
        BigInteger prime = new BigInteger("2");
        for (int i=0; i<primeToFind; i++) {
            prime = prime.nextProbablePrime();
        }
        return prime;
    }
}
