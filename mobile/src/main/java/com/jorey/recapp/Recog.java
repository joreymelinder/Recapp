/*package com.jorey.recapp;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;


public class Recog {

    public StreamSpeechRecognizer recognizer;
    public PipedInputStream in;
    public PipedOutputStream out;

    public boolean done=false;

    public Recog(){

        try {
            in = new PipedInputStream();
            out = new PipedOutputStream(in);

            Configuration config=new Configuration();
            config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            config.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

            recognizer=new StreamSpeechRecognizer(config);

            new Thread(new Runnable(){
                @Override
                public void run() {
                    recognizer.startRecognition(in);
                    SpeechResult result;
                    while (!done && (result = recognizer.getResult()) != null) {

                        System.out.format("Hypothesis: %s\n", result.getHypothesis());

                        System.out.println("List of recognized words and their times:");
                        for (WordResult r : result.getWords()) {
                            System.out.println(r);
                        }

                        System.out.println("Best 3 hypothesis:");
                        for (String s : result.getNbest(3))
                            System.out.println(s);
                    }
                }
            }).start();


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
*/