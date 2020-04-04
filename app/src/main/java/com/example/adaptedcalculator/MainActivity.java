package com.example.adaptedcalculator;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final double ACCELERATION_THRESHOLD = 3;
    private static final long TAP_INTERVAL_IN_NS = 250000000;

    private SensorManager sensorManager;
    private Sensor sensor;
    long lastTimestamp;
    int firstNumber;
    int secondNumber;
    int operator;
    int result;
    int state;
    CountDownTimer stateTimer;

    TextView firstNumberTextView;
    TextView secondNumberTextView;
    TextView operatorTextView;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAccelerometer();

        firstNumber = secondNumber = 1;

        firstNumberTextView = findViewById(R.id.firstNumber);
        secondNumberTextView = findViewById(R.id.secondNumber);
        operatorTextView = findViewById(R.id.operator);
        resultTextView = findViewById(R.id.result);

        refreshScreen();

        stateTimer = new CountDownTimer(3000, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                state = (state + 1) % 4;
                if (state == 0) {
                    clearScreen();
                } else if (state == 3) {
                    switch (operator) {
                        case 0:
                            result = firstNumber + secondNumber;
                            break;
                        case 1:
                            result = firstNumber - secondNumber;
                            break;
                        case 2:
                            result = firstNumber * secondNumber;
                            break;
                    }
                }

                refreshScreen();
                this.start();
            }
        }.start();
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

    @Override
    public void onSensorChanged(final SensorEvent event) {
        double zAxisAcceleration = event.values[2];
        long timestamp = event.timestamp;

        if (Math.abs(zAxisAcceleration) > ACCELERATION_THRESHOLD
                && timestamp - lastTimestamp >= TAP_INTERVAL_IN_NS) {
            onTapRecognized();
            lastTimestamp = timestamp;
        }
    }

    private void onTapRecognized() {
        Log.d(TAG, "Tap recognized!");
        stateTimer.cancel();
        switch (state) {
            case 0:
                firstNumber = firstNumber % 9 + 1;
                break;
            case 1:
                operator = (operator + 1) % 3;
                break;
            case 2:
                secondNumber = secondNumber % 9 + 1;
                break;
        }

        refreshScreen();
        stateTimer.start();
    }

    private void refreshScreen() {
        firstNumberTextView.setText(String.valueOf(firstNumber));
        secondNumberTextView.setText(String.valueOf(secondNumber));
        String operationSign = null;
        switch (operator) {
            case 0:
                operationSign = "+";
                break;
            case 1:
                operationSign = "-";
                break;
            case 2:
                operationSign = "*";
                break;
        }
        operatorTextView.setText(operationSign);

        if (state == 3)
            resultTextView.setText(String.valueOf(result));
        else
            resultTextView.setText("?");

        firstNumberTextView.setTextColor(Color.BLACK);
        secondNumberTextView.setTextColor(Color.BLACK);
        operatorTextView.setTextColor(Color.BLACK);
        resultTextView.setTextColor(Color.BLACK);

        switch (state) {
            case 0:
                firstNumberTextView.setTextColor(Color.RED);
                break;
            case 1:
                operatorTextView.setTextColor(Color.RED);
                break;
            case 2:
                secondNumberTextView.setTextColor(Color.RED);
                break;
            case 3:
                resultTextView.setTextColor(Color.GREEN);
        }
    }

    private void clearScreen() {
        firstNumber = 1;
        secondNumber = 1;
        operator = 0;
        result = 0;
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
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {}
}
