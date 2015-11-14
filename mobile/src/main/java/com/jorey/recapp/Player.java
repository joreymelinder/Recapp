package com.jorey.recapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;

public class Player{
    private static final int SAMPLERATE = 44100;
    //                                             {recording channels,playback channels}
    private static final int[] CHANNELS = {AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_OUT_MONO};
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    final int[] BUFFERSIZE={AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[0],ENCODING),AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[1],ENCODING)};

    private Thread playThread = null;
    private boolean isPlaying = false;
    public String playFile;

    public void play(String fileName){
        File file = new File(fileName);
        byte[] byteData = new byte[(int) file.length()];

        FileInputStream in;
        try {
            in = new FileInputStream( file );
            in.read( byteData );
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int intSize = android.media.AudioTrack.getMinBufferSize(SAMPLERATE, CHANNELS[1], ENCODING);

        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, CHANNELS[1], ENCODING, intSize, AudioTrack.MODE_STREAM);
        //TODO figure out how to change the volume.

        at.play();
        at.write(byteData, 0, byteData.length);
        at.stop();
        at.release();
    }

    public void playStart(String fileName){
        isPlaying = true;
        playFile=fileName;
        playThread = new Thread(new Runnable() {
            public void run() {
                play(playFile);
            }
        }, "Player Thread");
        playThread.start();
    }
}
