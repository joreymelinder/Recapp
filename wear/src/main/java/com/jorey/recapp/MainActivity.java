package com.jorey.recapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableFrameLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    private RecordService recordService;
    private boolean bound=false;
    private WearableFrameLayout layout;
    private Button saveButton;
    public GoogleApiClient mGoogleApiClient;

    public Context context=this;

    private String tag="Main Activity";
    public Thread endThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("main activity", "created");
        setContentView(R.layout.activity_main);

        setAmbientEnabled();
        layout = (WearableFrameLayout) findViewById(R.id.container);

        //start recording


        Intent intent = new Intent(this,RecordService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        //recordService.talk.context=this;
        //recordService.context=this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.v(tag,"api client connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.v(tag,"api client suspended");
                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.v(tag,"api client failed");
                    }
                }).addApi(Wearable.API).build();



        saveButton=(Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(tag, "clicked");

                if (bound) {
                    recordService.stopRecording();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            recordService.recordingThread.wait();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        send();
                        recordService.startRecording();
                    }
                }).start();
            }
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }


    private void updateDisplay() {
        if (isAmbient()) {
            layout.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            layout.setBackground(null);
        }
    }

    public void send(){
        try {
            Log.v(tag,"sending to phone");
            byte[] recording = recordService.talk.getBytes();

            Asset asset = Asset.createFromBytes(recording);
            //Log.v(tag,"ass data: "+asset.getData().toString());
            PutDataMapRequest dataMap = PutDataMapRequest.create("/recording");
            dataMap.getDataMap().putAsset("recording", asset);
            //dataMap.getDataMap().putByteArray("recording",recording);
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RecordService.RecordBinder myBinder = (RecordService.RecordBinder) service;
            recordService = myBinder.getService();
            recordService.setContext(context);
            bound = true;
        }
    };
}
