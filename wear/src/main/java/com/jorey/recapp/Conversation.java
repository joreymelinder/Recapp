package com.jorey.recapp;

import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Queue;

public class Conversation {
    private Queue<byte[]> data = new LinkedList<>();
    private GregorianCalendar cal=new GregorianCalendar();
    private int startTime,endTime;
    private boolean full=false;
    private int limit=300000;

    public Conversation(){
        startTime=currentTime();
    }


    //Add a line of sound data.
    public void speak(byte[] sound){
        //System.out.println("TIME: "+currentTime());
        //System.out.println(Arrays.toString(sound)+"\n");
        Log.v("sound bytes",Arrays.toString(sound));
        data.add(sound);
        if(!full){
            if(timeElapsed()>limit){
                full=true;
            }
        }
        else{
            data.remove();
        }
    }

    //Save audio to a file.
    public void save(int size) {
        // Write the output audio in byte

        Log.v("Conversation","write data: "+data.toString());
        byte[] recording=new byte[data.size()*2048];
        int i=0;
        for(byte[] b:data){
            for(byte a:b){
                recording[i]=a;
                i++;
            }
        }


        //NOW I NEED TO SEND TO PHONE

        Asset asset = Asset.createFromBytes(recording);
        PutDataMapRequest dataMap = PutDataMapRequest.create("/txt");
        dataMap.getDataMap().putAsset("com.example.company.key.TXT", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }

    //The number of milliseconds since the recording first started
    private int timeElapsed(){
        return currentTime()-startTime;
    }

    //The number of milliseconds since the last day
    private int currentTime(){
        cal=new GregorianCalendar();
        return cal.get(GregorianCalendar.MILLISECOND)+cal.get(GregorianCalendar.SECOND)*1000+cal.get(GregorianCalendar.MINUTE)*60000+cal.get(GregorianCalendar.HOUR)*3600000;
    }

    //The number of milliseconds since the last day
    private String currentName(){
        cal=new GregorianCalendar();
        return cal.get(GregorianCalendar.HOUR)+":"+cal.get(GregorianCalendar.MINUTE)+":"+cal.get(GregorianCalendar.SECOND);
    }

    //Generate the name of the file where the audio will be saved.
    private String getFilePath(){
        cal= new GregorianCalendar();
        String filePath = "/sdcard/recapp/";
        filePath+=cal.get(GregorianCalendar.YEAR)+"/";
        filePath+=cal.get(GregorianCalendar.MONTH)+"/";
        filePath+=cal.get(GregorianCalendar.DATE)+"/";
        new File(filePath).mkdirs();
        filePath+=currentName()+".pcm";
        return filePath;
    }
}
