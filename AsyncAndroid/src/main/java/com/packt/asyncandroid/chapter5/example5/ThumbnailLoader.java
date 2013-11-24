package com.packt.asyncandroid.chapter5.example5;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;

/**
 * An AsyncTaskLoader implementation that loads a Bitmap thumbnail
 * in the background from the MediaStore.
 *
 * There are two good reasons to load these thumbnails off the UI thread:
 *
 * (1) Loading bitmaps entails IO, which we should always try to keep
 *     off the main thread if we want our apps to be responsive.
 *
 * (2) The first time we request a thumbnail from the MediaStore it is
 *     quite possibly going to have to be created by scaling down the
 *     original image - this is a potentially expensive operation and
 *     we definitely don't want to do that on the main thread!
 *
 * Notice that:
 *
 * (i) The Loader does not know or care what the bitmap will be used for.
 *     Its job is purely to load the image, to cache the image for future
 *     use, and to properly clean up in the lifecycle callbacks.
 *
 * (ii) Bitmaps are "special", in that their data is managed off-heap, and
 *      is potentially large. We will clean them up expediently by calling
 *      recycle when the bitmap is no longer needed.
 */
public class ThumbnailLoader extends AsyncTaskLoader<Bitmap> {

    private Bitmap data, old;
    private Integer mediaId;

    public ThumbnailLoader(Context context, Integer mediaId) {
        super(context);
        this.mediaId = mediaId;
    }

    public ThumbnailLoader(Context context) {
        super(context);
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
        // notify the machinery of AsyncTaskLoader that something
        // has changed, so that it will trigger a reload when
        // necessary.
        onContentChanged();
    }

    @Override
    public Bitmap loadInBackground() {
        ContentResolver res = getContext().getContentResolver();
        if (mediaId != null) {
            return MediaStore.Images.Thumbnails.getThumbnail(
                res, mediaId, MediaStore.Images.Thumbnails.MINI_KIND, null);
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        // if we already have a loaded bitmap, deliver
        // it to the callbacks...
        if (data != null)
            deliverResult(data);

        // if we've been notified that the content
        // changed, or if we don't currently have
        // a bitmap at all ...
        if (takeContentChanged() || data == null) {
            // kick-start loading of the new data
            forceLoad();
        }
    }

    @Override
    public void deliverResult(Bitmap data) {
        super.deliverResult(this.data = data);
    }

    @Override
    protected void onStopLoading() {
        // cancel and make sure to clean up
        cancelLoad();
    }

    @Override
    public void onCanceled(Bitmap data) {
        // loading was cancelled before we got
        // here, so we must discard the loaded
        // bitmap (if there was one).
        if (data != null)
            data.recycle();
        old = null;
    }

    @Override
    protected void onReset() {
        // if we have a bitmap, make sure it is
        // recycled asap.
        if (data != null) {
            data.recycle();
            data = null;
        }
    }
}
