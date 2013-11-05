package com.packt.androidconcurrency.chapter6.example4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.Streams;
import com.packt.androidconcurrency.chapter6.CachingDownloadService;
import com.packt.androidconcurrency.chapter6.DownloadService;

import java.io.IOException;
import java.io.InputStream;

public class NasaImageOfTheDayActivity extends Activity {

    private static final String RSS_URL = "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss";

    private static final RSSHandler handler = new RSSHandler();
    private static final Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch6_example4_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.attach(this, (ViewGroup)findViewById(R.id.root));

        Intent intent = new Intent(this, CachingDownloadService.class);
        intent.putExtra(DownloadService.DOWNLOAD_FROM_URL, RSS_URL);
        intent.putExtra(DownloadService.REQUEST_ID, RSS_URL.hashCode());
        intent.putExtra(DownloadService.MESSENGER, messenger);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.detach();
    }

    private static class RSSHandler extends Handler {
        private NasaRSSParser parser = new NasaRSSParser();
        private Context ctx;
        private ViewGroup root;

        @Override
        public void handleMessage(Message message) {
            if (message.what == DownloadService.SUCCESSFUL) {
                if (root != null) {
                    if (message.arg1 == RSS_URL.hashCode()) {
                        Uri uri = (Uri) message.obj;
                        handleRSS(uri);
                    } else {
                        Uri uri = (Uri)message.obj;
                        displayImage(message.arg1, loadBitmap(uri,8));
                    }
                }
            } else {
                Log.w(LaunchActivity.TAG, "download failed :(");
            }
        }

        public void attach(Context ctx, ViewGroup root) {
            this.ctx = ctx;
            this.root = root;
        }

        public void detach() {
            ctx = null;
            root = null;
        }

        private void handleRSS(Uri uri) {
            InputStream in = null;
            try {
                in = openStream(uri);
                NasaRSS rss = parser.parse(in);
                for (int i=0; i<rss.size(); i++)
                    handle(i, rss.get(i));
            } catch (Exception exc) {
                Log.e(LaunchActivity.TAG, "parsing RSS", exc);
            } finally {
                Streams.close(in);
            }
        }

        private void handle(int i, NasaRSS.Item item) {
            displayText(i, item.url);
            downloadImage(i, item.url);
        }

        private void displayText(int i, String name) {
            name = name.substring(name.lastIndexOf("/")+1, name.length());
            ViewGroup row = (ViewGroup)root.getChildAt(i);
            TextView text = (TextView)row.findViewById(R.id.text);
            text.setText(name);
        }

        private void downloadImage(int i, String url) {
            Intent intent = new Intent(ctx, CachingDownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_FROM_URL, url);
            intent.putExtra(DownloadService.REQUEST_ID, i);
            intent.putExtra(DownloadService.MESSENGER, messenger);
            ctx.startService(intent);
        }

        private void displayImage(int i, Bitmap bitmap) {
            ViewGroup row = (ViewGroup)root.getChildAt(i);
            ImageView image = (ImageView)row.findViewById(R.id.img);
            image.setImageBitmap(bitmap);
        }

        protected Bitmap loadBitmap(Uri data, int sampleSize) {
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

        private InputStream openStream(Uri uri) {
            try {
                return ctx.getContentResolver().openInputStream(uri);
            } catch (IOException exc) {
                Log.e(LaunchActivity.TAG, "bad uri?", exc);
                return null;
            }
        }
    }

}
