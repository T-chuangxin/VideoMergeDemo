package com.tian.videomergedemo.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tian.videomergedemo.R;

/**
 * Progress bar on the top of screen
 * 
 * @author xiaodong
 * 
 */
public class ProgressView extends View {
	/**最大的可以录制的时长*/
	private int maxRecordTime;
	/**是否处于编辑删除状态，如果为true，最后一段高亮显示*/
	private boolean isEditing=false;
	/**存储断点百分比的集合*/
	private ArrayList<Float> pausePoints;
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
	}

	public ProgressView(Context context) {
		super(context);
		init();
	}

	public ProgressView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		init();
	}

	public ProgressView(Context context, AttributeSet paramAttributeSet,
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
		this.mTipPaint.setColor(Color.RED);
		
		this.editPaint.setStyle(Paint.Style.FILL);
		this.editPaint.setColor(getResources().getColor(R.color.edit_color));
		pausePoints=new ArrayList<Float>();
		tipPoints=new ArrayList<Float>();
	/*	pausePoints.add(0.33f);
		pausePoints.add(0.5f);
		pausePoints.add(0.75f);*/
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.shouldBeWidth > 0) {
			canvas.drawRect(0.0F, 0.0F, this.shouldBeWidth,
					getMeasuredHeight(), mPaint);
		}
		if(pausePoints!=null&&!pausePoints.isEmpty()){//画出断点分隔线
			for (Float pause : pausePoints) {
				
				canvas.drawRect(getMeasuredWidth()*pause, 0.0F, getMeasuredWidth()*pause+DIVIDER_WIDTH,
						getMeasuredHeight(), mPausePaint);
			} 
		}
//		for (int i = 0; i < array.length; i++) {
//			
//		}
		//绘制最后一段处于编辑状态高亮显示的部分
		if(isEditing&&pausePoints!=null&&!pausePoints.isEmpty()){
			if(pausePoints.size()==1){//如果只有一个断点
				
				canvas.drawRect(0, 0.0F, getMeasuredWidth()*pausePoints.get(0),
						getMeasuredHeight(), editPaint);
			}else{
				//倒数第二个断点
				Float lastSecond = pausePoints.get(pausePoints.size()-2);
				//最后一个断点
				Float lastPause = pausePoints.get(pausePoints.size()-1);
				canvas.drawRect(getMeasuredWidth()*lastSecond, 0.0F, getMeasuredWidth()*lastPause,
						getMeasuredHeight(), editPaint);
			}
//			Float lastSecond = pausePoints.get(pausePoints.size()-1);
			
		}
		if(tipPoints!=null&&!tipPoints.isEmpty()){
			for (Float tip : tipPoints) {
				System.out.println(tip+"<----------------------");
				canvas.drawRect(getMeasuredWidth()*tip, 0.0F, getMeasuredWidth()*tip+DIVIDER_WIDTH,
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
		shouldBeWidth=0;
		invalidate();
	}
	
	/**
	 * 暂停断点显示
	 * @param pausePoint
	 */
	public void addPausePoint(Float pausePoint){
		if(pausePoints!=null){
			pausePoints.add(pausePoint);
		}
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

	public ArrayList<Float> getPausePoints() {
		return pausePoints;
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
