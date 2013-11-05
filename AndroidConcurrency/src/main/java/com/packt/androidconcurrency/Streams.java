package com.packt.androidconcurrency;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public class Streams {

    public static void close(Closeable... streams) {
        for (Closeable c : streams) {
            close(c);
        }
    }

    public static void close(Closeable stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException anExc) {
            Log.w(LaunchActivity.TAG, "problem closing stream", anExc);
        }
    }

}
