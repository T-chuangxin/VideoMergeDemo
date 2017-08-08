package com.tian.videomergedemo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class DensityUtil {

	private static float mPixels = 0.0F;
	private static float density = -1.0F;

	/**
	 * 
	 * @param context
	 * @param pixels 
	 * @return
	 */
	public static int getDisplayMetrics(Context context, float pixels) {
		if (mPixels == 0.0F)
			mPixels = context.getResources().getDisplayMetrics().density;
		return (int) (0.5F + pixels * mPixels);
	}
	
	
	public static int getImageWeidth(Context context , float pixels) {
//		LogUtil.e("screen width " + context.getResources().getDisplayMetrics().widthPixels);
		return context.getResources().getDisplayMetrics().widthPixels - 66 - getDisplayMetrics(context, pixels);
	}

	/**
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int)(pxValue / scale + 0.5f);

	}

	/**
	 * dip
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue){
		final float scale = Resources.getSystem().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);

	}
	
	/**
	 * @param context
	 * @param height
	 * @return
	 */
	public static int getMetricsDensity(Context context , float height) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return Math.round(height * localDisplayMetrics.densityDpi / 160.0F);
	}
	

	public static final float getWidthInPx(Context context) {
		final float width = context.getResources().getDisplayMetrics().widthPixels;
		return width;
	}
	
	
}

