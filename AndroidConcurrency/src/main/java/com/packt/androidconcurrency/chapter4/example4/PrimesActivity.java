package com.packt.androidconcurrency.chapter4.example4;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.R;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Just for fun, continuing our running example with calculating primes,
 * this time using a loader to manage the background work, cache the results,
 * and manage the state across activity restarts.
 */
public class PrimesActivity extends FragmentActivity
implements LoaderManager.LoaderCallbacks<List<String>> {

    private static final int PRIMES_LOADER = "example1_primes_loader".hashCode();

    private PrimesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch4_example4_layout);

        adapter = new PrimesAdapter(this);
        GridView grid = (GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);

        final PrimesLoader loader = (PrimesLoader)
            getSupportLoaderManager().initLoader(PRIMES_LOADER, null, this);

        Button go = (Button) findViewById(R.id.go);

        // read the value of the text input and, if it is a valid number,
        // update the datasource and trigger the loader to recalculate
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView input = (TextView)findViewById(R.id.input);
                String value = input.getText().toString();
                if (value.matches("[1-9]+[0-9]*")) {
                    // if the value is a number, trigger the loader to
                    // reload when appropriate.
                    loader.setPrimesToFind(Integer.parseInt(value));
                } else {
                    Toast.makeText(PrimesActivity.this, "not a number!", 5000).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing())
            getSupportLoaderManager().destroyLoader(PRIMES_LOADER);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle bundle) {
        return new PrimesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> primes) {
        adapter.clear();
        for (String prime : primes)
            adapter.add(prime);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
    }

    private static class PrimesLoader extends AsyncTaskLoader<List<String>> {
        private List<String> data;
        private int primesToFind;

        public PrimesLoader(Context context) {
            super(context);
        }

        public void setPrimesToFind(int primesToFind) {
            this.primesToFind = primesToFind;
            onContentChanged();
        }

        @Override
        protected void onStartLoading() {
            if (data != null)
                deliverResult(data);

            if (takeContentChanged() || data == null)
                forceLoad();
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        public void onCanceled(List<String> data) {
            rollbackContentChanged();
        }

        @Override
        public List<String> loadInBackground() {
            List<String> result = new ArrayList<String>();
            BigInteger prime = new BigInteger("2");
            for (int i=0; i<primesToFind; i++) {
                prime = prime.nextProbablePrime();
                result.add(0, prime.toString());
            }
            commitContentChanged();
            return result;
        }

        @Override
        public void deliverResult(List<String> data) {
            if (isReset()) {
                this.data = null;
            } else if (isStarted()) {
                super.deliverResult(data);
                this.data = data;
            }
        }
    }

    private static class PrimesAdapter extends ArrayAdapter<String>{

        private static int[] colors = new int[]{ Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW };

        public PrimesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PrimeView result = (PrimeView) convertView;

            if (result == null) {
                result = new PrimeView(parent.getContext());
            }

            result.setValue(getItem(position));
            result.setBackgroundColor(colors[position % colors.length]);

            return result;
        }
    }
}
