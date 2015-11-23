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
import java.util.GregorianCalendar;

public class Player{
    private static final int SAMPLERATE = 44100;
    //                                             {recording channels,playback channels}
    private static final int[] CHANNELS = {AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_OUT_MONO};
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    final int[] BUFFERSIZE={AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[0],ENCODING),AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[1],ENCODING)};
    private AudioTrack track;
    private Thread playThread = null;
    public boolean isPlaying = false;
    public String playFile;
    public SeekBar seekBar;

    public Player(SeekBar sb){
        seekBar=sb;
    }

    public void play(String fileName,int position){
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

        track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, CHANNELS[1], ENCODING, intSize, AudioTrack.MODE_STREAM);
        //track.setVolume(track.getMaxVolume());
        track.play();

        ByteBuffer buff=ByteBuffer.wrap(byteData);

        buff.position((int)(position*byteData.length/100.0));

        System.out.println("BUFFER TOTAL: "+buff.remaining());
        System.out.println("ARRAY TOTAL: "+byteData.length);

        int progress=position;
        while(buff.hasRemaining()){
            if(isPlaying) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    float percent = 100 * ((float) buff.position() / (float) byteData.length);
                    if ((int) percent > progress) {
                        progress = (int) percent;
                        seekBar.setProgress(progress);
                        //System.out.println("PROGRESS: %" + percent);
                    }

                    //System.out.println("BUFFER POSITION: "+buff.position());
                    try{
                        if(track!=null) {
                            track.write(buff, buff.remaining(), AudioTrack.WRITE_NON_BLOCKING);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("SHIT");
                }
            }
        }
        stop();
    }

    public void playStart(String fileName,final int position){
        isPlaying = true;
        playFile=fileName;
        playThread = new Thread(new Runnable() {
            public void run() {
                play(playFile,position);
            }
        }, "Player Thread");
        playThread.start();
    }

    public void stop(){
        isPlaying=false;
        track.stop();
        track.release();
        track=null;
    }


}
