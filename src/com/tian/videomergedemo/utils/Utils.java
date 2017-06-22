package com.tian.videomergedemo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by bruce on 14-11-6.
 */
public final class Utils {
    
    private Utils() {
    }
    
    
	 /** 
     *dip转换px
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    /** 
     *dip转换px,传入的dp值是double类型�?
     */  
    public static int dip2px(Context context,double dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
  
    /** 
     *px转换dip
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    
    /** 
     *dp转换px
     */ 
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    /** 
     *sp转换px
     */ 
    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
    
    @SuppressLint("NewApi")
	public static void scaleX(View view, float f) {
        if (view == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setScaleX(f);
        } else {
            ScaleAnimation animation =new ScaleAnimation(f, f, f, f, 
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }
    }
    
    @SuppressLint("NewApi")
	public static void scaleY(View view, float f) {
        if (view == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setScaleY(f);
        } else {
            ScaleAnimation animation =new ScaleAnimation(f, f, f, f, 
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }
    }
    /**
     * 验证手机号码
     * @param mobiles
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber){
      boolean flag = false;
      try{
          Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
          Matcher matcher = regex.matcher(mobileNumber);
          flag = matcher.matches();
        }catch(Exception e){
          flag = false;
        }
      return flag;
    }
    	 
	public static String getFlVideoPath() {
		String path = Environment.getExternalStorageDirectory()+ "/CB";
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		return filePath.getPath();
	}
    
    public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉�??”符
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	} 
    
    private static final String TAG = "Utils"; 
	private static final int IO_BUFFER_SIZE = 32256;

	public static final String SHELL_CMD_CHMOD = "chmod";
	public static final int CHMOD_EXEC_VALUE = 700;

	public static void doChmod(File file, int chmodValue) {
		final StringBuilder sb = new StringBuilder();
		sb.append(SHELL_CMD_CHMOD);
		sb.append(' ');
		sb.append(chmodValue);
		sb.append(' ');
		sb.append(file.getAbsolutePath());

		try {
			Runtime.getRuntime().exec(sb.toString());
		} catch (IOException e) {
			Log.e(TAG, "Error performing chmod", e);
		}
	}

	public static void installBinaryFromRaw(Context context, int resId,
			File file) {
		final InputStream rawStream = context.getResources().openRawResource(
				resId);
		final OutputStream binStream = getFileOutputStream(file);

		if (rawStream != null && binStream != null) {
			pipeStreams(rawStream, binStream);

			try {
				rawStream.close();
				binStream.close();
			} catch (IOException e) {
				Log.e(TAG, "Failed to close streams!", e);
			}

			// doChmod(file, CHMOD_EXEC_VALUE);
		}
	}

	public static void checkFilePerms(File file) {
		try {
			Process proc = Runtime.getRuntime()
					.exec("ls -l " + file.toString());
			logInputStream(proc.getInputStream());
		} catch (IOException e) {
			Log.e(TAG, "Error checking file permissions.", e);
		}
	}

	public static void logInputStream(InputStream stream) {
		StringBuilder sb = new StringBuilder();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			Log.e(TAG, "Error reading inputstream.", e);
		}

		Log.d(TAG, sb.toString());
	}

	public static OutputStream getFileOutputStream(File file) {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "File not found attempting to stream file.", e);
		}
		return null;
	}

	public static void pipeStreams(InputStream is, OutputStream os) {
		byte[] buffer = new byte[IO_BUFFER_SIZE];
		int count;
		try {
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG, "Error writing stream.", e);
		}
	}
}
