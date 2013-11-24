package com.packt.asyncandroid.chapter4.example2;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;

import com.packt.asyncandroid.R;

public class MediaCursorAdapter extends SimpleCursorAdapter {
    private static String[] FIELDS = new String[]{
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME
    };

    private static int[] VIEWS = new int[]{
        R.id.media_id, R.id.display_name
    };

    public MediaCursorAdapter(Context context) {
        super(context, R.layout.ch4_example2_cell, null, FIELDS, VIEWS, 0);
    }
}
