package com.tian.videomergedemo.view;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.view.View;

import com.tian.videomergedemo.R;
import com.tian.videomergedemo.RecordActivity;

/**
 * record progress controller
 */

public class RecordProgressController {
    private static final String TAG = "RecordProgressController";
    private Handler mHandler;
    private RecordProgressView mProgressView;
    private ChangeProgressRunnable mProgressRunnable;
    private RecordProgressTimer mProgressTimer;
    private long mStartRecordingTime;
    private boolean mIsRecording;
    private LinkedList<RecordClipModel> mProgressClipList;
    
    private  int MAX_DURATION = 5 * 60 * 1000;

    private List<RecordingStateChanged> mRecordStateChangedListeners;
    private RecordingLengthChangedListener mRecordingLengthChangedListener;

    public RecordProgressController(View rootView) {
        mHandler = new Handler();
        mProgressView = (RecordProgressView) rootView.findViewById(R.id.record_progress);
        mProgressRunnable = new ChangeProgressRunnable();
        mRecordStateChangedListeners = new ArrayList<RecordingStateChanged>();
        mProgressTimer = new RecordProgressTimer();
        mProgressTimer.setProgressUpdateListener(mProgressUpdateListener);
        mRecordStateChangedListeners.add(mProgressTimer);

        mStartRecordingTime = 0;
        mIsRecording = false;
        mProgressClipList = new LinkedList<RecordClipModel>();

        mProgressView.setProgressClipList(mProgressClipList);
        mRecordStateChangedListeners.add(mProgressView);
    }

    
    /**
     * 设置视频的最大录制时间
     */
    public void setMaxDuration(int maxDuration){
    	
    	if(maxDuration!=0&&maxDuration>RecordActivity.MIN_DURATION){
    		MAX_DURATION=maxDuration;
    	}
    	mProgressView.setVedioMaxDuration(MAX_DURATION);
    }
    
    
    
    /**
     * 设置标记点时间戳
     */
    public void setFlagPointer(){
    	mProgressView.setFlagPointer(getRecordedTime());
    }
    
    public List<Integer> getFlagPointers(){
    	return mProgressView.getFlagPointers();
    }
    
    
    private class ChangeProgressRunnable implements Runnable {
        @Override
        public void run() {
            if (mProgressView.mTotalWidth >= mProgressView.mScreenWidth) {
                mProgressView.invalidate();
                if (mIsRecording && mRecordingLengthChangedListener != null) {
                    mRecordingLengthChangedListener.passMaxPoint();
                }
                mIsRecording = false;
            }
            mRecordingLengthChangedListener.passMinPoint(isPassMinPoint());

            mProgressView.invalidate();
        }
    }

    /**
     * 是否到达了最小录制时长
     *
     * @return
     */
    public boolean isPassMinPoint() {
        long recordedTime = 0;
        for (RecordClipModel clip : mProgressClipList) {
            recordedTime += clip.timeInterval;
        }
        return recordedTime >= RecordActivity.MIN_DURATION;
    }

    /**
     * 是否到达了最大录制时长
     *
     * @return
     */
    public boolean isPassMaxPoint() {
        return mProgressView.isPassMaxPoint();
    }

    /**
     * 进入录制页面Timer即可启动，用于随时更新录制的进度
     */
    public void start() {
        mProgressTimer.start();
    }

    public void stop() {
        mProgressTimer.stop();
    }

    /**
     * startRecord
     */
    public void startRecording() {
        if (mIsRecording) return;
        mStartRecordingTime = System.currentTimeMillis();
        mIsRecording = true;
        RecordClipModel clip = new RecordClipModel();
        clip.timeInterval = 0;
        clip.state = 0;
        mProgressClipList.add(clip);

        for (RecordingStateChanged listener : mRecordStateChangedListeners) {
            listener.recordingStart(mStartRecordingTime);
        }
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        mProgressTimer.stop();
        mProgressTimer.setProgressUpdateListener(null);
        mRecordStateChangedListeners.clear();
        mProgressClipList.clear();
        mProgressView.release();
    }

    /**
     * stop record
     */
    public void stopRecording() {
        mIsRecording = false;
        if (!mProgressClipList.isEmpty()) {
            mProgressClipList.getLast().state = 1;
            mProgressClipList.getLast().timeInterval += 20;
            mHandler.post(mProgressRunnable);
        }

        for (RecordingStateChanged listener : mRecordStateChangedListeners) {
            listener.recordingStop();
        }
    }

    /**
     * remove record file
     */
    public void rollback() {
        mIsRecording = false;
        if (!mProgressClipList.isEmpty()) {
            mProgressClipList.removeLast();
            mHandler.post(mProgressRunnable);
        }
    }

    /**
     * 设置最后一个file为待删除文件
     */
    public void setLastClipPending() {
        if (!mProgressClipList.isEmpty()) {
            mProgressClipList.getLast().state = 2;
            mHandler.post(mProgressRunnable);
        }
    }

    /**
     * 设置最后一个file为普通文件
     */
    public void setLastClipNormal() {
        if (!mProgressClipList.isEmpty()) {
            mProgressClipList.getLast().state = 1;
            mHandler.post(mProgressRunnable);
        }
    }

    public int getClipListSize() {
        return mProgressClipList.size();
    }

    /**
     * 只是预估时间，实际录制时长已视频为准
     *
     * @return
     */
    public int getRecordedTime() {
        return mProgressView.mTotalWidth * MAX_DURATION / mProgressView
                .mScreenWidth;
    }

    public boolean getIsRecording() {
        return mIsRecording;
    }

    public long getStartRecordingTime() {
        return mStartRecordingTime;
    }

    public void setRecordingLengthChangedListener(RecordingLengthChangedListener listener) {
        mRecordingLengthChangedListener = listener;
    }

    private RecordProgressTimer.ProgressUpdateListener mProgressUpdateListener = new RecordProgressTimer.ProgressUpdateListener() {
        @Override
        public void updateProgress(long interval) {
            if (!mProgressClipList.isEmpty()) {
                RecordClipModel clip = mProgressClipList.getLast();
                clip.timeInterval = interval;
                mHandler.post(mProgressRunnable);
            }
        }
    };

    public interface RecordingStateChanged {
        void recordingStart(long startTime);

        void recordingStop();
    }

    public interface RecordingLengthChangedListener {
        void passMinPoint(boolean pass);

        void passMaxPoint();
    }
}
