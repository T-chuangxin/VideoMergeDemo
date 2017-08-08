package com.tian.videomergedemo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.tian.videomergedemo.R;
import com.tian.videomergedemo.utils.OtherUtils;
import com.tian.videomergedemo.utils.VideoItem;

public class VideosGridAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VideoItem> mList;
	private int mWidth;

	
//	private ImageLoader imageLoader;
	public VideosGridAdapter(Context mContext, ArrayList<VideoItem> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;

		int screenWidth = OtherUtils.getWidthInPx(mContext);
		mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4))/3;
		
		
		
		
		
		
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
//		ViewHolder holder=null;
//		if(convertView==null){
//			holder=new ViewHolder();
			convertView = View.inflate(mContext, R.layout.video_select_item_layout,null);
			ImageView iv_thumb=(ImageView) convertView.findViewById(R.id.iv_thumb);
			final ImageView iv_select=(ImageView) convertView.findViewById(R.id.iv_select);
			
//			convertView.setTag(holder);
//		}else{
//			holder=(ViewHolder) convertView.getTag();
//		}
		
		
		
//		View view=View.inflate(mContext, R.layout.video_select_item_layout, null);
		final VideoItem videoItem = mList.get(position);
		String path = videoItem.path;
//		imageLoader.displayImage(path, holder.iv_thumb, options);
//		Bitmap decodeFile = BitmapFactory.decodeFile(path);
//		Bitmap srcBitmap = ThumbnailUtils.createVideoThumbnail(path, 0);
//		srcBitmap = ThumbnailUtils.extractThumbnail(srcBitmap, 420, 300);
//		iv_thumb.setImageBitmap(srcBitmap);
		
		
		
//		imageLoader.displayImage(path, iv_thumb, options);
		
		if(videoItem.isSelected){
			iv_select.setImageResource(R.drawable.blue_dot_solid);
		}else{
			iv_select.setImageResource(R.drawable.blue_dot_empty);
		}
		iv_thumb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(videoItem.isSelected){
					videoItem.isSelected=false;
					iv_select.setImageResource(R.drawable.blue_dot_empty);//空心
				}else{
					videoItem.isSelected=true;
					iv_select.setImageResource(R.drawable.blue_dot_solid);//实心
				}
			}
		});
		return convertView;
	}
	public class ViewHolder{
//		TextView name,first_word;
		ImageView iv_thumb;
		ImageView iv_select;
	}

}
