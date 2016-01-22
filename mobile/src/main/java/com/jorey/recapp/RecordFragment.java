package com.jorey.recapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.criapp.circleprogresscustomview.CircleProgressView;


public class RecordFragment extends Fragment {
    private RecordService recordService;
    private boolean bound=false;
    private CircleProgressView saveButton;
    private OnRecordInteractionListener listener;

    public static RecordFragment newInstance() {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_record, container, false);

        //start recording

        Intent intent = new Intent(getContext(),RecordService.class);
        getContext().startService(intent);
        getContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        saveButton = (CircleProgressView) view.findViewById(R.id.vButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.simulateProgress(360);
            }
        });
        saveButton.setmCallback(new CircleProgressView.CircleProgressCallback() {

            @Override
            public void onFinish(int progress1) {
                saveButton.setProgress(0);
                Log.v("RecordFragment","savebutton progress (should be 0): "+saveButton.getProgress());
                //recorder.stop();
                listener.onRecord();
                if(bound){
                    recordService.stopRecording();
                }
            }

            @Override public void onProgress(int progress1) {} @Override public void onError(int progress1) {}
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnRecordInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Intent intent = new Intent(getContext(),RecordService.class);
        //getContext().stopService(intent);
        recordService.stopSelf();
        getContext().unbindService(mServiceConnection);
        listener = null;
    }

    public interface OnRecordInteractionListener {
        void onRecord();
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