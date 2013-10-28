package com.packt.androidconcurrency.chapter4.example1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ImageView;

import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.chapter4.ThumbnailLoader;

public class ThumbnailActivity extends FragmentActivity
implements LoaderManager.LoaderCallbacks<Bitmap> {

    private static final int LOADER_ID = "thumb_loader".hashCode();

    private Integer mediaId;
    private ImageView thumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch4_example1_layout);
        thumb = (ImageView) findViewById(R.id.thumb);
        mediaId = getMediaIdFromIntent(getIntent());
        if (mediaId != null)
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
        return new ThumbnailLoader(getApplicationContext(), mediaId);
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
        thumb.setImageBitmap(bitmap);
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {
        // we don't need to do anything here.
    }

    private Integer getMediaIdFromIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            return new Integer(uri.getLastPathSegment());
        } else {
            return null;
        }
    }
}
