package com.tian.videomergedemo.manager;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;

import com.tian.videomergedemo.R;
import com.tian.videomergedemo.inter.CompletionListener;
import com.tian.videomergedemo.task.StitchingTask;
import com.tian.videomergedemo.task.TrimTask;
import com.tian.videomergedemo.utils.Utils;


/**
 * Created by TCX on 21/01/17.
 * 
 * 合并和切割的操做（如果编码的话会很耗时，所以用线程池进行管理控制）
 * 
 * 
 */
public class FfmpegManager {

    private static FfmpegManager manager;

    private String mFfmpegInstallPath;

    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    // 线程队列
    private final BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
    private static final int KEEP_ALIVE_TIME = 1;
    // 线程池设置
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // 线程池管理
    ThreadPoolExecutor mDecodeThreadPool = new ThreadPoolExecutor(
            NUMBER_OF_CORES,       // 初始化线程
            NUMBER_OF_CORES,       // 最大线程数
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            mDecodeWorkQueue);

    private FfmpegManager() {

    }
    public synchronized static FfmpegManager getInstance() {

        if (manager == null) {
            manager = new FfmpegManager();

        }
        return manager;
    }




    /**
     * 合并操作
     * @param context
     * @param videoStitchingRequest
     * @param completionListener
     */
    public void stitchVideos(Context context, VideoStitchingRequest videoStitchingRequest, CompletionListener completionListener) {
        installFfmpeg(context);
        StitchingTask stitchingTask = new StitchingTask(context, mFfmpegInstallPath, videoStitchingRequest, completionListener);
        mDecodeThreadPool.execute(stitchingTask);
    }
    
    //切割操作
    public void trimVideo(Context context,File srcFile,File destFile,List<long[]> mNewSeeks,CompletionListener completionListener){
    	 installFfmpeg(context);
    	 TrimTask trimTask=new TrimTask(context, mFfmpegInstallPath, srcFile, destFile,mNewSeeks , completionListener);
    	 mDecodeThreadPool.execute(trimTask);
    }

    /*
    * 插入FFmpeg的路径（这里我保存在资源文件下的raw文件夹下）
    */
    @SuppressLint("NewApi") private void installFfmpeg(Context context) {

        String arch = System.getProperty("os.arch");//获取CPU的架构类型
        String arc = arch.substring(0, 3).toUpperCase();
        String rarc = "";
        int rawFileId;
        if (arc.equals("ARM")) {//arm架构
            rawFileId = R.raw.ffmpeg;
        } else if (arc.equals("MIP")) {
            rawFileId = R.raw.ffmpeg;
        } else if (arc.equals("X86")) {//x86架构
            rawFileId = R.raw.ffmpeg_x86;
        } else {
            rawFileId = R.raw.ffmpeg;
        }

        File ffmpegFile = new File(context.getCacheDir(), "ffmpeg");
        mFfmpegInstallPath = ffmpegFile.toString();
        Utils.installBinaryFromRaw(context, rawFileId, ffmpegFile);
        ffmpegFile.setExecutable(true);//对操作者的执行权限
    }

}
