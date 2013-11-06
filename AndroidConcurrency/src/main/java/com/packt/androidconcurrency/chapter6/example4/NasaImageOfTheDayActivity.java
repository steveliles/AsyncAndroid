package com.packt.androidconcurrency.chapter6.example4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.Streams;
import com.packt.androidconcurrency.chapter6.DownloadTask;

import java.io.IOException;
import java.io.InputStream;

public class NasaImageOfTheDayActivity extends Activity {

    private static final String RSS_URL = "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss";
    private static NasaRSSParser parser = new NasaRSSParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch6_example4_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        downloadRSS();
    }

    @Override
    protected void onPause() {
        super.onPause();

        DownloadTask.clearCallbacks();
    }

    private void downloadRSS(){
        new DownloadTask<NasaRSS>(RSS_URL) {
            @Override
            public NasaRSS convertInBackground(Uri data)
            throws Exception {
                InputStream in = null;
                try {
                    in = openStream(data);
                    return parser.parse(in);
                } finally {
                    Streams.close(in);
                }
            }

            @Override
            public void onSuccess(NasaRSS rss) {
                for (int i=0; i<rss.size(); i++) {
                    NasaRSS.Item item = rss.get(i);
                    displayText(i, item.url);
                    downloadImage(i, item.url, 8);
                }
            }

            private void displayText(int i, String name) {
                name = name.substring(name.lastIndexOf("/")+1, name.length());
                TextView text = (TextView) getRow(i).findViewById(R.id.text);
                text.setText(name);
            }
        }.execute(this);
    }

    private void downloadImage(final int i, String url, final int sampleSize){
        new DownloadTask<Bitmap>(url) {
            @Override
            public Bitmap convertInBackground(Uri data) throws Exception {
                InputStream in = null;
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = sampleSize;
                    in = openStream(data);
                    return BitmapFactory.decodeStream(in, null, opts);
                } finally {
                    Streams.close(in);
                }
            }

            @Override
            public void onSuccess(Bitmap data) {
                ImageView image = (ImageView)
                    getRow(i).findViewById(R.id.img);
                image.setImageBitmap(data);
            }
        }.execute(this);
    }

    private ViewGroup getRow(int i) {
        ViewGroup root = (ViewGroup)findViewById(R.id.root);
        return (ViewGroup)root.getChildAt(i);
    }

    private InputStream openStream(Uri uri) {
        try {
            return getContentResolver().openInputStream(uri);
        } catch (IOException exc) {
            Log.e(LaunchActivity.TAG, "bad uri?", exc);
            return null;
        }
    }
}
