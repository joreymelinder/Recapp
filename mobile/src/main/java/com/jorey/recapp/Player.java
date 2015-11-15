package com.jorey.recapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Player{
    private static final int SAMPLERATE = 44100;
    //                                             {recording channels,playback channels}
    private static final int[] CHANNELS = {AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_OUT_MONO};
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    final int[] BUFFERSIZE={AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[0],ENCODING),AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[1],ENCODING)};

    private Thread playThread = null;
    private boolean isPlaying = false;
    public String playFile;
    public SeekBar seekBar;

    public Player(SeekBar sb){
        seekBar=sb;
    }

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
        int intSize = AudioTrack.getMinBufferSize(SAMPLERATE, CHANNELS[1], ENCODING);

        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, CHANNELS[1], ENCODING, intSize, AudioTrack.MODE_STREAM);
        //at.setVolume(at.getMaxVolume());
        at.play();

        ByteBuffer buff=ByteBuffer.wrap(byteData);
        System.out.println("BUFFER TOTAL: "+buff.remaining());
        System.out.println("ARRAY TOTAL: "+byteData.length);
        int progress=0;
        while(buff.hasRemaining()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float percent=100*((float)buff.position()/(float)byteData.length);
                if((int)percent>progress){
                    progress=(int)percent;
                    seekBar.setProgress(progress);
                    System.out.println("PROGRESS: %"+percent);
                }

                //System.out.println("BUFFER POSITION: "+buff.position());
                at.write(buff,buff.remaining(),AudioTrack.WRITE_NON_BLOCKING);
            }
            else{
                System.out.println("SHIT");
            }
        }

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
