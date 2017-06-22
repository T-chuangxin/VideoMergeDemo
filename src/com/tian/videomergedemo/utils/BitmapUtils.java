package com.tian.videomergedemo.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class BitmapUtils {
	public static File getStorage = Environment.getExternalStorageDirectory();
	public static String imagepath="/HB/images/";
	public static String path=Environment.getExternalStorageDirectory()+"/JWZT_PHOTOS/test/ShuiYin/";
	/**
	 * 转化成BitMap类型
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		//及时回收内存
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 转化成Drawable类型
	 * @param context
	 * @param resId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable readBitMap2Drawable(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;//将减少一半内存的使用
		//让系统几时回收内存资�?
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/***
	 * 图片的缩放方�?
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放�?
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,(int) height, matrix, true);
		return bitmap;
	}
	
    public static Bitmap comp(Bitmap image) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos�? 
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //�?��读入图片，此时把options.inJustDecodeBounds 设回true�? 
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        //现在主流手机比较多是800*480分辨率，�?��高和宽我们设置为  
        float hh = 800f;//这里设置高度�?00f  
        float ww = 480f;//这里设置宽度�?80f  
        //缩放比�?由于是固定比例缩放，只用高或者宽其中�?��数据进行计算即可  
        int be = 1;//be=1表示不缩�? 
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩�? 
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩�? 
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0){
        	be = 1;  
        }
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false�? 
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        
        //此处可以拆分�?��为一个压缩方法，当压缩后的大小小�?00kb时停�?
        ByteArrayOutputStream baoszoom = new ByteArrayOutputStream();  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoszoom);//质量压缩方法，这�?00表示不压缩，把压缩后的数据存放到baos�? 
        int options = 100;  
        while ( baoszoom.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大�?00kb,大于继续压缩         
        	baoszoom.reset();//重置baos即清空baos  
        	bitmap.compress(Bitmap.CompressFormat.JPEG, options, baoszoom);//这里压缩options%，把压缩后的数据存放到baos�? 
            options -= 10;//每次都减�?0  
        }  
        ByteArrayInputStream isBmzoom = new ByteArrayInputStream(baoszoom.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream�? 
        Bitmap bitmapzoom = BitmapFactory.decodeStream(isBmzoom, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmapzoom;  
//        return compressImage(bitmap);//压缩好比例大小后再进行质量压�? 
    }  

	/**
	 * 转换图片成圆�?
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯�?

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画�?drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径�?
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参�?http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

	/**
	 * 将网络图片转成bitmap
	 * 
	 * @param url
	 * @return bitmap type
	 */
	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;

		try {
			myFileUrl = new URL(url);
			HttpURLConnection conn;
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			BitmapFactory.Options opt = new BitmapFactory.Options();
			// 新增加的
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			int scale = 2;
			opt.inSampleSize = scale;
		    bitmap = BitmapFactory.decodeStream(is, null,opt);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 将网络图片转成bitmap
	 * 
	 * @param url
	 * @return bitmap type
	 */
	public static Bitmap returnBitMapImg(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;

		try {
			myFileUrl = new URL(url);
			//HttpURLConnection conn;
			//imgUrl = new URL(urlString);
			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn = (HttpURLConnection) myFileUrl.openConnection();
			urlConn.setDoInput(true);
			urlConn.connect();
			// 将得到的数据转化成InputStream
			InputStream is = urlConn.getInputStream();
			// 将InputStream转换成Bitmap
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			/**/
			/*conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			BitmapFactory.Options opt = new BitmapFactory.Options();
			// 新增加的
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.outHeight=50;
			opt.outWidth=50;
			//opt.inPurgeable = true;
			//opt.inInputShareable = true;
			int scale = 0;
			opt.inSampleSize = scale;
		    bitmap = BitmapFactory.decodeStream(is, null,opt);*/
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 根据路径压缩图片
	 * @param path
	 * @return
	 */
	public static Bitmap yasuoimg(String path){
		Bitmap bitmap=null; 
		try {
			FileInputStream fis = new FileInputStream(path);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			// 新增加的
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			int scale = 2;
			opt.inSampleSize = scale;
		    bitmap = BitmapFactory.decodeStream(fis, null,opt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
		
		return bitmap;
	}
	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static void savePic(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(imagepath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(imagepath + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	public static void saveAdPicFile(Bitmap bm, String fileName)
			throws IOException {
		File dirFile = new File(getStorage+imagepath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(dirFile, fileName + ".jpg");
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}

	public static Bitmap showAdPic(String fileName) {
		Bitmap bitmap = null;
		String file = "";
		file = getStorage+imagepath + fileName + ".jpg";
		bitmap = BitmapFactory.decodeFile(file);
		return bitmap;
	}

	public static void saveImageToGallery(Context context, Bitmap bmp,String url) {
		// 首先保存图片
		File appDir = new File(getStorage+imagepath);
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		File file = new File(appDir, url + ".jpg");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), url, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// �?��通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + file)));
	}
	
	public static void saveImageToGa(Context context, Bitmap bmp,String url) {
		// 首先保存图片
		File appDir = new File(getStorage+imagepath);
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		File file = new File(appDir, url + ".jpg");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	/*	// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), url, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		/*// �?��通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + file)));*/
	}


	public static boolean isPicExist(String fileName) {
		try {
			File f = new File(getStorage+imagepath + fileName + ".jpg");
			if (!f.exists()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 显示图片的一部分
	 * @return
	 */
	public static Bitmap cutPicture(Bitmap bm,int X,int Y,int width,int height){
		return Bitmap.createBitmap(bm, X, Y, width, height);
	}
	

	/**
	 * 保存图片
	 * @param mBitmap
	 * @param f
	 */
	public static void saveMyBitmap(Bitmap mBitmap,File f)  {
//	       File f = new File( Environment.getExternalStorageDirectory()+"/"+bitName + ".jpg");
	       FileOutputStream fOut = null;
	       try {
	               fOut = new FileOutputStream(f);
	       } catch (FileNotFoundException e) {
	               e.printStackTrace();
	       }
	       mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	       try {
	               fOut.flush();
	       } catch (IOException e) {
	               e.printStackTrace();
	       }
	       try {
	               fOut.close();
	       } catch (IOException e) {
	               e.printStackTrace();
	       }
	}
	

	
	/**
	 * 返回虚化过后的Bitmap
	 * @param originBitmap
	 * @param scaleRatio   虚化度设�? 
	 * @return
	 */
	/*public static Bitmap toBlurMap(Bitmap  originBitmap,int scaleRatio){
		Bitmap scaledBitmap, blurBitmap = null;
		  scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
		  originBitmap.getWidth() / 3,
		  originBitmap.getHeight() / 3,
		      false);
		  blurBitmap = FastBlurUtil.doBlur(originBitmap,65, false);
		
		return blurBitmap;
	}*/
	
	/**
	 * 返回虚化过后的Bitmap
	 * @param originBitmap
	 * @param scaleRatio   虚化度设�? 
	 * @return
	 */
	/*public static Bitmap toBlurMapLive(Bitmap  originBitmap,int scaleRatio){
		Bitmap scaledBitmap, blurBitmap = null;
		  scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
		  originBitmap.getWidth() / 2,
		  originBitmap.getHeight() / 2,
		      false);
		  blurBitmap = FastBlurUtil.doBlur(originBitmap,65, false);
		
		return blurBitmap;
	}*/
	
	/**
	 * 根据在sd卡中的路径转换成bitmap
	 * @param path 本地图片路径
	 * @return
	 */
    public static Bitmap getSDCardToBitmap(String path) {  
        if (!new File(path).exists()) {//表示文件不存�?
            return null;  
        }  
        byte[] buf = new byte[1024 * 1024];
        Bitmap bitmap = null;  
        try {  
            FileInputStream fis = new FileInputStream(path);  
//            int len = fis.read(buf, 0, buf.length);  
            BitmapFactory.Options opt = new BitmapFactory.Options();
			// 新增加的
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			int scale = 2;
			opt.inSampleSize = scale;
		    bitmap = BitmapFactory.decodeStream(fis, null,opt);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return bitmap;  
    }  
    
	/**
	 * 文件转移
	 * @param desFile
	 * @param targetFile
	 * @param isZoom
	 */
	public static void transferPicFile(String desFile,String targetFile,boolean isZoom){
		
		if(desFile==null||"".equals(desFile)){
			return;
		}
		String name=targetFile.substring(targetFile.lastIndexOf("/")+1);//文件名称
		String dirs=targetFile.replace(name, "");//获取文件�?
		File file=new File(dirs);
		if(!file.exists()){
			file.mkdirs();
		}
		try {
			Bitmap diskBitmap = getDiskBitmap(desFile);
			if(isZoom){
				//�?��进行放缩
				Bitmap zoomImage = zoomImage(diskBitmap,213,213);
				saveMyBitmap(zoomImage,new File(targetFile));
			}else{
				//不需要进行放�?
				saveMyBitmap(diskBitmap,new File(targetFile));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载本地SD卡图�?
	 * @param pathString
	 * @return
	 */
	public static Bitmap getDiskBitmap(String pathString){  
	    Bitmap bitmap = null;  
	    try{  
	        File file = new File(pathString);  
	        if(file.exists()){  
	            bitmap = BitmapFactory.decodeFile(pathString);  
	        }  
	    } catch (Exception e){  
	    	e.printStackTrace();
	    }  
	    return bitmap;  
	} 
	
    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Bitmap getSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    
    /**
     * Resize a bitmap object to fit the passed width and height
     *
     * @param input
     *           The bitmap to be resized
     * @param destWidth
     *           Desired maximum width of the result bitmap
     * @param destHeight
     *           Desired maximum height of the result bitmap
     * @return A new resized bitmap
     * @throws OutOfMemoryError
     *            if the operation exceeds the available vm memory
     */
    public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight, int rotation ) throws OutOfMemoryError {

        int dstWidth = destWidth;
        int dstHeight = destHeight;
        final int srcWidth = input.getWidth();
        final int srcHeight = input.getHeight();

        if ( rotation == 90 || rotation == 270 ) {
            dstWidth = destHeight;
            dstHeight = destWidth;
        }

        boolean needsResize = false;
        float p;
        if ( ( srcWidth > dstWidth ) || ( srcHeight > dstHeight ) ) {
            needsResize = true;
            if ( ( srcWidth > srcHeight ) && ( srcWidth > dstWidth ) ) {
                p = (float) dstWidth / (float) srcWidth;
                dstHeight = (int) ( srcHeight * p );
            } else {
                p = (float) dstHeight / (float) srcHeight;
                dstWidth = (int) ( srcWidth * p );
            }
        } else {
            dstWidth = srcWidth;
            dstHeight = srcHeight;
        }

        if ( needsResize || rotation != 0 ) {
            Bitmap output;

            if ( rotation == 0 ) {
                output = Bitmap.createScaledBitmap( input, dstWidth, dstHeight, true );
            } else {
                Matrix matrix = new Matrix();
                matrix.postScale( (float) dstWidth / srcWidth, (float) dstHeight / srcHeight );
                matrix.postRotate( rotation );
                output = Bitmap.createBitmap( input, 0, 0, srcWidth, srcHeight, matrix, true );
            }
            return output;
        } else
            return input;
    }
    
    
    
    
    /**
     * 多张图片横向拼接
     * @param picPaths
     * @return
     */
    public static Bitmap addHBitmap(List<Bitmap> bits){
    	Bitmap firstBit=null;
    	if(bits!=null&&bits.size()>0){
    		firstBit=bits.get(0);
    		for(int i=1;i<bits.size();i++){
    			firstBit=addHBitmap(firstBit,bits.get(i));
    		}
    	}
    	return firstBit;
    	
    }
    
    
    
    
    
    /**
    * 
    * ����ƴ��
    * 
    * @param first
    * @param second
    * @return 
    */
    private static Bitmap addHBitmap(Bitmap first, Bitmap second) {
    int width = first.getWidth() + second.getWidth();
    int height = Math.max(first.getHeight(), second.getHeight());
    Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
    Canvas canvas = new Canvas(result);
    canvas.drawBitmap(first, 0, 0, null);
    canvas.drawBitmap(second, first.getWidth(), 0, null);
    return result;
    }
    
    
    
    
    private static int[] color={Color.BLACK};
    
    
    
    /**
     * ����ͼƬ��ƴ��
     * 
     * @param first
     * @param second
     * @return 
     */
     public static Bitmap addMBitmap(Bitmap first,int itemWidth,int position) {
	     int width = first.getWidth() +itemWidth ;
	     int height = first.getHeight();
	     Bitmap temp_fist = Bitmap.createBitmap(first, 0, 0, position, height);
	     
	     Bitmap temp_second = Bitmap.createBitmap(first, position, 0, first.getWidth()-position, height);
	     
	     Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
	     Canvas canvas = new Canvas(result);
	     canvas.drawBitmap(temp_fist, 0, 0, null);
	     Paint paint=new Paint();
	     paint.setColor(Color.BLACK);
	     canvas.drawRect(position, 0, position+itemWidth, height, paint);
	     canvas.drawBitmap(temp_second, position+itemWidth, 0, null);
	     return result;
     }
    

     
     /**
      * Drawable To Bitmap
      * 
      * @param drawable
      * @return
      */
     public static Bitmap drawableToBitmap(Drawable drawable) {
         int w = drawable.getIntrinsicWidth();
         int h = drawable.getIntrinsicHeight();
         Bitmap.Config config =
                 drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                         : Bitmap.Config.RGB_565;
         Bitmap bitmap = Bitmap.createBitmap(w, h, config);
         Canvas canvas = new Canvas(bitmap);
         drawable.setBounds(0, 0, w, h);
         drawable.draw(canvas);
         return bitmap;
     }
     
     

    /**
    * 
    * ����ƴ��
    * 
    * @param first
    * @param second
    * @return 
    */
    private static Bitmap addVBitmap(Bitmap first, Bitmap second) {
    int width = Math.max(first.getWidth(),second.getWidth());
    int height = first.getHeight() + second.getHeight();
    Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
    Canvas canvas = new Canvas(result);
    canvas.drawBitmap(first, 0, 0, null);
    canvas.drawBitmap(second, first.getHeight(), 0, null);
    return result;
    }
    
    
    
    
}
