package com.jorey.recapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class PlayFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public ListView recordingList;
    public Button playButton;
    public SeekBar seekBar;
    public int selected=-1;
    //public Recorder recorder=new Recorder();
    public Player player;

    public static PlayFragment newInstance() {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_play, container, false);
        recordingList=(ListView) view.findViewById(R.id.recording_list);
        ArrayList<String> recList= new ArrayList<>();
        File file= new File(getFilePath());
        if(file.listFiles().length>0){
            File[] list = file.listFiles();
            for(File f:list){
                recList.add(f.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, android.R.id.text1,recList);
            recordingList.setAdapter(adapter);
        }


        recordingList.setItemsCanFocus(true);
        recordingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected=position;
            }
        });

        recordingList.getSelectedItemPosition();

        seekBar=(SeekBar) view.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println("SEEKBAR PROGRESS: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        player=new Player(seekBar);
        playButton=(Button) view.findViewById(R.id.play_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                player.playStart(getFilePath()+recordingList.getItemAtPosition(selected));
            }
        });
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private String getFilePath(){
        GregorianCalendar cal= new GregorianCalendar();
        String filePath = "/sdcard/recapp/";
        filePath+=cal.get(GregorianCalendar.YEAR)+"/";
        filePath+=cal.get(GregorianCalendar.MONTH)+"/";
        filePath+=cal.get(GregorianCalendar.DATE)+"/";
        return filePath;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
