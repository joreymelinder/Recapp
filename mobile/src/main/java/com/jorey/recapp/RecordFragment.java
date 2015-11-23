package com.jorey.recapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.criapp.circleprogresscustomview.CircleProgressView;


public class RecordFragment extends Fragment {
    private Recorder recorder=new Recorder();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_record, container, false);
        recorder.start();
        
        saveButton = (CircleProgressView) view.findViewById(R.id.vButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.simulateProgress(360);
            }
        });
        saveButton.setmCallback(new CircleProgressView.CircleProgressCallback() {
            @Override
            public void onError(int progress1) {

            }

            @Override
            public void onFinish(int progress1) {
                saveButton.setProgress(0);
                recorder.stop();
                listener.onRecord();
            }

            @Override
            public void onProgress(int progress1) {

            }
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
        listener = null;
    }

    public interface OnRecordInteractionListener {
        public void onRecord();
    }
}