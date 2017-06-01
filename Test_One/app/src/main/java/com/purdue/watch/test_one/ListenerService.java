package com.purdue.watch.test_one;

import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Haleema on 30/05/2017.
 */



public class ListenerService extends WearableListenerService {
    private SensorManager sensorManager;
    private PutDataMapRequest sensorData;

    String PATH_NOTIFICAITON_MESSAGE = "com/purdue/watch/test_one/MainActivity";
    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().equals(PATH_NOTIFICAITON_MESSAGE)) {
            Log.d("Listener Class", "Message Received");
        }


    }



}
