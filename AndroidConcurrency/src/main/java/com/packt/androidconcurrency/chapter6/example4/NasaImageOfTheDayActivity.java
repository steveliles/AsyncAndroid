package com.packt.androidconcurrency.chapter6.example4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.packt.androidconcurrency.chapter6.DownloadService;

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

        Intent intent = new Intent(this, NasaRSSDownloadService.class);
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
        private Context ctx;
        private ViewGroup root;

        @Override
        public void handleMessage(Message message) {
            if (message.what == DownloadService.SUCCESSFUL) {
                if (root != null) {
                    if (message.arg1 == RSS_URL.hashCode()) {
                        NasaRSS rss = (NasaRSS) message.obj;
                        for (int i=0; i<rss.size(); i++)
                            handle(i, rss.get(i));
                    } else {
                        displayImage(message.arg1, (Bitmap)message.obj);
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
            Intent intent = new Intent(ctx, ImageDownloadService.class);
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
    }

}
