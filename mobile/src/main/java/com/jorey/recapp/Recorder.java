package com.jorey.recapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Recorder {

    private static final int SAMPLERATE = 44100;
    //                                             {recording channels,playback channels}
    private static final int[] CHANNELS = {AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_OUT_MONO};
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    final int BUFFER = 1024;
    final int BYTES = 2;
    final int[] BUFFERSIZE={AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[0],ENCODING),AudioTrack.getMinBufferSize(SAMPLERATE,CHANNELS[1],ENCODING)};

    private AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLERATE, CHANNELS[0],ENCODING, BUFFER * BYTES);
    private AudioTrack track=null;
    byte[] data = new byte[BUFFERSIZE[0]];

    private Thread recordingThread = null;
    private boolean isRecording = false;

    public Conversation talk=new Conversation();

    public void start(){
        talk=new Conversation();
        startRecording();
    }

    public void stop(){
        stopRecording();
    }

    public void play(){
        File file = new File("/sdcard/recapp/recording.pcm");
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

    private void startRecording() {
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeConversation();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void startRecording2(){
        recorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                String filepath = Environment.getExternalStorageDirectory().getPath();
                FileOutputStream os = null;

                try {
                    os = new FileOutputStream("/sdcard/recapp/recording.pcm");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while(isRecording) {
                    recorder.read(data, 0, data.length);
                    try {
                        os.write(data, 0, BUFFERSIZE[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

        short sData[] = new short[BUFFER];

        while (isRecording) {
            recorder.read(sData, 0, BUFFER);
            byte bData[] = byteConversion(sData);
            talk.speak(bData);
        }
    }

    private void stopRecording() {
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLERATE, CHANNELS[0],ENCODING, BUFFER * BYTES);;
            recordingThread = null;
        }
        talk.save(BUFFER*BYTES);
        start();
    }
}
