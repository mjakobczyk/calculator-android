package com.example.adaptedcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final double ACCELERATION_THRESHOLD = 3;
    private static final long TAP_INTERVAL = 250000000;

    private SensorManager sensorManager;
    private Sensor sensor;
    long lastTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAccelerometer();
    }

    private void initializeAccelerometer() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Toast.makeText(this, R.string.no_accelerometer, Toast.LENGTH_LONG).show();
        }
    }

    private void onTapRecognized() {
        Log.d(TAG, "Tap recognized!");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double zAxisAcceleration = event.values[2];
        long timestamp = event.timestamp;

        if (Math.abs(zAxisAcceleration) > ACCELERATION_THRESHOLD
                && timestamp - lastTimestamp >= TAP_INTERVAL) {
            onTapRecognized();
            lastTimestamp = timestamp;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
