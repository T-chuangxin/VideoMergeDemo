package com.tian.videomergedemo.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.tian.videomergedemo.inter.CompletionListener;
import com.tian.videomergedemo.utils.TimeFormatUtils;

/**
 * 
 */
public class TrimTask implements Runnable {

    private Context context;
//    private VideoStitchingRequest videoStitchingRequest;
    private CompletionListener completionListener;
    private String mFfmpegInstallPath;
    private File srcFile;
    private File destFile;
    private List<long[]> mNewSeeks;
    private List<String> mMergeList=new ArrayList<String>();
    public TrimTask(Context context, String mFfmpegInstallPath, File srcFile,File destFile,List<long[]> mNewSeeks, CompletionListener completionListener) {
        this.context = context;
        this.mFfmpegInstallPath = mFfmpegInstallPath;
//        this.videoStitchingRequest = stitchingRequest;
        this.completionListener = completionListener;
        this.srcFile=srcFile;
        this.destFile=destFile;
        this.mNewSeeks=mNewSeeks;
        
    
    }


    @Override
    public void run() {
    	trimVideo(context, mFfmpegInstallPath, completionListener, mNewSeeks, srcFile, destFile);
    }

    /**
     * @param context
     * @param mFfmpegInstallPath
     * @param videoStitchingRequest
     * @param completionListener
     */
    private void trimVideo(Context context, String mFfmpegInstallPath,  final CompletionListener completionListener,List<long[]> mNewSeeks,File srcFile,File destFile) {

    	mMergeList.clear();
    	for(int i=0;i<mNewSeeks.size();i++){
    		
    		File myMovie = new File(destFile, String.format("0cut_output-%s.mp4", System.currentTimeMillis()+"")); 
			mMergeList.add(myMovie.getAbsolutePath());
			String startTime=TimeFormatUtils.formatLongToTimeStr((int)mNewSeeks.get(i)[0]);
			String durationTime=TimeFormatUtils.formatLongToTimeStr((int)(mNewSeeks.get(i)[1]-mNewSeeks.get(i)[0]));
//			ffmpeg -ss 00:00:20 -t 00:00:10 -i D:/MyVideo.mpg -vcodec copy -acopy copy D:/Split.mpg
//			String [] ffmpegTrimCommand={mFfmpegInstallPath,"-ss" ,startTime, "-t",durationTime,  "-i" ,srcFile.getAbsolutePath(), "-vcodec","copy" , "-acopy","copy",myMovie.getAbsolutePath(),"-y"};
			
			//切割的命令行
			String [] ffmpegTrimCommand={mFfmpegInstallPath,"-i" ,srcFile.getAbsolutePath(),"-vcodec", "copy", "-acodec", "copy","-ss",startTime ,"-t" ,  durationTime,myMovie.getAbsolutePath(),"-y"};
			try {
                Process ffmpegProcess = new ProcessBuilder(ffmpegTrimCommand)
                        .redirectErrorStream(true).start();

                String line;

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(ffmpegProcess.getInputStream()));
                Log.e("***", "*******Starting FFMPEG");
                while ((line = reader.readLine()) != null) {
                    Log.e("***", "***" + line + "***");
                }
                Log.e(null, "****ending FFMPEG****");
                ffmpegProcess.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}

    	//裁剪完成回调接口（MergeList为裁剪后短视频的路径集合）
          completionListener.onProcessCompleted("Video trim Comleted",mMergeList);

    }
    
    
    /**
	 * 将播放的进度转换成类似00:00:01.000”这种形
	 * @param progressPosition
	 * @return
	 */
	public static  String convertToStr(int progressPosition){
		//先求出剩余的余数的毫
		int remain=progressPosition%1000;
		//求出整数的秒数（可能大于60秒，如果大于60秒则要转换为分钟
		int seconds=progressPosition/1000;
		int minutes=seconds/60;
		//求余数求出剩余的秒数
		int remainSecond=seconds%60;
		String millSeconds=remain+"";
		if(remain<10){
			millSeconds="00"+millSeconds;
		}else if(remain<100){
			millSeconds="0"+millSeconds;
		}
		String second=remainSecond+"";
		if(remainSecond<10){
			second="0"+second;
		}
		String minute=minutes+"";
		if(minutes<10){
			minute="0"+minute;
		}
		return "00:"+minute+":"+second+"."+millSeconds;
	}

    
    
    
    
}
