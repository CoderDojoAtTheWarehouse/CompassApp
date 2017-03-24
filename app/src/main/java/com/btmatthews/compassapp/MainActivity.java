package com.btmatthews.compassapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
f

    private Sensor compass;

    private Sensor accelerometer;

    private ImageView pointer;

    private float[] lastAccelerometer = new float[3];

    private float[] lastMagnetometer = new float[3];

    private boolean lastAccelerometerSet = false;

    private boolean lastMagnetometerSet = false;

    private float currentDegree = 0f;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointer = (ImageView)findViewById(R.id.pointer);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public final void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    }

    @Override
    public final void onSensorChanged(final SensorEvent event) {
        if (event.sensor == compass) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        } else if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            final float[] rotation = new float[9];
            final float[] orientation = new float[3];
            SensorManager.getRotationMatrix(rotation, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotation, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            final RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            pointer.startAnimation(ra);
            currentDegree = -azimuthInDegrees;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
