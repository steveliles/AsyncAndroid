package com.packt.androidconcurrency.chapter3.example5;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.packt.androidconcurrency.R;

public class CompassActivity extends Activity {

    private static final String TAG = "androidconcurrency";
    private static final int DIRECTION_CHANGE_MESSAGE = "direction_change_message".hashCode();

    private SensorManager manager;
    private Sensor compass, accelerometer;
    private Handler uiHandler, bgHandler;
    private HandlerThread background;
    private SensorEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch3_example5_compass_layout);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        compass = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();

        background = new HandlerThread("sensor");
        background.setPriority(
                Process.THREAD_PRIORITY_BACKGROUND +
                        Process.THREAD_PRIORITY_MORE_FAVORABLE);
        background.start();
        bgHandler = new Handler(background.getLooper());

        CompassView view = (CompassView)findViewById(R.id.compass);
        uiHandler = new CompassDirectionHandler(view);

        listener = new CompassListener(uiHandler);

        manager.registerListener(
            listener, compass, SensorManager.SENSOR_DELAY_GAME, bgHandler);
        manager.registerListener(
            listener, accelerometer, SensorManager.SENSOR_DELAY_GAME, bgHandler);
    }

    protected void onPause() {
        super.onPause();
        manager.unregisterListener(listener);
        background.quit();
    }

    private static class CompassListener implements SensorEventListener {
        private static final float FILTER = 0.8f;
        private Handler uiHandler;
        private float[] rot = new float[16];
        private float[] inc = new float[16];
        private float[] result = new float[3];
        private float[] gravity, mag;
        private float azimuth;

        public CompassListener(Handler uiHandler) {
            this.uiHandler = uiHandler;
        }

        /**
         * Receive sensor data and calculate the angle to north.
         * This entails a fair bit of computation, so its good
         * to get this off the main thread - especially when
         * we're setting the sensor delay to SENSOR_DELAY_GAME.
         *
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    gravity = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mag = event.values.clone();
                    break;
            }

            if (gravity != null && mag != null){
                if (SensorManager.getRotationMatrix(rot, inc, gravity, mag)){
                    SensorManager.getOrientation(rot, result);
                    float update = 180f + ((180f / 3.142f) * result[0]);

                    // low-pass filter to reduce jitter due to noise
                    // in the sensor data. Leaving a bitter filter
                    // as an exercise :)
                    azimuth = (FILTER * update) + ((1-FILTER) * azimuth);

                    // send position info to ui thread
                    uiHandler.sendMessage(
                        Message.obtain(uiHandler, DIRECTION_CHANGE_MESSAGE, azimuth));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i(TAG, sensor.getName() + " accuracy changed to " + accuracy);
        }
    }

    private static class CompassDirectionHandler extends Handler {
        private CompassView view;

        public CompassDirectionHandler(CompassView view) {
            this.view = view;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DIRECTION_CHANGE_MESSAGE) {
                view.updateDirection((Float) msg.obj);
            }
        }
    }
}
