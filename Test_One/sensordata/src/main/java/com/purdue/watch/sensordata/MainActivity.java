package com.purdue.watch.sensordata;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener{

    private TextView GyroTextView, AccTextView, MagTextView;
    private SensorManager SensorManager;
    private Sensor GyroSensor, AccSensor, MagSensor;
    private List<String> AccList, GyroList, MagList;

    private boolean collect = false;

    private float ax,ay,az;
    private float gx,gy,gz;
    private float mx,my,mz;
    private float second;

    private boolean justStart = true;
    private long startTick = 0;
    String folderName = "Data";
    String sDate = "";
    public Date startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTextView();
        setButtonListeners();
        registerSensors();

    }

    private void setTextView() {

        GyroTextView = (TextView) findViewById(R.id.gyrotxt);
        AccTextView = (TextView) findViewById(R.id.aclmtrtxt);
        MagTextView = (TextView)findViewById(R.id.cmpstxt);

    }

    private void setButtonListeners() {

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                collect = true;
                justStart = true;
                Log.d("button", "collect = true");
            }
        });

        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                collect = false;
                Log.d("button", "collect = false");
                savedata();
            }
        });

    }

    private void savedata() {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        sDate = sDateFormat.format(startDate);
        FileStreamManager fileStreamManager = new FileStreamManager();

        fileStreamManager.OutputList("/"+folderName+"/" + sDate + "/acc.txt",AccList);
        AccList.clear();
        Log.d("savefile","saved accelerometer file");

        fileStreamManager.OutputList("/"+folderName+"/" + sDate + "/gyro.txt",GyroList);
        GyroList.clear();
        Log.d("savefile","saved gyrometer file");

        fileStreamManager.OutputList("/"+folderName+"/" + sDate + "/mag.txt",MagList);
        MagList.clear();
        Log.d("savefile","save magnetometer file");

    }


    private void registerSensors() {

        second = ax = ay = az = gx = gy = gz = mx = my = mz = 0;
        SensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        GyroSensor = null;
        if (SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            GyroSensor = SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            SensorManager.registerListener(this,
                    GyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
            GyroList = new ArrayList<String>();
        }

        if (SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            AccSensor = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            SensorManager.registerListener(this,
                    AccSensor, SensorManager.SENSOR_DELAY_NORMAL);
            AccList = new ArrayList<String>();
        }

        if (SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            MagSensor = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            SensorManager.registerListener(this,
                    MagSensor, SensorManager.SENSOR_DELAY_NORMAL);
            MagList = new ArrayList<String>();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        SensorManager.unregisterListener(this, AccSensor);
        SensorManager.unregisterListener(this, GyroSensor);
        SensorManager.unregisterListener(this, MagSensor);

    }

    @Override
    protected void onResume() {

        super.onResume();
        SensorManager.registerListener(this,
                GyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        SensorManager.registerListener(this,
                AccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        SensorManager.registerListener(this,
                MagSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    protected void onPause(){

        super.onPause();
        SensorManager.unregisterListener(this, AccSensor);
        SensorManager.unregisterListener(this, GyroSensor);
        SensorManager.unregisterListener(this, MagSensor);

    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        long time = System.nanoTime();
        if (justStart) {
            justStart = false;
            startTick = time;
            startDate = new java.util.Date();
        }
        second = (float) ((time - startTick) / 1000000000.0);

        synchronized (this) {
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    AccTextView.setText("Accelerometer \nx:"+Float.toString(event.values[0]) +
                            "\n y:"+Float.toString(event.values[1]) +
                            "\n z:"+Float.toString(event.values[2]));
                    ax = event.values[0];
                    ay = event.values[1];
                    az = event.values[2];
                    if (collect)
                        AccList.add(second+";"+ax+";"+ay+";"+az);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    GyroTextView.setText("Gyroscope \nx:"+Float.toString(event.values[0]) +
                            "\n y:"+Float.toString(event.values[1]) +
                            "\n z:"+Float.toString(event.values[2]));
                    gx = event.values[0];
                    gy = event.values[1];
                    gz = event.values[2];
                    if (collect)
                        GyroList.add(second+";"+gx+";"+gy+";"+gz);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    MagTextView.setText("Magnetometer \nx:"+Float.toString(event.values[0]) +
                            "\n y:"+Float.toString(event.values[1]) +
                            "\n z:"+Float.toString(event.values[2]));
                    mx = event.values[0];
                    my = event.values[1];
                    mz = event.values[2];
                    if (collect)
                        MagList.add(second+";"+mx+";"+my+";"+mz);
                    break;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        return;

    }



}