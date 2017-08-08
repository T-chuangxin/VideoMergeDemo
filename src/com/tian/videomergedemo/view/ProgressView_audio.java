package com.tian.videomergedemo.view;

import java.util.ArrayList;
import java.util.List;

import com.tian.videomergedemo.R;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Progress bar on the top of screen
 * 
 * @author xiaodong
 * 
 */
public class ProgressView_audio extends View {
	/**最大的可以录制的时长*/
	private int maxRecordTime;
	/**是否处于编辑删除状态，如果为true，最后一段高亮显示*/
	private boolean isEditing=false;
	/**存储断点百分比的集合*/
	private List<Float> pausePoints;
	private ArrayList<Float> tipPoints;
	/**用于存储断点拍摄的点的进度值*/
	private ArrayList<Integer> tips;
	/**用于存储打标记的点的进度值*/
	private ArrayList<Integer> flags;
	/**断点分隔线的宽度*/
	private static final int DIVIDER_WIDTH=2;
	private final Paint mPaint = new Paint();
	private final Paint editPaint = new Paint();
	private final Paint mPausePaint = new Paint();
	private final Paint mTipPaint = new Paint();
	private int shouldBeWidth = 0;
	
	public int getMaxRecordTime() {
		return maxRecordTime;
	}

	public void setMaxRecordTime(int maxRecordTime) {
		this.maxRecordTime = maxRecordTime;
	}

	public ArrayList<Integer> getTips() {
		return tips;
	}

	public void setTips(ArrayList<Integer> tips) {
		this.tips = tips;
	}

	public ArrayList<Integer> getFlags() {
		return flags;
	}

	public void setFlags(ArrayList<Integer> flags) {
		this.flags = flags;
	}

	public void setWidth(int width) {
		shouldBeWidth = width;
		invalidate();
	}

	public ProgressView_audio(Context context) {
		super(context);
		init();
	}

	public ProgressView_audio(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		init();
	}

	public ProgressView_audio(Context context, AttributeSet paramAttributeSet,
			int paramInt) {
		super(context, paramAttributeSet, paramInt);
		init();
	}

	private void init() {
		this.mPaint.setStyle(Paint.Style.FILL);
		this.mPaint.setColor(getResources().getColor(R.color.vine_green));
		
		this.mPausePaint.setStyle(Paint.Style.FILL);
		this.mPausePaint.setColor(getResources().getColor(R.color.progress_divider_color));
		
		this.mTipPaint.setStyle(Paint.Style.FILL);
		this.mTipPaint.setColor(getResources().getColor(R.color.vine_green_1));
		
		this.editPaint.setStyle(Paint.Style.FILL);
		this.editPaint.setColor(getResources().getColor(R.color.edit_color));
		pausePoints=new ArrayList<Float>();
		tipPoints=new ArrayList<Float>();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
			canvas.drawRect(0, 0.0F, getMeasuredWidth(),
					getMeasuredHeight(), mPaint);
		
			
		if(pausePoints!=null&&!pausePoints.isEmpty()){
			for (Float pause : pausePoints) {
				canvas.drawRect(pause, 0.0F, pause+DIVIDER_WIDTH,
						getMeasuredHeight(), mTipPaint);
			}
		}
	}

	
	public ArrayList<Float> getTipPoints() {
		return tipPoints;
	}

	public void setTipPoints(ArrayList<Float> tipPoints) {
		this.tipPoints = tipPoints;
	}

	public void clearPausePoints(){
		if(pausePoints!=null){
			pausePoints.clear();
		}
		invalidate();
	}
	
	/**
	 * 暂停断点显示
	 * @param pausePoint
	 */
	public void addPausePoint(Float pausePoint){
		
		if(pausePoints!=null&&pausePoint!=0){
			pausePoints.add(pausePoint);
		}
		invalidate();
	}
	
	
	
	/**
	 * 获取标记点
	 * @return
	 */
	public List<Float> getPausePoint(){
		return pausePoints;
	}
	
	/**
	 * 重新设置标记点
	 * @return
	 */
	public void setPausePoint(List<Float> pausePoints ){
		this.pausePoints=pausePoints;
		invalidate();
	}
	
	
	/**
	 * 将打标记的时刻的播放进度进行存储
	 * @param progress
	 */
	public void addTipProgress(int progress){
		if(tips==null){
			tips=new ArrayList<Integer>();
		}
		tips.add(progress);
	}
	public void addFlagProgress(int progress){
		if(flags==null){
			flags=new ArrayList<Integer>();
		}
		flags.add(progress);
	}
	/**
	 * tip断点显示
	 * @param pausePoint
	 */
	public void addTipPoint(Float tipPoint){
		if(tipPoints!=null){
			tipPoints.add(tipPoint);
		}
	}
	/**
	 * tip断点清除
	 * @param pausePoint
	 */
	public void clearTipPoints(){
		if(tipPoints!=null){
			tipPoints.clear();
		}
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	public boolean isEditing() {
		return isEditing;
	}
	/**移除最后一个分段视频的记录的断点*/
	public void removeLastPausePoint(){
		if(pausePoints!=null&&!pausePoints.isEmpty()){
			pausePoints.remove(pausePoints.size()-1);
		}
	}

	/**
	 * 做一些清空的操作
	 */
	public void doClear(){
		if(tips!=null){
			tips.clear();
			tips=null;
		}
		if(flags!=null){
			flags.clear();
			flags=null;
			
		}
		if(pausePoints!=null){
			pausePoints.clear();
			pausePoints=null;
		}
		if(tipPoints!=null){
			tipPoints.clear();
			tipPoints=null;
		}
	}
	
	
	
}
