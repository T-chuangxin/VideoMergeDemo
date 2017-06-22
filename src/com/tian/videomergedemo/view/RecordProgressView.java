package com.tian.videomergedemo.view;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.tian.videomergedemo.R;
import com.tian.videomergedemo.RecordActivity;

/**
 * record progress on 17/5/12.
 */

public class RecordProgressView extends View implements RecordProgressController.RecordingStateChanged{
    private static final String TAG = "RecordProgressView";

    private int paddingTop = 0;
    private int paddingBottom = 0;
    private Paint mBkgPaint;
    private Paint mPaint;
    private Paint mMinPaint;
    private Paint mPausedPaint;
    private int mMinPoint;
    private Handler mHandler;
    private boolean mFlag;
    private Bitmap mCursorBitmap;
    private List<Integer> mFlagPointer=new ArrayList<Integer>();

    private LinkedList<RecordClipModel> mProgressClipList;
    public int mTotalWidth;
    private Paint mPendingPaint;
    public int mScreenWidth;

    private boolean mIsRecording;
    private int mMaxDuration=RecordActivity.MAX_DURATION;//默认视频录制最大时长

	private Paint mFlagPaint;

    public RecordProgressView(Context context) {
        super(context);
        instantiate(context);
    }

    public RecordProgressView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        instantiate(context);
    }

    public RecordProgressView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        instantiate(context);
    }

    private void instantiate(Context context) {
        mBkgPaint = new Paint();
        mPaint = new Paint();
        mMinPaint = new Paint();
        mPausedPaint = new Paint();
        mFlagPaint = new Paint();
        mPendingPaint = new Paint();
        mTotalWidth = 0;
        mProgressClipList = null;
        mHandler = new Handler();
        mFlag = false;
        Resources res = context.getResources();
        mCursorBitmap = BitmapFactory.decodeResource(res,
                R.drawable.record_progressbar_front);
        this.setBackgroundColor(getResources().getColor(R.color.record_progress_black));
        mBkgPaint.setStyle(android.graphics.Paint.Style.FILL);
        mBkgPaint.setColor(getResources()
                .getColor(R.color.record_progress_bg));
        mPaint.setStyle(android.graphics.Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.record_progress_blue));
        mPausedPaint.setStyle(android.graphics.Paint.Style.FILL);
        mPausedPaint.setColor(getResources().getColor(
                R.color.record_progress_pause));
        //标记点画笔
        mFlagPaint.setStyle(android.graphics.Paint.Style.FILL);
        mFlagPaint.setColor(getResources().getColor(
        		R.color.red));
        
        mMinPaint.setStyle(android.graphics.Paint.Style.FILL);
        mMinPaint.setColor(getResources().getColor(
                R.color.record_progress_pause));
        mPendingPaint.setStyle(android.graphics.Paint.Style.FILL);
        mPendingPaint.setColor(getResources().getColor(
                R.color.record_progress_red));
        mScreenWidth = getScreenWidthPixels(getContext());
        mHandler.postDelayed(mCursorRunnable, 500);//延时500毫秒刷新一次UI
    
        
        
    }

    
    
    /**
     * 设置视频最大录制时长
     */
    public void setVedioMaxDuration(int maxDuration){
    	
    	if(maxDuration==0){
    		mMinPoint = mScreenWidth
                    * RecordActivity.MIN_DURATION
                    / mMaxDuration;
    	}else{
    		mMaxDuration=maxDuration;
    		mMinPoint = mScreenWidth
                    * RecordActivity.MIN_DURATION
                    / maxDuration;
    	}
    }
    
    
    
    /**
     * 设置录制时间标记点位
     * 
     * @param timeFlag
     */
    public void setFlagPointer(int timeFlag){
    	mFlagPointer.add(timeFlag);
    }
    
    
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, paddingTop, getMeasuredWidth(), getMeasuredHeight()
                - paddingBottom, mBkgPaint);//底部背景矩形
        boolean pendingDelete = false;
        
      
        
        if (mProgressClipList != null && !mProgressClipList.isEmpty()) {
            int totalWidth = 0;
            for (RecordClipModel clip : mProgressClipList) {
                long newWidth = totalWidth + (clip.timeInterval * mScreenWidth)
                        / mMaxDuration;
                switch (clip.state) {
                    case 0: // recording
                        canvas.drawRect(totalWidth, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPaint);
                        	
                        break;
                    case 1: // recorded
                        canvas.drawRect(totalWidth, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPaint);
                        canvas.drawRect(newWidth - 2, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPausedPaint);
                        break;
                    case 2: // pending fro delete
                        canvas.drawRect(totalWidth, paddingTop, newWidth,
                                getMeasuredHeight() - paddingBottom, mPendingPaint);
                        pendingDelete = true;
                        break;
                    default:
                        break;
                }
                totalWidth = (int) newWidth;
            }
            mTotalWidth = totalWidth;
        } else {
            mTotalWidth = 0;
        }
        
        
        //临时变量
        List<Integer> mFlagPointer_p=mFlagPointer;
       int totleTime = mTotalWidth * mMaxDuration / mScreenWidth;
       for(int i=0;i<mFlagPointer_p.size();i++){
    	   if(!(mFlagPointer_p.get(i)<=totleTime)){
    		   mFlagPointer.remove(i);
    	   }
       }
       
       
        
        
        if (mTotalWidth < mMinPoint) {
            canvas.drawRect(mMinPoint, paddingTop, mMinPoint + 3,
                    getMeasuredHeight() - paddingBottom, mMinPaint);
        }
        if ((mFlag && !pendingDelete) || mIsRecording) {
            canvas.drawBitmap(mCursorBitmap, null, new Rect(mTotalWidth - 20,
                    paddingTop, mTotalWidth + 12, getMeasuredHeight() - paddingBottom), null);
        }
        
      //画出标记的时间点位置
        for(Integer flag:mFlagPointer){
        	long tempFlagpointer = (flag * mScreenWidth)
                    / mMaxDuration;
        	canvas.drawRect(tempFlagpointer - 2, paddingTop, tempFlagpointer,
                    getMeasuredHeight() - paddingBottom, mFlagPaint);
        }
    }

    @Override
    public void recordingStart(long startTime) {
        mIsRecording = true;
    }

    @Override
    public void recordingStop() {
        mIsRecording = false;
    }

    public void release() {
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mProgressClipList.clear();
    }

    public void setProgressClipList(
            LinkedList<RecordClipModel> clips) {
        mProgressClipList = clips;
    }

    public boolean isPassMinPointQuick() {
        if (mProgressClipList != null && !mProgressClipList.isEmpty()) {
            int totalWidth = 0;
            for (RecordClipModel clip : mProgressClipList) {
                long newWidth = totalWidth + (clip.timeInterval * mScreenWidth)
                        / mMaxDuration;
                totalWidth = (int) newWidth;
            }
            if (totalWidth >= mMinPoint) {
                return true;
            }
        }
        return false;
    }

    public boolean isPassMinPoint() {
        if (mTotalWidth >= mMinPoint) {
            return true;
        }
        return false;
    }

    public boolean isPassMaxPoint() {
        if (mTotalWidth >= mScreenWidth) {
            return true;
        }
        return false;
    }

    private Runnable mCursorRunnable = new Runnable() {
        @Override
        public void run() {
            mFlag = !mFlag;
            mHandler.postDelayed(mCursorRunnable, 500);
            invalidate();
        }
    };

    private int getScreenWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 或取标记点时间
     * @return
     */
	public List<Integer> getFlagPointers() {
		return mFlagPointer;
	}
}
