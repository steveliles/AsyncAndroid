package com.packt.asyncandroid.chapter4.example3;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.GridView;

import com.packt.asyncandroid.R;
import com.packt.asyncandroid.CompatibleActivity;

public class MediaStoreActivity extends CompatibleActivity
implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "asyncandroid";
    public static final int MS_LOADER = "ms_crsr".hashCode();

    private MediaCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch4_example3_layout);

        adapter = new MediaCursorAdapter(
            getApplicationContext(),
            getSupportLoaderManager());

        GridView grid = (GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);

        final CursorLoader loader = (CursorLoader)
            getSupportLoaderManager().initLoader(MS_LOADER, null, this);

        loader.setUpdateThrottle(10000L);
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(this,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
            }, "", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor media) {
        adapter.changeCursor(media);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing()) {
            // if we don't want to use these loaders in other activities
            // this is our last chance to clean them up.
            getSupportLoaderManager().destroyLoader(MS_LOADER);
            adapter.destroyLoaders();
        }
    }
}
