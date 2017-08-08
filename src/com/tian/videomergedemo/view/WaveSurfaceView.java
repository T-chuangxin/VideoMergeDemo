package com.tian.videomergedemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 该类只是一个初始化surfaceview的封装
 * @author  tcx
 */
public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	private SurfaceHolder holder;
	private int line_off;//上下边距距离
	

    public int getLine_off() {
		return line_off;
	}


	public void setLine_off(int line_off) {
		this.line_off = line_off;
	}


	public WaveSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.holder = getHolder();
		holder.addCallback(this);
		
	}


    /**
     * @author tcx
     * init surfaceview
     */
    public  void initSurfaceView( final SurfaceView sfv){
    	new Thread(){
    		public void run() {
    			 Canvas canvas = sfv.getHolder().lockCanvas(  
    	                 new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));// 关键:获取画布  
    	         if(canvas==null){
    	        	 return;
    	         }
    	         //canvas.drawColor(Color.rgb(241, 241, 241));// 清除背景  
    	         canvas.drawARGB(255, 42, 53, 82);
    	        
				int height = sfv.getHeight()-line_off;
    	         Paint paintLine =new Paint();
    	         Paint centerLine =new Paint();
    	         Paint circlePaint = new Paint();
    	         circlePaint.setColor(Color.rgb(246, 131, 126));
    	         paintLine.setColor(Color.rgb(255, 255, 255));
    	         paintLine.setStrokeWidth(2);
    	         circlePaint.setAntiAlias(true);
    	         
    	         canvas.drawLine(sfv.getWidth()/2, 0, sfv.getWidth()/2, sfv.getHeight(), circlePaint);//垂直的线
    	         centerLine.setColor(Color.rgb(39, 199, 175));
    	         canvas.drawLine(0, line_off/2, sfv.getWidth(), line_off/2, paintLine);//最上面的那根线
    	         canvas.drawLine(0, sfv.getHeight()-line_off/2-1, sfv.getWidth(), sfv.getHeight()-line_off/2-1, paintLine);//最下面的那根线  
    	         canvas.drawLine(0, height*0.5f+line_off/2, sfv.getWidth() ,height*0.5f+line_off/2, centerLine);//中心线
    	         sfv.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    		};
    	}.start();
    	
    }


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initSurfaceView(this);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}

	

}
