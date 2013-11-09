package com.packt.androidconcurrency.chapter5.example5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ImageView;

/**
 * LoaderCallbacks implementation that expects to receive a Bitmap in onLoadFinished
 * and will display the bitmap with a nice fade-in effect.
 */
public class ThumbnailCallbacks implements LoaderManager.LoaderCallbacks<Bitmap> {
    private Context context;
    private ImageView image;

    public ThumbnailCallbacks(Context context, ImageView image) {
        // ensuring that we always only keep a handle on the Application Context
        // so that we don't leak references to the Activity!
        this.context = context.getApplicationContext();
        this.image = image;
    }

    @Override
    public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
        return new ThumbnailLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap b) {
        // we could just set the bitmap to the imageview, but
        // lets create a drawable that fades from transparent to
        // our bitmap, for a bit of extra swankiness...
        final TransitionDrawable drawable =
                new TransitionDrawable(new Drawable[] {
                        new ColorDrawable(Color.TRANSPARENT),
                        new BitmapDrawable(context.getResources(), b)
                });

        // set our fade-in drawable to the image view
        image.setImageDrawable(drawable);

        // fade in over 0.2 seconds
        drawable.startTransition(200);
    }

    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {
        // nothing much for us to do here.
    }
}
