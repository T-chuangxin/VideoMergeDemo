package com.tian.videomergedemo.inter;

import com.tian.videomergedemo.view.ObservableScrollView;


/**
 * 接口监听 ScrollView的滑动
 * @author afnasdf
 *
 */
public interface ScrollViewListener {  
  
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy,boolean isByUser);  
  
}  