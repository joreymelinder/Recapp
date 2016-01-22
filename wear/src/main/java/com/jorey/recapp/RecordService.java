package com.jorey.recapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Arrays;

public class RecordService extends Service {
    private static String tag = "RecordService";

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
    public Context context;
    public Conversation talk=new Conversation();
    public Thread recordingThread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(tag, "in onCreate");
        startRecording();
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.v(tag, "in onBind");
        return rBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(tag, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(tag, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(tag, "in onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public void setContext(Context c){
        context=c;
        talk.context=c;
    }

    public void startRecording() {
        recordingThread=new Thread(new Runnable() {
            public void run() {
                Log.v(tag, "startRecording");
                //Log.v("watch","start Recording");
                //System.out.println("RECORDING");
                talk = new Conversation();
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLERATE, CHANNELS[0], ENCODING, BUFFER * BYTES);
                recorder.startRecording();
                isRecording = true;
                //Log.v(LOG_TAG,"writeConversation");

                while(isRecording) {
                    writeConversation();
                }
                Log.v(tag, "recording stopped");

                recorder.stop();
                recorder.release();
                try {
                    notify();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
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
        //Log.v(tag,"write conversation");
        short sData[] = new short[BUFFER];
        try{
            recorder.read(sData, 0, BUFFER);
            byte bData[] = byteConversion(sData);
            //Log.v(tag, Arrays.toString(bData));
            //System.out.println("audio data: " + Arrays.toString(bData));
            talk.speak(bData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        //Log.v(LOG_TAG,"stopRecording");
        isRecording=false;
    }

    public class RecordBinder extends Binder {
        RecordService getService() {
            return RecordService.this;
        }
    }
}