package com.packt.asyncandroid.chapter5.example5;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.packt.asyncandroid.CompatibleActivity;
import com.packt.asyncandroid.R;

public class UploadPhotoActivity extends CompatibleActivity
implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "asyncandroid";
    public static final int MS_LOADER = "ms_crsr".hashCode();

    private MediaCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch5_example5_layout);

        adapter = new MediaCursorAdapter(
            getApplicationContext(),
            getSupportLoaderManager());

        GridView grid = (GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)adapter.getItem(position);
                int mediaId = cursor.getInt(
                    cursor.getColumnIndex(MediaStore.Images.Media._ID));
                Uri uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    Integer.toString(mediaId));
                Intent intent = new Intent(
                    UploadPhotoActivity.this, UploadIntentService.class);
                intent.setData(uri);
                startService(intent);
            }
        });

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
