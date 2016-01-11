package com.jorey.recapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

    public boolean isPlaying = false;
    public boolean ready = true;

    //public String playFile;
    public PlayFragment playFragment;

    public int progress;
    public String file;

    public Player(PlayFragment pf){
        playFragment=pf;
    }

    public void play(String filename,int position){

        byte[] byteData=getBytes(filename);

        int intSize = AudioTrack.getMinBufferSize(SAMPLERATE, CHANNELS[1], ENCODING);
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLERATE, CHANNELS[1], ENCODING, intSize, AudioTrack.MODE_STREAM);
        track.play();

        ByteBuffer buff=ByteBuffer.wrap(byteData);

        buff.position((int) (position * byteData.length / 100.0));
        Log.d("Player","buffer position: "+buff.position());

        System.out.println("BUFFER TOTAL: "+buff.remaining());
        System.out.println("ARRAY TOTAL: "+byteData.length);

        progress=position;

        while(isPlaying && buff.hasRemaining()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float percent = 100 * ((float) buff.position() / (float) byteData.length);

                if ((int) percent > progress) {
                    progress = (int) percent;

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            playFragment.seekBar.setProgress(progress);
                        }
                    });
                }

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

        if(isPlaying){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    playFragment.playButton.setImageResource(R.drawable.ic_media_play);
                }
            });
            isPlaying=false;
            Log.v("Player","play stopped");
        }
        else{
            Log.v("Player","play finished");
        }
        track.stop();
        track.release();
        ready=true;
    }

    private byte[] getBytes(String filename){
        File file = new File(filename);
        byte[] bytes = new byte[(int) file.length()];

        FileInputStream in;
        try {
            in = new FileInputStream( file );
            in.read( bytes );
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void playStart(final String filename,final int position){
        Log.d("Player","file: "+file);
        Log.d("Player","filename: "+filename);
        Log.d("Player","progress: "+progress);
        Log.d("Player","position: "+position);
        new Thread(new Runnable() {
            public void run() {
                while(!ready){}
                ready=false;
                isPlaying = true;
                file=filename;
                play(filename,position);
            }
        }, "Player Thread").start();
    }

    public void stop(){
        isPlaying=false;
    }
}
