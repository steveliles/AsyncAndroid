package com.packt.androidconcurrency.chapter7.example3;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;

/**
 * Demonstrates a BroadcastReceiver using the "goAsync" method
 * introduced in API level 11. Note that we are still subject to
 * the 10 second budget, but we can work in the background instead
 * of on the main thread without worrying about being shut-down
 * when the onReceive method completes.
 *
 * We MUST call result.finish within our 10 second budget (give or
 * take) to terminate the receive otherwise the system will shut
 * us down with ANR, so this is not suitable for long running
 * tasks or things that have dependencies outside our control (e.g
 * data connection speeds).
 */
public class AsyncBroadcastReceiver extends BroadcastReceiver {

    public static final String PRIME_TO_FIND = "prime_to_find";

    @SuppressLint("NewApi")
    @Override
    public void onReceive(final Context context, final Intent intent) {


        if (Build.VERSION.SDK_INT > 11) {
            final PendingResult result = goAsync();
            final int n = intent.getIntExtra(PRIME_TO_FIND, 2);
            new AsyncTask<Void,Void,BigInteger>(){
                @Override
                protected BigInteger doInBackground(Void... params) {
                    BigInteger prime = new BigInteger("2");
                    for (int i=0; i<n; i++) {
                        prime = prime.nextProbablePrime();
                    }
                    return prime;
                }

                @Override
                protected void onPostExecute(BigInteger prime) {
                    notifyUser(context, String.format("The %sth prime is %s",
                        n, prime.toString()));
                    result.finish();
                }
            }.execute();
        } else {
            notifyUser(context,
                "Boo, can't go async, we're API level " +
                Build.VERSION.SDK_INT);
        }
    }

    private void notifyUser(Context context, String msg) {
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(context.getString(R.string.ch7_ex1))
                .setContentText(msg);
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(R.string.ch7_ex1, builder.build());
    }
}
