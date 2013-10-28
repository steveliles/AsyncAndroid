package com.packt.androidconcurrency.chapter4.example5;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListView;

import com.packt.androidconcurrency.R;
import com.packt.androidconcurrency.CompatibleActivity;

/**
 * Simple activity demonstrating the user of a CursorLoader
 * to list all of the known image files on the device.
 *
 * Contacts the MediaStore and lists the files on a background
 * thread before updating the user-interface.
 *
 * Registers an observer to watch for new files being added
 * to the media-store and triggers reload if updates are
 * detected.
 */
public class FileListActivity extends CompatibleActivity
implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "androidconcurrency";
    private static final int MEDIA_STORE_LOADER = "image_loader".hashCode();

    private CursorAdapter adapter;
    private ContentObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch4_example5_layout);

        ListView list = (ListView) findViewById(R.id.list);

        // We'll use the SDK class SimpleCursorAdapter
        // to map our cursor data to the view.
        adapter = new SimpleCursorAdapter(
            getApplicationContext(), // avoid leaking references to the activity context!
            R.layout.ch4_example5_cell, // use this layout for each row in our list
            null, // don't have a cursor yet
            new String[]{ MediaStore.Images.Media.DISPLAY_NAME }, // fetch the display name only
            new int[]{ R.id.filename }, // map the display name to the example5_cell view
            0
        );

        // register our cursoradapter with the view.
        list.setAdapter(adapter);

        // make sure our loader is initialised and set up
        // to call back on our Activity
        final Loader loader = getSupportLoaderManager()
            .initLoader(MEDIA_STORE_LOADER, null, this);

        // create an observer that will kick the loader if
        // it discovers that the underlying datasource has changed
        observer = new MediaContentObserver(new Handler(), loader);

        // register our observer to be notified of changes to the
        // MediaStore's image database - e.g. if a new image is added
        // from the camera (to test, run the app to see the current
        // set of images, then press home, launch the camera and take
        // a pic, then re-open the app and see that the data is reloaded).
        getContentResolver().registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer);
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle bundle) {
        // create a CursorLoader that loads from the MediaStore's
        // database of images. We're only going to display the file
        // names in this activity, but we'll also need to request
        // the _ID as CursorAdapter needs it.
        return new CursorLoader(this.getApplicationContext(),
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[]{
                MediaStore.Images.Media._ID, // required, else we'll get an exception!
                MediaStore.Images.Media.DISPLAY_NAME
            }, "", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor media) {
        // the data finished loading, swap the cursors over
        // in our adapter. Note that calling changeCursor
        // directly closes the old cursor, which is not strictly
        // necessary when using a CursorLoader - the CursorLoader
        // will make sure that the old cursor is closed if we call
        // swapCursor instead.
        adapter.changeCursor(media);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // don't forget to unregister the observer if we're changing
        // configurations
        if (isChangingConfigurations() || isFinishing()) {
            getContentResolver().unregisterContentObserver(observer);
        }

        if (isFinishing())
            getSupportLoaderManager().destroyLoader(MEDIA_STORE_LOADER);
    }

    private static class MediaContentObserver extends ContentObserver
    {
        private Loader<?> loader;

        public MediaContentObserver(Handler handler, Loader<?> loader) {
            super(handler);
            this.loader = loader;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.i(TAG, "received notification of updates!");
            loader.onContentChanged();
        }
    }
}
