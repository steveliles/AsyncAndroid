package com.packt.androidconcurrency.chapter6.example4;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.ImageView;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.chapter6.DownloadService;

public class NasaImageOfTheDayActivity extends Activity {

    private static final String URL = "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss";

    private static final RSSHandler handler = new RSSHandler();
    private static final Messenger messenger = new Messenger(handler);

    @Override
    protected void onResume() {
        super.onResume();

        handler.attach((ImageView)findViewById(R.id.img));

        Intent intent = new Intent(this, NasaRSSDownloadService.class);
        intent.putExtra(DownloadService.DOWNLOAD_FROM_URL, URL);
        intent.putExtra(DownloadService.REQUEST_ID, 1);
        intent.putExtra(DownloadService.MESSENGER, messenger);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.detach();
    }

    private static class RSSHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            if (message.what == DownloadService.SUCCESSFUL) {
                NasaRSS rss = (NasaRSS) message.obj;
                Log.i(LaunchActivity.TAG, "found " + rss.size() + "items");
            } else {
                Log.w(LaunchActivity.TAG, "download failed :(");
            }
        }

        public void attach(ImageView view) {
            // todo
        }

        public void detach() {
            // todo
        }
    }

}
