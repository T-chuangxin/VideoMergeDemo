package com.tian.videomergedemo.utils;


import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public  class UserToast {
	private static Toast mToast = null; 
	private static Toast imageToast=null;
	 public static void toSetToast(Context context,String ss){
		 
		 if( null == context || TextUtils.isEmpty( ss ) ){  
	           return;  
	       }  
	         
	       if( null == mToast ){  
	           mToast = Toast.makeText( context, ss, Toast.LENGTH_SHORT);  
	       }else{  
	           mToast.setText( ss );  
	       } 
	         
	          // mToast.setDuration(500);
			   mToast.setGravity(Gravity.CENTER, 0, 0);
			  // LinearLayout toastView = (LinearLayout) mToast.getView();
			/*   ImageView imageCodeProject = new ImageView(getApplicationContext());
			   imageCodeProject.setImageResource(R.drawable.icon);
			   toastView.addView(imageCodeProject, 0);*/
			   mToast.show();
	 }
	
	/* public static void showImageToast(Context context,String str){
		 if( null == context || TextUtils.isEmpty( str ) ){  
	           return;  
	       } 
		 View view=View.inflate(context, R.layout.image_toast_layout, null);
		 TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
		 if( null == imageToast ){  
			 imageToast =  new Toast(context);
	          
	       }
		 tv_num.setText(str);
		 
		
		 imageToast.setGravity(Gravity.CENTER, 0, 0);
		 imageToast.setView(view);
		 imageToast.setDuration(0);
		 imageToast.show();
	 }
	  */
	 
}
