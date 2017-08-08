package com.tian.videomergedemo.adapter;

import com.tian.videomergedemo.R;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


/**
 * 时间刻度显示界面
 * 
 * @author howie
 * 
 */
public class TimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	private int width;
	
	public TimeAdapter(int width){
		this.width=width;
	}
	
	
	/**
	 * 第一种布局
	 * @author afnasdf
	 *
	 */
	public final static class ItemViewHolder extends RecyclerView.ViewHolder {
		public View lineLeft;
		public View lineRight;
		public TextView time;
		

		public ItemViewHolder(View itemView) {
			super(itemView);
			lineLeft=(View) itemView.findViewById(R.id.line_left);
			lineRight=(View) itemView.findViewById(R.id.line_right);
			time = (TextView) itemView.findViewById(R.id.tv_time_recorder);
			
		}
	}
	
	/**
	 * 第二种布局
	 * @author afnasdf
	 *
	 */
	public final static class ItemViewHolder1 extends RecyclerView.ViewHolder {
		ImageView iv;
		
		
		public ItemViewHolder1(View itemView,int width) {
			super(itemView);
			iv=(ImageView) itemView.findViewById(R.id.iv);
			RelativeLayout.LayoutParams param=(LayoutParams) iv.getLayoutParams();
			param.width=width;
			param.height=LayoutParams.MATCH_PARENT;
			iv.setLayoutParams(param);
			
		}
	}

	@Override
	public int getItemCount() {
		return 3600;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if(position==0){
			ItemViewHolder1 itemViewHolder1=(ItemViewHolder1)viewHolder;
			itemViewHolder1.iv.setBackgroundColor(Color.TRANSPARENT);
			
		}else{
			ItemViewHolder itemViewHolder=(ItemViewHolder)viewHolder;
			String timeStr="";
			int min=(position-1)/60;
			int sec=(position-1)%60;
			if(min<10){
				timeStr="0"+min;
			}else{
				timeStr=min+"";
			}
			if(sec<10){
				timeStr=timeStr+":0"+sec;
			}else{
				timeStr=timeStr+":"+sec;
			}
			itemViewHolder.time.setText(timeStr);
		
		}
		
		
//		if(position==0){
//			itemViewHolder.time.setText("00:01");
//			
//		}else{
//			String timeStr="";
//			int min=position/60;
//			int sec=position%60;
//			if(min<10){
//				timeStr="0"+min;
//			}else{
//				timeStr=min+"";
//			}
//			if((sec+1)<10){
//				timeStr=timeStr+":0"+(sec+1);
//			}else{
//				timeStr=timeStr+":"+(sec+1);
//			}
//			itemViewHolder.time.setText(timeStr);
//			
//		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view=null;
		
		if(viewType==0){
			view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
			
			return new ItemViewHolder1(view,width);
		}else{
			view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_time_counter, viewGroup, false);
			
			return new ItemViewHolder(view);
		}
		
		
	}

	
	
    @Override
    public int getItemViewType(int position) {
        return position==0?0:1;
    }
	
	
	
}
