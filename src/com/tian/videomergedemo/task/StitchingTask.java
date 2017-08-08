package com.tian.videomergedemo.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tian.videomergedemo.inter.CompletionListener;
import com.tian.videomergedemo.manager.VideoStitchingRequest;

/**
 * Created by TCX on 22/01/16.
 */
public class StitchingTask implements Runnable {

    private Context context;
    private VideoStitchingRequest videoStitchingRequest;
    private CompletionListener completionListener;
    private String mFfmpegInstallPath;

    public StitchingTask(Context context, String mFfmpegInstallPath, VideoStitchingRequest stitchingRequest, CompletionListener completionListener) {
        this.context = context;
        this.mFfmpegInstallPath = mFfmpegInstallPath;
        this.videoStitchingRequest = stitchingRequest;
        this.completionListener = completionListener;
    }


    @Override
    public void run() {
        stitchVideo(context, mFfmpegInstallPath, videoStitchingRequest, completionListener);
    }


    private void stitchVideo(Context context, String mFfmpegInstallPath, VideoStitchingRequest videoStitchingRequest, final CompletionListener completionListener) {


    	//合成的路径
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ffmpeg_videos";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File inputfile = new File(path, "input.txt");

        try {
            inputfile.createNewFile();
            FileOutputStream out = new FileOutputStream(inputfile);
            for (String string : videoStitchingRequest.getInputVideoFilePaths()) {
                out.write(("file " + "'" + string + "'").getBytes());
                out.write("\n".getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        execFFmpegBinary("-i " + src.getAbsolutePath() + " -ss "+ startMs/1000 + " -to " + endMs/1000 + " -strict -2 -async 1 "+ dest.getAbsolutePath());

        
        //合成的FFmpeg命令行
        String[] sampleFFmpegcommand = {mFfmpegInstallPath, "-f", "concat", "-i", inputfile.getAbsolutePath(), "-c", "copy", videoStitchingRequest.getOutputPath()};
        try {
            Process ffmpegProcess = new ProcessBuilder(sampleFFmpegcommand)
                    .redirectErrorStream(true).start();

            String line;

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(ffmpegProcess.getInputStream()));
            Log.d("***", "*******Starting FFMPEG");
            while ((line = reader.readLine()) != null) {

                Log.d("***", "***" + line + "***");
            }
            Log.d(null, "****ending FFMPEG****");

            ffmpegProcess.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputfile.delete();
        //合成成功的接口回调
        completionListener.onProcessCompleted("Video Stitiching Comleted",null);

    }
}
