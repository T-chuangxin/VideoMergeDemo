package com.tian.videomergedemo.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tian.videomergedemo.R;
/**
 * 视频编辑界面显示进度的进度条
 * @author howie
 *
 */
public class VideoEditProgressBar extends View {
	private Paint progressPaint;
	private float currentProgress;
	private MarkIndexListener mListener;
	/**用于记录标记点的集合*/
	private ArrayList<Float> markList=new ArrayList<Float>();
	/**绘制标记上的数字的画笔*/
	private Paint markTextPaint;
	private Bitmap markIcon;
	private Rect srcRect;
	private Rect destRect;
	private Paint bottomHalfPaint;
	private Paint darkPaint;//绘制深色分隔线的画笔
	/**控件下半部分的颜色背景*/
	private Rect bottomHalfBgRect;
	private int bitWidth;
	private int bitHeight;
	private Paint paint;
	private int width;
	private int height;
	public ArrayList<Float> getMarkList() {
		return markList;
	}
	public void setMarkList(ArrayList<Float> markList) {
		this.markList = markList;
	}
	
	
	
	/**
	 * 清除标记
	 */
	public void clearPoint(){
		if(markList!=null&&!"".equals(markList)){
			markList.clear();
		}
		currentProgress=0;//进度清0
		invalidate();
	}
	
	
	
	
	
	public VideoEditProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
		progressPaint=new Paint();
		progressPaint.setColor(getResources().getColor(R.color.vine_green));
		paint=new Paint();
		
		
		bottomHalfPaint=new Paint();
		darkPaint=new Paint();
		
		
		darkPaint.setColor(getResources().getColor(R.color.dark_black));
		bottomHalfPaint.setColor(getResources().getColor(R.color.bottomBg));
		
		
		markTextPaint=new Paint();
		markTextPaint.setColor(getResources().getColor(R.color.hui));
		markTextPaint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		markIcon=((BitmapDrawable)getResources().getDrawable(R.drawable.edit_mark)).getBitmap();
		bitWidth = markIcon.getWidth();
		bitHeight = markIcon.getHeight();
//		initMarks();
	
	}
	public void initMarks() {
	}
	public void setPausePoints(ArrayList<Float> points){
		markList=points;
		invalidate();  
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		height =getMeasuredHeight();
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(srcRect==null){
			srcRect=new Rect(0, 0, bitWidth, bitHeight);
		}
		if(destRect==null){
			destRect=new Rect((width-bitWidth/2)/2, 0, (width-bitWidth/2)/2+bitWidth/2, bitHeight/2);
			
		}
		if(bottomHalfBgRect==null){
			bottomHalfBgRect=new Rect(0, height-20, width, height);
		}
		
		canvas.drawRect(bottomHalfBgRect, bottomHalfPaint);//绘制下半部分的背景色
		
		if(currentProgress>0){
//			canvas.drawRect(r, bottomHalfPaint);
			canvas.drawRect(0, height-20, currentProgress*width, height, progressPaint);
		}
		if(markList!=null&&!markList.isEmpty()){
			for (int i = 0; i < markList.size(); i++) {
				Float float1 = markList.get(i);
				int left=(int) (width*float1);
//				destRect=new Rect((width-bitWidth/2)/2, 0, (width-bitWidth/2)/2+bitWidth/2, bitHeight/2);
				destRect=new Rect((left-bitWidth/4), 0, (left-bitWidth/4)+bitWidth/2, bitHeight/2);
				canvas.drawBitmap(markIcon, srcRect, destRect, paint);//绘制标记的轮廓
				
				String text=(i+1)+"";
				float textWidth = markTextPaint.measureText(text);
				FontMetricsInt fontMetricsInt = markTextPaint.getFontMetricsInt();
				
				int fontHeight=fontMetricsInt.bottom-fontMetricsInt.top;
				
				canvas.drawText(text, (left-textWidth/2), fontHeight-8, markTextPaint);//这里减去8是为了微调数字显示的垂直方向的位置
				if(float1<currentProgress){//如果此标记代表的进度小于当前进度，就用深色画笔去画分隔线
					canvas.drawLine(left, height-20, left, height,bottomHalfPaint );
				}else{//如果此标记代表的进度大于当前进度，就用浅色画笔去画分隔线
					canvas.drawLine(left, height-20, left, height,progressPaint );
				}
			}
		}
	}

	public void setProgress(Float progress){
		currentProgress=progress;
		invalidate();
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(markList!=null&&!markList.isEmpty()){
				float x2 = event.getX();
				for (int i = 0; i < markList.size(); i++) {
					
					Float float1 = markList.get(i);
					if(Math.abs(float1*width-x2)<10&&mListener!=null){//点击的x坐标误差在10像素以内视为点击
						mListener.indexClick(i);
						break;
					}
				}
				break;
			}
			}
			
		return super.onTouchEvent(event);
	}
	/**
	 * 回调点击的标记的索引的监听
	 * @author howie
	 *
	 */
	public interface MarkIndexListener{
		/**
		 * 被点击的标记的索引
		 * @param index
		 */
		void indexClick  (int index);
	}
	public void setIndexClickListener(MarkIndexListener markIndexListener){
		mListener=markIndexListener;
	}

}
