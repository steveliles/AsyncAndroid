package com.packt.androidconcurrency.chapter5.example3;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.packt.androidconcurrency.R;

import java.util.ArrayList;
import java.util.List;

public class MediaCursorAdapter extends CursorAdapter {

    private LoaderManager mgr;
    private LayoutInflater inf;
    private int count;
    private List<Integer> ids;

    public MediaCursorAdapter(Context ctx, LoaderManager mgr) {
        super(ctx.getApplicationContext(), null, true);
        this.mgr = mgr;
        inf = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ids = new ArrayList<Integer>();
    }

    @Override
    public View newView(final Context ctx, Cursor crsr, ViewGroup parent) {
        ImageView view = (ImageView) inf.
            inflate(R.layout.ch5_example2_cell, parent, false);
        view.setId(MediaCursorAdapter.class.hashCode() + ++count);
        mgr.initLoader(
            view.getId(), null, new ThumbnailCallbacks(ctx, view));
        ids.add(view.getId()); // remember the id, so we can clean it up later
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Loader<?> l = mgr.getLoader(view.getId());
        ThumbnailLoader loader = (ThumbnailLoader) l;

        Integer mediaId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));

        ImageView image = (ImageView) view;
        image.setImageBitmap(null);
        loader.setMediaId(mediaId);
    }

    public void destroyLoaders() {
        for (Integer id : ids)
            mgr.destroyLoader(id);
    }
}

