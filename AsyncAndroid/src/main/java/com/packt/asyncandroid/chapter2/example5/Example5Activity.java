package com.packt.asyncandroid.chapter2.example5;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.packt.asyncandroid.R;

import java.math.BigInteger;

public class Example5Activity extends FragmentActivity
implements AsyncListener<Integer, BigInteger> {

    public static final String PRIMES = "primes";
    private ProgressDialog dialog;
    private TextView resultView;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch2_example_layout);
        resultView = (TextView)findViewById(R.id.result);
        goButton = (Button)findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PrimesFragment primes = (PrimesFragment)
                    getSupportFragmentManager().findFragmentByTag(PRIMES);

                if (primes == null) {
                    primes = new PrimesFragment();
                    FragmentTransaction transaction =
                        getSupportFragmentManager().beginTransaction();
                    transaction.add(primes, PRIMES);
                    transaction.commit();
                }
            }
        });
    }

    public void onPreExecute() {
        onProgressUpdate(0);
    }

    public void onProgressUpdate(Integer... progress) {
        if (dialog == null) {
            prepareProgressDialog();
        }
        dialog.setProgress(progress[0]);
    }

    public void onPostExecute(BigInteger result) {
        resultView.setText(result.toString());
        cleanUp();
    }

    public void onCancelled(BigInteger result) {
        resultView.setText("cancelled at " + result);
        cleanUp();
    }

    private void prepareProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.calculating);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                PrimesFragment primes = (PrimesFragment)
                        getSupportFragmentManager().findFragmentByTag(PRIMES);
                primes.cancel();
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
    }

    private void cleanUp() {
        dialog.dismiss();
        dialog = null;
        FragmentManager fm = getSupportFragmentManager();
        PrimesFragment primes = (PrimesFragment) fm.findFragmentByTag(PRIMES);
        fm.beginTransaction().remove(primes).commit();
    }
}
