package com.jorey.recapp;

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
        File file=new File(getFilePath());
        try {
            file.createNewFile();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("FILE ERROR");
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
        } catch (IOException e) {
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

    //Generate the name of the file where the audio will be saved.
    private String getFilePath(){
        cal= new GregorianCalendar();
        String filePath = "/sdcard/recapp/";
        filePath+=cal.get(GregorianCalendar.YEAR)+"/";
        filePath+=cal.get(GregorianCalendar.MONTH)+"/";
        filePath+=cal.get(GregorianCalendar.DATE)+"/";
        new File(filePath).mkdirs();
        filePath+=currentTime()+".pcm";
        return filePath;
    }
}
