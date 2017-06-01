package com.purdue.watch.test_one;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity implements
        SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener,
        MessageApi.MessageListener {


    private TextView mGyroTextView, mAcclmtrTextView, mCmpsTextView;
    private SensorManager mSensorManager;
    private GoogleApiClient mGoogleApiClient;
    private Sensor mGyroSensor,mAcclSensor, mCmpsSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        Log.d("onCeate", "After calling mGoogleApiClient.connect()");
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAcclmtrTextView = (TextView)findViewById(R.id.atextview);
        mGyroTextView = (TextView)findViewById(R.id.gtextView);
        mCmpsTextView = (TextView)findViewById(R.id.ctextView);
        registerSensors();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }
//GoogleApiClient Functions; onConnected, onConnectionSuspended, onConnectionFailed
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("GAC", "onConnected: Google API client connected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GAC", "onConnectedSuspended: Google API client suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("GAC", "onConnectionFailed: " + connectionResult.toString());
    }

//Data Layer Api function
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }
//Message Api function
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("Test_One", "onMessageReceived: " + messageEvent);

    }

    //SensorEvenListener functions
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
      return;
    }

//Private Function of the class
    private void registerSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor = mAcclSensor = mCmpsSensor = null;
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mSensorManager.registerListener(this,
                    mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mGyroTextView.setText("Gyroscrope available!");
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAcclSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this,
                    mAcclSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mAcclmtrTextView.setText("Accelerometer available!");
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mCmpsSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mSensorManager.registerListener(this,
                    mCmpsSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mCmpsTextView.setText("Compass available!");
        }
    }
}
