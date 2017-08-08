package com.tian.videomergedemo.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 
 * @author TCX
 *
 */
public class VideoPicker {

    public static final String TAG = VideoPicker.class.getSimpleName();
    /**
     * 录像的结果请求
     */
    public static final int REQUEST_VIDEO_TAKE = 1001;
    /**
     * 请求视频预览
     */
    public static final int REQUEST_VIDEO_PREVIEW = 1002;
    /**
     * 返回视频数据的结果
     */
    public static final int RESULT_VIDEO_ITEMS = 1003;
    /**
     * 从预览界面返回
     */
    public static final int RESULT_VIDEO_BACK = 1004;

    /**
     * 选中的视频项
     */
    public static final String EXTRA_RESULT_VIDEO_ITEMS = "extra_result_items";
    /**
     * 已选择的视频项
     */
    public static final String EXTRA_SELECTED_VIDEO_POSITION = "selected_video_position";
    /**
     * 已选中的所有视频文件夹项
     */
    public static final String EXTRA_VIDEO_ITEMS = "extra_video_items";

    private boolean multiMode = true;    //图片选择模式
    private int selectLimit = 9;         //最大选择图片数量
    private boolean showCamera = true;   //显示相机
//    private ImageLoader imageLoader;     //图片加载器
    private File takeVideoFile;

    private ArrayList<VideoItem> mSelectedVideos = new ArrayList<VideoItem>();   //选中的图片集合
    private List<VideoFolder> mVideoFolders;      //所有的图片文件夹
    private int mCurrentVideoFolderPosition = 0;  //当前选中的文件夹位置 0表示所有图片
    private List<OnVideoSelectedListener> mVideoSelectedListeners;          // 图片选中的监听回调

    private static VideoPicker mInstance;

    private VideoPicker() {
    }

    public static VideoPicker getInstance() {
        if (mInstance == null) {
            synchronized (VideoPicker.class) {
                if (mInstance == null) {
                    mInstance = new VideoPicker();
                }
            }
        }
        return mInstance;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public File getTakeVideoFile() {
        return takeVideoFile;
    }

//    public ImageLoader getImageLoader() {
//        return imageLoader;
//    }
//
//    public void setImageLoader(ImageLoader imageLoader) {
//        this.imageLoader = imageLoader;
//    }
    public List<VideoFolder> getVideoFolders() {
        return mVideoFolders;
    }

    public void setVideoFolders(List<VideoFolder> videoFolders) {
        mVideoFolders = videoFolders;
    }

    public int getCurrentVideoFolderPosition() {
        return mCurrentVideoFolderPosition;
    }

    /** 设置当前选中视频文件夹所在的位置*/
    public void setCurrentVideoFolderPosition(int mCurrentSelectedVideoSetPosition) {
        mCurrentVideoFolderPosition = mCurrentSelectedVideoSetPosition;
    }
    /** 获取当前视频所在的文件夹 */
    public ArrayList<VideoItem> getCurrentVideoFolderItems() {
        return mVideoFolders.get(mCurrentVideoFolderPosition).videos;
    }
    /** 是否选中该视频 checkbox*/
    public boolean isSelect(VideoItem item) {
        return mSelectedVideos.contains(item);
    }

    public int getSelectVideoCount() {
        if (mSelectedVideos == null) {
            return 0;
        }
        return mSelectedVideos.size();
    }

    public ArrayList<VideoItem> getSelectedVideos() {
        return mSelectedVideos;
    }
    /** 清除选中的视频 */
    public void clearSelectedVideos() {
        if (mSelectedVideos != null) mSelectedVideos.clear();
    }

    public void clear() {
        if (mVideoSelectedListeners != null) {
            mVideoSelectedListeners.clear();
            mVideoSelectedListeners = null;
        }
        if (mVideoFolders != null) {
            mVideoFolders.clear();
            mVideoFolders = null;
        }
        if (mSelectedVideos != null) {
            mSelectedVideos.clear();
        }
        mCurrentVideoFolderPosition = 0;
    }

    /** 录像的方法 */
    public void takeRecord(Activity activity, int requestCode) {
        Intent takeRecordIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        takeRecordIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takeRecordIntent.resolveActivity(activity.getPackageManager()) != null) {
            //是否存在SD卡
            if (Utils.existSDCard())
                takeVideoFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/movie/");
            else takeVideoFile = Environment.getDataDirectory();
            takeVideoFile = createFile(takeVideoFile, "RIDEO_", ".mp4");
            if (takeVideoFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！
                takeRecordIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takeVideoFile));
            }
        }
        takeRecordIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        activity.startActivityForResult(takeRecordIntent, requestCode);
    }

    /** 根据系统时间、前缀、后缀产生一个文件 */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /** 扫描图片 */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /** 视频选中的监听 */
    public interface OnVideoSelectedListener {
        void onVideoSelected(int position, VideoItem item, boolean isAdd);
    }
    /** 添加视频选中的监听 */
    public void addOnVideoSelectedListener(OnVideoSelectedListener l) {
        if (mVideoSelectedListeners == null) mVideoSelectedListeners = new ArrayList<OnVideoSelectedListener>();
        mVideoSelectedListeners.add(l);
    }

    public void removeOnVideoSelectedListener(OnVideoSelectedListener l) {
        if (mVideoSelectedListeners == null) return;
        mVideoSelectedListeners.remove(l);
    }
    /** 添加选中某一项视频 */
    public void addSelectedVideoItem(int position, VideoItem item, boolean isAdd) {
        if (isAdd) mSelectedVideos.add(item);
        else mSelectedVideos.remove(item);
        notifyVideoSelectedChanged(position, item, isAdd);
    }

    private void notifyVideoSelectedChanged(int position, VideoItem item, boolean isAdd) {
        if (mVideoSelectedListeners == null) return;
        for (OnVideoSelectedListener l : mVideoSelectedListeners) {
            l.onVideoSelected(position, item, isAdd);
        }
    }

    /**
     * Android读出的时长为 long 类型以毫秒数为单位，例如：将 234736 转化为分钟和秒应为 03:55 （包含四舍五入）
     * @param duration 时长
     * @return
     */
    public String timeParse(long duration) {
        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second ;
        return time ;
    }
}