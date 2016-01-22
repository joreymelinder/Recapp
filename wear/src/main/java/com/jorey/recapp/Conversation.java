package com.jorey.recapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileInputStream;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Conversation {
    private Queue<byte[]> data = new LinkedList<>();
    private GregorianCalendar cal=new GregorianCalendar();
    private int startTime,endTime;
    private boolean full=false;
    private int limit=300000;
    public Context context;
    private String tag = "Conversation";

    public Conversation(){
        startTime=currentTime();
    }

    public Conversation(File file){
        read(file);
    }


    //Add a line of sound data.
    public void speak(byte[] sound){
        //Log.v("sound bytes",Arrays.toString(sound));
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

        Log.v(tag,"making file");
        byte[] recording=getBytes();


        //NOW I NEED TO SEND TO PHONE
        Log.v(tag,"Sending file");
        //setup data layer client
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                }).addApi(Wearable.API).build();

        Asset asset = Asset.createFromBytes(recording);
        PutDataMapRequest dataMap = PutDataMapRequest.create("/txt");
        dataMap.getDataMap().putAsset("com.example.company.key.TXT", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);

    }

    public byte[] getBytes(){
        // Write the output audio in byte

        Log.v(tag,"getBytes");

        byte[] recording=new byte[data.size()*2048];
        int i=0;
        for(byte[] b:data){
            for(byte a:b){
                recording[i]=a;
                i++;
            }
        }

        return recording;
    }

    public void read(File file){
        Log.v(tag,"reading file");
        FileInputStream is=null;
        try{
            is=new FileInputStream(file);

            int done=0;
            while(done!=-1){
                byte[] buffer=new byte[2048];
                done=is.read(buffer,0,2048);
                //Log.v(tag,""+buffer);
                data.add(buffer);
            }
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
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
        return filePath;
    }
}
