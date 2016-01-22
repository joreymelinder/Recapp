package com.jorey.recapp;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
    public String tag = "Conversation";
    public Conversation(){
        startTime=currentTime();
    }

    public Conversation(byte[] sound){
        for(int i=0;i<sound.length/2048;i++)
        {
            byte[] moment=new byte[2048];
            for(int j=0;j<2048;j++){
                moment[j]=sound[2048*i+j];
            }
            data.add(moment);
        }
    }

    public Conversation(File file){
        read(file);
    }

    public Conversation(InputStream is){
        read(is);
    }


    //Add a line of sound data.
    public void speak(byte[] sound){
        //System.out.println("TIME: "+currentTime());
        //System.out.println(Arrays.toString(sound)+"\n");
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

        FileOutputStream os = null;
        String filename=getFilePath()+currentName()+".pcm";
        File file=new File(filename);
        try {
            file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.v("Conversation","create file: "+filename);

        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(byte[] b:data){
            try {
                os.write(b, 0, size);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            os.close();
            Log.v("Conversation","file closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Save audio to a file.
    public void save(String name) {
        // Write the output audio in byte

        FileOutputStream os = null;
        String filename=getFilePath()+name+".pcm";
        File file=new File(filename);
        try {
            file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.v("Conversation","create file: "+filename);

        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.v("Conversation", "write data: " + data.toString());

        for(byte[] b:data){
            try {
                Log.v("soundsize",""+b.length);
                os.write(b, 0, 2048);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            os.close();
            Log.v("Conversation","file closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(File file){
        FileInputStream is=null;
        try{
            is=new FileInputStream(file);

            int done=0;
            while(done!=-1){
                byte[] buffer=new byte[2048];
                done=is.read(buffer,0,2048);
                data.add(buffer);
            }
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void read(InputStream is){
        try{
            int done=0;
            while(done!=-1){
                byte[] buffer=new byte[2048];
                done=is.read(buffer,0,2048);
                Log.v(tag,""+buffer);
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
