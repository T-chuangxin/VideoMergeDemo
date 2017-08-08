package com.tian.videomergedemo;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.tian.videomergedemo.adapter.VideosGridAdapter;
import com.tian.videomergedemo.utils.VideoDataSource;
import com.tian.videomergedemo.utils.VideoDataSource.OnVideosLoadedListener;
import com.tian.videomergedemo.utils.VideoFolder;
import com.tian.videomergedemo.utils.VideoItem;
import com.tian.videomergedemo.utils.VideoPicker;
import com.tian.videomergedemo.utils.VideoPicker.OnVideoSelectedListener;
/**
 * 以网格显示的选择多个视频文件的界面
 * @author howie
 *
 */
public class VideoGridActivity extends FragmentActivity implements OnVideosLoadedListener,OnVideoSelectedListener,OnClickListener{
	private List<VideoFolder> mVideoFolders;   //所有的视频文件夹
	private VideoPicker videoPicker;
	private GridView gv;
	/**左上角的关闭按钮*/
	private View iv_close,tv_confirm;//关闭按钮和确定按钮
	private ArrayList<VideoItem> videos; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_grid_layout);
		initData();   
	    
	}



	/**
	 * 初始化数据
	 */
	private void initData() {
		gv=(GridView) findViewById(R.id.gv);
		iv_close=findViewById(R.id.iv_close);
		tv_confirm=findViewById(R.id.tv_confirm);
		iv_close.setOnClickListener(this);
		tv_confirm.setOnClickListener(this);
		videoPicker = VideoPicker.getInstance();
	    videoPicker.clear();
	    //视频加载完成时回调该接口
	    videoPicker.addOnVideoSelectedListener(this);   
	    new VideoDataSource(this, null, this);
	}
	
	
	@Override
	public void onVideosLoaded(List<VideoFolder> videoFolders) {
		videoPicker.setVideoFolders(videoFolders); 
		VideoFolder videoFolder = videoFolders.get(0);
		videos = videoFolder.videos;
		for (int i = 0; i < videos.size(); i++) {
			VideoItem videoItem = videos.get(i);
			if(!TextUtils.isEmpty(videoItem.path)){
				
				File file=new File(videoItem.path);
				if(!file.exists()){
					videos.remove(i);
					i--;
				}
			}else{
				videos.remove(i);
				i--;
			}
		}
		
		
		VideosGridAdapter adapter=new VideosGridAdapter(VideoGridActivity.this,videos);
		gv.setAdapter(adapter);
	}
	@Override
	public void onVideoSelected(int position, VideoItem item, boolean isAdd) {
		
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.iv_close:
			finish();
			break;
		case R.id.tv_confirm:
//			finish();
			boolean isSelect = false;
			ArrayList<String> paths=null;
			if(videos!=null&&!videos.isEmpty()){
				for (int i = 0; i < videos.size(); i++) {
					if(videos.get(i).isSelected){
						isSelect=true;
						if(paths==null){
							paths=new ArrayList<String>();
						}
						paths.add(videos.get(i).path);
					}
				}
			}
			if(!isSelect){
				Toast.makeText(VideoGridActivity.this, "您还没有选择任何视频", 0).show();
			}else{
				Intent intent=new Intent();
				intent.putStringArrayListExtra("paths", paths);//把选择的视频的路径的集合传回去
				setResult(1, intent);
				finish();
			}
			break;
		}
	}
	private void getVideoFile(final List<VideoItem> list, File file) {// 获得视频文件


		file.listFiles(new FileFilter() {
		@Override
		public boolean accept(File file) {
		// sdCard找到视频名称
		String name = file.getName();


		int i = name.indexOf('.');
		if (i != -1) {
		name = name.substring(i);
		if (name.equalsIgnoreCase(".mp4")){
		
			VideoItem vi = new VideoItem();
		vi.name=file.getName();
		vi.path=file.getAbsolutePath();
		list.add(vi);
		return true;
		}
		} else if (file.isDirectory()) {
		getVideoFile(list, file);
		}
		return false;
		}
		});
		}
	

}
