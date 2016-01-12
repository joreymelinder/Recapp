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

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    private RecordService recordService;
    private boolean bound=false;
    private WearableFrameLayout layout;
    private Button saveButton;

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

        saveButton=(Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound){
                    recordService.stopRecording();
                }
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
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        Intent intent = new Intent(this,RecordService.class);
        stopService(intent);
        unbindService(mServiceConnection);*/
    }

    private void updateDisplay() {
        if (isAmbient()) {
            layout.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            layout.setBackground(null);
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
            bound = true;
        }
    };
}
