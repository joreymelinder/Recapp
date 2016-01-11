package com.jorey.recapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class RecordService extends Service {
    private static String LOG_TAG = "RecordService";

    private IBinder rBinder = new RecordBinder();

    private static final int SAMPLERATE = 44100;
    //                                             {recording channels,playback channels}
    private static final int[] CHANNELS = {AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_OUT_MONO};
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    final int BUFFER = 1024;
    final int BYTES = 2;

    private AudioRecord recorder;

    private boolean isRecording = false;
    public boolean kill=false;

    public Conversation talk=new Conversation();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        startRecording();
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");

        return rBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    private void startRecording() {
        new Thread(new Runnable() {
            public void run() {
                while(!kill) {
                    Log.v(LOG_TAG, "startRecording");
                    talk = new Conversation();
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLERATE, CHANNELS[0], ENCODING, BUFFER * BYTES);
                    recorder.startRecording();
                    isRecording = true;
                    Log.v(LOG_TAG,"writeConversation");
                    while(isRecording) {
                        writeConversation();
                    }
                    Log.v(LOG_TAG, "recording stopped");

                    recorder.stop();
                    recorder.release();

                    talk.save(BUFFER * BYTES);
                }
            }
        }, "AudioRecorder Thread").start();
    }

    //convert short to byte
    private byte[] byteConversion(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private void writeConversation() {
        short sData[] = new short[BUFFER];
        try{
            recorder.read(sData, 0, BUFFER);
            byte bData[] = byteConversion(sData);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });
            talk.speak(bData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        Log.v(LOG_TAG,"stopRecording");
        isRecording=false;
    }

    public class RecordBinder extends Binder {
        RecordService getService() {
            return RecordService.this;
        }
    }
}