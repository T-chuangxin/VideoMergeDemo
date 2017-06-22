package com.tian.videomergedemo.manager;

import java.util.ArrayList;

/**
 * Created by Karthik on 22/01/16.
 */
public class VideoStitchingRequest {
    private ArrayList<String> inputVideoFilePaths;
    private String outputPath;



    public VideoStitchingRequest(Builder builder){
        this.inputVideoFilePaths=builder.inputVideoFilePaths;
        this.outputPath=builder.outputPath;
    }

    public ArrayList<String> getInputVideoFilePaths() {
        return inputVideoFilePaths;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public static class Builder{

        private ArrayList<String> inputVideoFilePaths;
        private String outputPath;

        public Builder inputVideoFilePath(ArrayList<String> inputVideoFilePaths){
            this.inputVideoFilePaths=inputVideoFilePaths;
            return this;
        }

        public Builder outputPath(String outputPath){
            this.outputPath=outputPath;
            return this;
        }

        public VideoStitchingRequest build(){
            return new VideoStitchingRequest(this);
        }


    }

}
