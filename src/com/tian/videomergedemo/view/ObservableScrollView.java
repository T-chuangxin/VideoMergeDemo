package com.tian.videomergedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.tian.videomergedemo.inter.ScrollViewListener;

public class ObservableScrollView extends HorizontalScrollView {  
  
    private ScrollViewListener scrollViewListener = null;  
    private boolean isTouch=false;//默认是手势控制滑动
  
    public ObservableScrollView(Context context) {  
        super(context);  
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
        this.scrollViewListener = scrollViewListener;  
    }  
  
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {  
        super.onScrollChanged(x, y, oldx, oldy);  
        if (scrollViewListener != null) {  
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy,isTouch);  
        }  
    }

    
    
    
	
    
    
    
    
    
    
    
    
  
}  