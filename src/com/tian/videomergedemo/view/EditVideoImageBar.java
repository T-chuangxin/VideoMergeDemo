package com.tian.videomergedemo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * 裁剪条
 * @author Administrator
 *
 */
public class EditVideoImageBar extends ImageView {
    private List<Float> cutPoint=new ArrayList<Float>();
    private Map<Float ,float[]> selectAreas=new HashMap<Float, float[]>();
    
    private List<float[]> selectPoints=new ArrayList<float[]>();


	private Paint cutPaint;


	private Paint mSelectedPaint;
	
	

	public EditVideoImageBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		init();
		
	}

	

	public EditVideoImageBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EditVideoImageBar(Context context) {
		super(context);
		init();
	}
	
	/**
	 * 初始化 分割线画笔
	 */
	private void init() {
		
		//分割线画笔
        cutPaint = new Paint();//切割线一
        cutPaint.setColor(Color.rgb(0, 0, 0));
        cutPaint.setStrokeWidth(6);
        cutPaint.setAntiAlias(true);
		
        
        
        //选中区域画笔
        mSelectedPaint = new Paint();//选择区域
        mSelectedPaint.setStrokeWidth(6);
        mSelectedPaint.setStyle(Style.STROKE);
        mSelectedPaint.setAntiAlias(false);
        mSelectedPaint.setColor(Color.RED);
	}
	
	
	private Handler mHandler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 1:
				invalidate();
				break;

			default:
				break;
			}
			
			
		};
		
	};
	
	
	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
		
		for(int i=0;i<cutPoint.size();i++){
        	canvas.drawLine(cutPoint.get(i), 0, cutPoint.get(i), measuredHeight, cutPaint);
//			canvas.drawRect(cutPoint.get(i), 0, cutPoint.get(i)+SPACING, measuredHeight, cutPaint);
        }
        
        //选中区域选择
        for(int j=0;j<cutPoint.size();j++){
        		if(j==0&&touchX!=0){
        			if(touchX<cutPoint.get(j)){
        				float[] cutPostion=new float[2];
        				if(selectAreas.containsKey(0.0F)){
        					selectAreas.remove(0.0F);
        					
        				}else{
            				cutPostion[0]=0;
            				cutPostion[1]=cutPoint.get(j);
            				selectAreas.put(0.0F, cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		} 
        		if(j==cutPoint.size()-1){
        			if(touchX>cutPoint.get(j)){
        				float[] cutPostion=new float[2];
        				if(selectAreas.containsKey(cutPoint.get(j))){
        					selectAreas.remove(cutPoint.get(j));
        					
        				}else{
            				cutPostion[0]=cutPoint.get(j);
            				cutPostion[1]=getWidth();
            				selectAreas.put(cutPoint.get(j), cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		}
        		
        		if(j<cutPoint.size()){
        			if(touchX>cutPoint.get(j)&&touchX<cutPoint.get(j+1)&&touchX!=0){
        				float[] cutPostion=new float[2];
        				if(selectAreas.containsKey(cutPoint.get(j))){
        					selectAreas.remove(cutPoint.get(j));
        				}else{
//        					canvas.drawRect(cutPoint.get(j), 0.0F, cutPoint.get(j+1),
//            	        			measuredHeight, mSelectedPaint);
            				cutPostion[0]=cutPoint.get(j);
            				cutPostion[1]=cutPoint.get(j+1);
            				selectAreas.put(cutPoint.get(j), cutPostion);
        				}
        				touchX=0;
        				break;
                	}
        		}
        	}
        
        
        
        selectPoints.clear();//每次进行刷新的操作，重新填充集合
        Iterator<Float> iterator = selectAreas.keySet().iterator();
        while (iterator.hasNext()) {
        	//获取key值
        	Float next = iterator.next();
        	float[] fs = selectAreas.get(next);
        	canvas.drawRect(fs[0], 0.0F, fs[1],
        			measuredHeight, mSelectedPaint);
        }
		
	}
	
	
	
	
	
	 /**
     * 设置剪辑位置
     * @param position
     */
    public void setCutPostion(int position){
    	touchX=0;
    	int temp=cutPoint.size();
    	for(int i=0;i<cutPoint.size();i++){
    		if(cutPoint.get(i)>position){
    			temp=i;
    			break;
    		}
    	}
    	cutPoint.add(temp,(float) position);
    	invalidate();
//    	mHandler.sendEmptyMessageDelayed(1, 100);
    
    }
	
    
    private float touchX=0;
	private float touchX1;
	
	
	
	/**
	 * 返回分割点的集合
	 * @return
	 */
	public List<float[]> getCutPostion(){
		
		float[] tempArray=new float[selectAreas.size()];
		
		 Iterator<Float> iterator = selectAreas.keySet().iterator();
		 int flag=0;
	     while (iterator.hasNext()) {
	        	//获取key值
	        	Float next = iterator.next();
	        	tempArray[flag]=next;
	        	flag=flag+1;
	        }
	        for(int i=0;i<tempArray.length;i++){
	        	for(int j=0;j<tempArray.length;j++){
	        		if(tempArray[i]<tempArray[j]){
	        			float temp=0;
	        			temp=tempArray[j];
	        			tempArray[j]=tempArray[i];
	        			tempArray[i]=temp;
	        		}
	        	}
	        }
	        for(int i=0;i<tempArray.length;i++){
	        	selectPoints.add(selectAreas.get(tempArray[i]));
	        }
		return selectPoints;
	}
	
    
	/**
	 * 显示选中的区域
	 * @param isFling
	 */
	public void showSelectArea(boolean isFling){
		if(!isFling){
			//非滑动状态
			touchX=touchX1;
			invalidate();
		}
	}
    
	
	
	/**
	 * 返回选择区域的坐标
	 * @return
	 */
	public float getSelcetPoint(){
		return touchX;
	}
	
	/**
	 * 清除选中的断点位置，以及选中的区域
	 */
	public void clearPosition(){
		cutPoint.clear();
		touchX=0;
		//TODO
		selectAreas.clear();
		selectPoints.clear();
		invalidate();//重新绘制界面
	}
	
    
    /**
     * 进行控件的触摸事件处理（记录触摸的坐标）
     */
    @SuppressLint("ClickableViewAccessibility") 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchX1=event.getX();
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;

		default:
			break;
		}
    	
    	return super.onTouchEvent(event);
    }
    
    
	
	

}
