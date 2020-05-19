package com.example.tensorflowliteapp;

/**
 * Created by marianne-linhares on 20/04/17.
 */

public class Classification {

    float conf;
    String label;

    Classification(float confidence, String labels){
        conf=confidence;
        label=labels;
    }

}
