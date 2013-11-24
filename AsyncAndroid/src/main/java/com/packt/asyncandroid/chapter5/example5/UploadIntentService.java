package com.packt.asyncandroid.chapter5.example5;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.packt.asyncandroid.R;

public class UploadIntentService extends IntentService {

    private ImageUploader uploader;

    public UploadIntentService() {
        super("upload");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        uploader = new ImageUploader(getContentResolver());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri data = intent.getData();
        int id = Integer.parseInt(data.getLastPathSegment());
        String msg = String.format("Uploading %s.jpg",id);
        ProgressNotificationCallback progress =
            new ProgressNotificationCallback(this, id, msg);
        if (uploader.upload(data, progress)) {
            progress.onComplete(
                String.format("Successfully uploaded %s.jpg", id));
        } else {
            progress.onComplete(
                String.format("Upload failed %s.jpg", id));
        }
    }

    private class ProgressNotificationCallback
    implements ImageUploader.ProgressCallback {

        private NotificationCompat.Builder builder;
        private NotificationManager nm;
        private int id, prev;

        public ProgressNotificationCallback(Context ctx, int id, String msg) {
            this.id = id;
            prev = 0;
            builder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentTitle(getString(R.string.upload_service))
                .setContentText(msg)
                .setProgress(100,0,false);
            nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(id, builder.build());
        }

        @Override
        public void onProgress(int max, int progress) {
            int percent = (int)((100f*progress)/max);
            if (percent > (prev + 5)) {
                builder.setProgress(100, percent, false);
                nm.notify(id, builder.build());
                prev = percent;
            }
        }

        @Override
        public void onComplete(String msg) {
            builder.setProgress(0, 0, false);
            builder.setContentText(msg);
            nm.notify(id, builder.build());
        }
    }
}
