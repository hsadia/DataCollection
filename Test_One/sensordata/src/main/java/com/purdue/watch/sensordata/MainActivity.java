package com.purdue.watch.sensordata;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener{

    private TextView GyroTextView, AccTextView, MagTextView;
    protected SensorManager SensorManager;
    protected Sensor GyroSensor, AccSensor, MagSensor, LinearSensor, GravSensor;
    private List<String> AccList, GyroList, MagList, LinearList, GravList;

    private boolean collect = false;

    private float ax,ay,az;
    private float gx,gy,gz;
    private float mx,my,mz;
    private float linearAccX, linearAccY, linearAccZ;
    private float grx,gry,grz;
    private float second;

    private boolean justStart = true;
    private long startTick = 0;
    String folderName = "Data";
    String sDate = "";
    public Date startDate;
    private boolean shouldKeepData = false;
    FileStreamManager fileStreamManager = new FileStreamManager();
    private GoogleApiClient mGoogleApiClient;

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
                Log.d("Start", "collect = true");
            }
        });

        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                collect = false;
                Log.d("Stop & Save", "collect = false");

                int requestCode = 1;
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                Log.d("Permission Check READ", "Result: " + permissionCheck);
                if(permissionCheck == PackageManager.PERMISSION_DENIED) {// PERMISSION_GRANTED is 0
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                }
                permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                Log.d("Permission Check WRITE", "Result: " + permissionCheck);
                if(permissionCheck == PackageManager.PERMISSION_DENIED) {// PERMISSION_GRANTED is 0
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                savedata();
                try {
                    fileStreamManager.ReadDir();
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
        });

        final Button exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d("Exit", "Bye Bye!");

                if(shouldKeepData==false)
                    fileStreamManager.deleteFolder(folderName, sDate);
                onStop();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

    }



    private void savedata() {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        sDate = sDateFormat.format(startDate);

        Context context = getApplicationContext();
        boolean result = fileStreamManager.OutputList( "acc.txt",AccList, folderName, sDate, context);
        AccList.clear();
        if(result)
            Log.d("savedata","saved accelerometer file");

        result = fileStreamManager.OutputList("gyro.txt",GyroList, folderName, sDate, context);
        GyroList.clear();
        if(result)
            Log.d("savedata","saved gyrometer file");

        result = fileStreamManager.OutputList("mag.txt",MagList, folderName, sDate, context);
        MagList.clear();
        if(result)
             Log.d("savedata","saved magnetometer file");


        result = fileStreamManager.OutputList("linearAcc.txt",LinearList, folderName, sDate, context);
        LinearList.clear();
        if(result)
            Log.d("savedata","saved linear accelerometer file");

        result = fileStreamManager.OutputList("grav.txt",GravList, folderName, sDate, context);
        GravList.clear();
        if(result)
            Log.d("savedata","saved gravity file");

    }


    private void registerSensors() {

        second = ax = ay = az = gx = gy = gz = mx = my = mz = linearAccX = linearAccY = linearAccZ = grx= gry = grz = 0;
        SensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        GyroSensor = null;
        if (SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            GyroSensor = SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            SensorManager.registerListener(this,
                    GyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
            GyroList = new ArrayList<String>();
        }

        if (SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            AccSensor = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            SensorManager.registerListener(this,
                    AccSensor, SensorManager.SENSOR_DELAY_FASTEST);
            AccList = new ArrayList<String>();
        }

        if (SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            MagSensor = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            SensorManager.registerListener(this,
                    MagSensor, SensorManager.SENSOR_DELAY_FASTEST);
            MagList = new ArrayList<String>();
        }
        if (SensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            GravSensor = SensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            SensorManager.registerListener(this,
                   GravSensor, SensorManager.SENSOR_DELAY_FASTEST);
            GravList = new ArrayList<String>();
        }
        if (SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            LinearSensor = SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            SensorManager.registerListener(this,
                    LinearSensor, SensorManager.SENSOR_DELAY_FASTEST);
            LinearList = new ArrayList<String>();

        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        SensorManager.unregisterListener(this, AccSensor);
        SensorManager.unregisterListener(this, GyroSensor);
        SensorManager.unregisterListener(this, MagSensor);
        SensorManager.unregisterListener(this, GravSensor);
        SensorManager.unregisterListener(this, LinearSensor);

    }

    @Override
    protected void onResume() {

        super.onResume();
        SensorManager.registerListener(this,
                GyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(this,
                AccSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(this,
                MagSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(this,
                GravSensor, SensorManager.SENSOR_DELAY_FASTEST);
        SensorManager.registerListener(this,
                LinearSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause(){

        super.onPause();
        SensorManager.unregisterListener(this, AccSensor);
        SensorManager.unregisterListener(this, GyroSensor);
        SensorManager.unregisterListener(this, MagSensor);
        SensorManager.unregisterListener(this, GravSensor);
        SensorManager.unregisterListener(this, LinearSensor);
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

       // synchronized (this) {
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
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    linearAccX = event.values[0];
                    linearAccY = event.values[1];
                    linearAccZ = event.values[2];
                    if (collect)
                        LinearList.add(second+";"+linearAccX+";"+linearAccY+";"+linearAccZ);
                    break;
                case Sensor.TYPE_GRAVITY:
                    grx = event.values[0];
                    gry = event.values[1];
                    grz = event.values[2];
                    if(collect)
                        GravList.add(second+";"+grx+";"+gry+";"+grz);
                    break;

            }
     //   }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        return;

    }
    
}