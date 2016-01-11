package com.jorey.recapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class PlayFragment extends Fragment {
    private OnPlayInteractionListener listener;

    public ListView recordingList;
    public ImageButton playButton;
    public SeekBar seekBar;
    public Button buttParty;
    public int selected=-1;
    public Player player;
    private View view;
    private int buttNum=-1;

    public static PlayFragment newInstance() {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public PlayFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_play, container, false);
        player=new Player(this);

        seekBar=(SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    player.stop();
                    player.playStart(player.file, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        recordingList=(ListView) view.findViewById(R.id.recording_list);
        load();
        recordingList.setItemsCanFocus(true);
        recordingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((getFilePath() + recordingList.getItemAtPosition(position)).equals(player.file)){
                    playButton.setImageResource(R.drawable.ic_media_play);
                }
                else{
                    if(player.isPlaying) {
                        playButton.setImageResource(R.drawable.ic_media_play);
                    }
                }
                selected = position;
            }
        });

        playButton = (ImageButton) view.findViewById(R.id.play_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying) {
                    player.stop();
                    if ((getFilePath() + recordingList.getItemAtPosition(selected)).equals(player.file)) {
                        playButton.setImageResource(R.drawable.ic_media_play);
                    } else {
                        playButton.setImageResource(R.drawable.ic_media_pause);
                        player.playStart(getFilePath() + recordingList.getItemAtPosition(selected), 0);
                    }
                } else {
                    playButton.setImageResource(R.drawable.ic_media_pause);
                    if ((getFilePath() + recordingList.getItemAtPosition(selected)).equals(player.file) && player.progress < 100) {
                        player.playStart(getFilePath() + recordingList.getItemAtPosition(selected), player.progress);
                    } else {
                        player.playStart(getFilePath() + recordingList.getItemAtPosition(selected), 0);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnPlayInteractionListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    public void load(){
        ArrayList<String> recList= new ArrayList<>();
        File file= new File(getFilePath());
        if(file.listFiles()!=null){
            File[] list = file.listFiles();
            for(File f:list){
                recList.add(f.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1, android.R.id.text1,recList);
            recordingList.setAdapter(adapter);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnPlayInteractionListener {
    }

    //TODO: the whole file management system needs to be less bad. right now it is very bad.
    public String getFilePath(){
        GregorianCalendar cal= new GregorianCalendar();
        String filePath = "/sdcard/recapp/";
        filePath+=cal.get(GregorianCalendar.YEAR)+"/";
        filePath+=cal.get(GregorianCalendar.MONTH)+"/";
        filePath+=cal.get(GregorianCalendar.DATE)+"/";
        return filePath;
    }
}