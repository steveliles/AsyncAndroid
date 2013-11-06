package com.packt.androidconcurrency;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LaunchActivity extends Activity {

    public static final String TAG = "android.concurrency";
    public static final int[] COLORS = new int[]{
        0xff000000, // not used
        0xffff0000, // ch1
        0xffff9900, // ch2
        0xff88dd00, // ch3
        0xff0099ff, // ch4
        0xffff0099, // ch5
        0xff9900ff  // ch6
    };

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_layout);

        handler = new Handler();

        new Thread(){
            public void run(){
                final List<ActivityInfo> info = getActivityList();
                Log.i(TAG, info.size() + " demos to enjoy!");

                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        initActivityGrid(info);
                    }
                });
            }
        }.start();
    }

    private void initActivityGrid(final List<ActivityInfo> info) {
        GridView grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(new ArrayAdapter<ActivityInfo>(this, R.layout.book_activity_cell, info) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                    view = inflater.inflate(R.layout.book_activity_cell, parent, false);
                }

                ViewGroup vg = (ViewGroup) view;
                TextView title = (TextView) vg.findViewById(R.id.title);
                TextView description = (TextView) vg.findViewById(R.id.description);

                ActivityInfo info = getItem(position);

                String name = info.loadLabel(getPackageManager()).toString();
                int chapter = Integer.parseInt(name.substring(7, name.indexOf(" ")));
                view.setBackgroundColor(COLORS[chapter]);

                title.setText(name);
                if (info.descriptionRes > 0)
                    description.setText(getString(info.descriptionRes));

                return view;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityInfo ai = info.get(position);
                try {
                    LaunchActivity.this.startActivity(new Intent(LaunchActivity.this, Class.forName(ai.name)));
                } catch (Exception exc) {
                    Log.e(TAG, "Surprisingly, the Activity class " + ai.name + " doesn't seem to exist", exc);
                }
            }
        });
    }

    private List<ActivityInfo> getActivityList() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            List<ActivityInfo> result = new ArrayList<ActivityInfo>(Arrays.asList(info.activities));

            for (Iterator<ActivityInfo> i = result.iterator(); i.hasNext(); ) {
                ActivityInfo ai = i.next();
                if (!ai.loadLabel(pm).toString().startsWith("Chapter"))
                    i.remove();
            }

            return result;
        } catch (PackageManager.NameNotFoundException exc) {
            Log.e(TAG, "Well that's pretty weird, our package doesn't exist!", exc);
            return new ArrayList<ActivityInfo>();
        }
    }
}
