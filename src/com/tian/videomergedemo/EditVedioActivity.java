package com.tian.videomergedemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.tian.videomergedemo.bean.AttachsBeen;
import com.tian.videomergedemo.bean.ComposeInfo;
import com.tian.videomergedemo.bean.RecordDetail;
import com.tian.videomergedemo.dao.DatabaseContext;
import com.tian.videomergedemo.dao.RecordDao;
import com.tian.videomergedemo.inter.CompletionListener;
import com.tian.videomergedemo.inter.ScrollViewListener;
import com.tian.videomergedemo.manager.FfmpegManager;
import com.tian.videomergedemo.manager.VideoStitchingRequest;
import com.tian.videomergedemo.utils.BitmapUtils;
import com.tian.videomergedemo.utils.TimeFormatUtils;
import com.tian.videomergedemo.view.EditVideoImageBar;
import com.tian.videomergedemo.view.ObservableScrollView;
import com.tian.videomergedemo.view.VideoEditProgressBar;


/**
 * 
 * 视频的编辑界面
 * 
 * @author Administrator
 *
 */
@SuppressLint("NewApi") public class EditVedioActivity extends Activity implements OnClickListener, ScrollViewListener {
	
	
	private int screenWidth;
	private int screenHeight;
	private String mVedioPath;
	private ComposeInfo info;
	private RecordDao dao;
	private AttachsBeen bean;
	private VideoView mVideoView;
	private VideoEditProgressBar mVedioBar;
	private ImageView mPlayerController;
	private TextView mVideoDuration;
	private ImageView mCutter;
	private ImageView mDelete;
	private ImageView mmerge;
	private ArrayList<Integer> keyFrameList;
	private EditVideoImageBar mImageLists;
	protected int mDuration=0;
	private int mBottomLength=1;
	private int mDragPosition=0;//手势拖动的距离
	private boolean isPlaying=false;
	
	
	
	
	private Handler mHandler=new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case 0://拖动更新
				if(!mVideoView.isPlaying()&&!isPlaying){
					//非播放状态
					mVideoView.seekTo(mDragPosition);
				}else{
					//播放状态
					int mDragPosition1 = mVideoView.getCurrentPosition();
					int currentPosition=0;
					if(mDuration!=0){
						currentPosition=mBottomLength*mDragPosition1/mDuration;
					}else{
						if(currentPosition<=100){
							currentPosition=mDragPosition1;
						}
					}
					mScrollView.scrollTo(currentPosition==0?1:currentPosition, 0);
					mCurrentPosition=currentPosition;
					mHandler.sendEmptyMessageDelayed(0, 100);
				}
				int currentPosition = mVideoView.getCurrentPosition();
				mVideoEditProgressBar.setProgress((currentPosition*1.0f/mDuration));
				break;
			case 1://视频播放或者暂停action
				
				break;
			case 3:
				mVideoView.pause();
				break;
			case 100:
				mererVideo();
				break;
			case 101:
				reRreshUI();
				break;
			default:
				break;
			}
			
		}
		
	};
	/**
	 * Scroller的滑动监听
	 * @param scrollView
	 * @param x
	 * @param y
	 * @param oldx
	 * @param oldy
	 */
	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy,boolean isByUser) {
		if(x>=0){
			this.mCurrentPosition=x;
			mImageLists.showSelectArea(true);
			mBottomLength = mImageLists.getMeasuredWidth();
			if(mBottomLength!=0){
				mDragPosition = mDuration*x/mBottomLength;
			}
			if(!mVideoView.isPlaying()&&!isPlaying){
				mHandler.sendEmptyMessage(0);
			}
			currentTime.setText(TimeFormatUtils.formatLongToTimeStr(mDragPosition)+"");
		}
		
	}
	
	private TextView currentTime;
	private ObservableScrollView mScrollView;
	private AlertDialog alertDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_vedio);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		WindowManager windowManager = (WindowManager) getApplication().
                getSystemService(getApplication().WINDOW_SERVICE);
		screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
		
        mVedioPath = getIntent().getStringExtra("vedio_path");
        info=new ComposeInfo();
		info.setPath(mVedioPath);
		if(dao==null){
			dao=new RecordDao(this);
		}
		
 		bean = new AttachsBeen();
		bean.setAchsPath(mVedioPath);
		bean = dao.isExist(bean);
		info.setTips(bean.getTips());
		info.setFlags(bean.getFlags());
        
		
		initView();
        init();
        
       
        
        
		
	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		mVideoView = (VideoView)this.findViewById(R.id.vv_vedio_show);
		View backView=this.findViewById(R.id.iv_edit_back);
		backView.setOnClickListener(this);
		mPlayerController = (ImageView)this.findViewById(R.id.iv_play_pause);
		mPlayerController.setOnClickListener(this);
		mVideoDuration = (TextView)this.findViewById(R.id.tv_duration);
		mCutter = (ImageView)this.findViewById(R.id.iv_btn_cut);
		mCutter.setOnClickListener(this);
		mDelete = (ImageView)this.findViewById(R.id.iv_delete);
		mDelete.setOnClickListener(this);
		mmerge = (ImageView)this.findViewById(R.id.iv_merge);
		mmerge.setOnClickListener(this);
		mImageLists = (EditVideoImageBar)this.findViewById(R.id.iv_show);
		
		mVideoEditProgressBar = (VideoEditProgressBar)this.findViewById(R.id.vedio_progress);
		
		
		currentTime = (TextView)this.findViewById(R.id.tv_progress_time);
		
		
		mScrollView = (ObservableScrollView)this.findViewById(R.id.sl);
		
		mScrollView.setScrollViewListener(this);
		mImageLists.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mImageLists.showSelectArea(false);
			}
		});
		LinearLayout ll_wave_content = (LinearLayout)this.findViewById(R.id.ll_scroll);
		ll_wave_content.setPadding(screenWidth/2, 0, screenWidth/2, 0);
	}

	
	
	/**
	 * 初始化参数生成
	 */
	private void init() {
		
		mImageLists.clearPosition();
		initVideoInfo();
		
		mVideoEditProgressBar.clearPoint();
		mVideoEditProgressBar.setPausePoints(info.getPausePoints());
		final ArrayList<Float> pausePoints = new ArrayList<Float>();
		mVideoView.setVideoPath(mVedioPath);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				
				mDuration = mp.getDuration();       
//				mVideoView.seekTo(1);//避免显示黑屏   
				mVideoDuration.setText(TimeFormatUtils.formatLongToTimeStr(mDuration)+"");
				
				if(info.getFlags()!=null&&!info.getFlags().isEmpty()){
					for (int i = 0; i <info.getFlags().size(); i++) {
						Integer integer = info.getFlags().get(i);
						pausePoints.add(integer*1.0f/mDuration);   
					}
				}
				if (pausePoints != null && pausePoints.size() > 0) {
					mVideoEditProgressBar.setPausePoints(pausePoints);
				}
				mVideoView.start();
				mHandler.sendEmptyMessageDelayed(3, 100);
				
			}
		});
//		mVideoView.seekTo(1);
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				isPlayCompletion=true;
				isPlaying=false;
				mPlayerController.getDrawable().setLevel(1);
				mScrollView.scrollTo(mDragPosition, 0);
			}
		});
	}
	private boolean isPlayCompletion=false;
	
	
	/**
	 * 初始化视频文件信息
	 */
	private void initVideoInfo(){
		File playFile=new File(mVedioPath);
		if(playFile.exists()){
			new ExtractTask(mVedioPath).execute();//执行抽取关键帧的异步任务
		}else{
			Toast.makeText(this, "请检查视频文件是否存在！", 0).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_edit_back://返回按钮
			if(isEdit){
				showTip();
			}else{
				finish();
			}
			break;
		case R.id.iv_play_pause://视频暂停/开始
			playVideo();
			break;
		case R.id.iv_btn_cut://裁剪按钮
			setCurrentPostion();
			
			break;
		case R.id.iv_delete://删除按钮
			cutVideo();
			break;
		case R.id.iv_merge://合并按钮
			if(isEdit){//如果被编辑了
				showTip();
			}else{
				Toast.makeText(EditVedioActivity.this, "您还未对视频进行编辑！", 0).show();
			}
			break;
			
		case R.id.tv_yes:
			//TODO
			saveFlag();//保存重置的标记点
//			EventBus.getDefault().post(new StringMessage(mVedioPath));
			alertDialog.dismiss();
			finish();
			break;
		case R.id.tv_no://不保存
			alertDialog.dismiss();
			finish();
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 音频的裁剪删除操作
	 */
	protected void cutVideo() {
		cutPostion = mImageLists.getCutPostion();
			if(cutPostion!=null&&cutPostion.size()>0){
				onSave();
			}else{
				Toast.makeText(this, "请选择删除视频片段！", 0).show();
			}
	}
	
	
	 /**
     * 进行断点处理
     */
    private void setCurrentPostion(){
    	mImageLists.setCutPostion(mCurrentPosition);
    }
	
	
    /**
	 * 显示是否保存编辑的提示
	 */
	private void showTip(){
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(this)
					.create();
		}
		alertDialog.show();
		View tipView = View.inflate(this, R.layout.edit_alert_layout, null);
		View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
		//不再提醒
		tv_yes.setOnClickListener(this);
		tipView.findViewById(R.id.tv_no).setOnClickListener(this);
		alertDialog.setContentView(tipView);

	}
	@Override
	public void onBackPressed() {
		if(isEdit){
			showTip();
		}else{
			super.onBackPressed();
		}
	}
    
    
	
	/**
	 * 播放控制
	 */
	private void playVideo() {
		
		if(isPlayCompletion){
			mDragPosition=0;
			mScrollView.scrollTo(mDragPosition, 0);
			isPlayCompletion=false;
		}
		if(mVideoView.isPlaying()){
			isPlaying=false;
			mVideoView.pause();
			mPlayerController.getDrawable().setLevel(1);
		}else{
			isPlaying=true;
//			mVideoView.seekTo(mDragPosition);
			mPlayerController.getDrawable().setLevel(2);
			mVideoView.start();
		}
		
		mHandler.sendEmptyMessage(0);
		
	}
	private int width1;
	private VideoEditProgressBar mVideoEditProgressBar;

	/**
	 * 抽取关键帧的异步任务
	 * @author howie
	 *
	 */
	class ExtractTask extends AsyncTask<Void, Void, Bitmap>{
		private String path;
		private ProgressDialog progressDialog1;
		
		public ExtractTask(String path) {
			super();
			this.path = path;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(progressDialog1==null){
				progressDialog1 = ProgressDialog.show(EditVedioActivity.this,
						"加载中...", "请稍等...", true);
			}else{
				progressDialog1.show();
			}
			
		}
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			return BitmapUtils.addHBitmap(addFrames(path));
		}
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Bitmap bitmaps) {
			super.onPostExecute(bitmaps);
			progressDialog1.dismiss();
			progressDialog1.cancel();
			progressDialog1=null;
			mImageLists.setBackground(new BitmapDrawable(bitmaps));
			mHandler.sendEmptyMessage(0);
		}
		
	}
	
	
	
	
	 private List<Bitmap> addFrames(String path) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(path);
		// 取得视频的长度(单位为毫秒)
		String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		int duration = Integer.parseInt(time);
		int toatalFrames = duration / 1000;
		keyFrameList = new ArrayList<Integer>();
		int interval = 0;

		for (int i = 0; i < toatalFrames; i++) {//
			int frameTime = Integer.valueOf(interval) / 1000;
			keyFrameList.add(frameTime);
			interval += duration / toatalFrames;   
		}
		List<Bitmap> bits=new ArrayList<Bitmap>();
		for (int i = 0; i < keyFrameList.size(); i++) {
			Bitmap bitmap = retriever.getFrameAtTime(keyFrameList.get(i) * 1000 * 1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			if(bitmap!=null){
				int bmpWidth=bitmap.getWidth();   
		        int bmpHeight=bitmap.getHeight();  
		        float scale=(float) 0.7;
		        /* 产生reSize后的Bitmap对象 */  
		        Matrix matrix = new Matrix();  
		        matrix.postScale(scale, scale);  
		        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth,   
		                bmpHeight,matrix,true);
				bits.add(resizeBmp);
			}
		}
		return bits;
	}
	private int mCurrentPosition=0;
	
	
	
	
	
	
	/**
	 * 删除删除片段操作
	 */
	private void onSave() {
        saveRingtone();

    }
	private int mNewFileKind;

	private File outFile;
	private List<float[]> cutPostion;
	private boolean isEdit=false;
	private List<long[]> cutPostion_temp=new ArrayList<long[]>();
	private List<long[]> cutPostion_use=new ArrayList<long[]>();
	private List<long[]> cutPostion_use1=new ArrayList<long[]>();
	protected String mMessage=null;
	protected List<String> mMergerList;
    private void saveRingtone() {
    	isEdit = true;
        cutPostion_temp.clear();
        for(int i=0;i<cutPostion.size();i++){
        	long[] temp_fs=new long[2];
        	float[] fs = cutPostion.get(i);
        	
        	double start=fs[0]*mDuration/mBottomLength;
        	double end=fs[1]*mDuration/mBottomLength;
        	
        	temp_fs[0] = (long) start;
            temp_fs[1] = (long) end;
        	cutPostion_temp.add(temp_fs);
        }
        cutPostion_use.clear();
    	
    	//头部开始计算
    	long[] lg_f=new long[2];
    	lg_f[0]=0;
    	lg_f[1]=0;
		cutPostion_use.add(lg_f);
		//添加选中的区间
    	for(int i=0;i<cutPostion_temp.size();i++){
    		cutPostion_use.add(cutPostion_temp.get(i));
    	}
    	//最后的默认选中区间
    	long[] lg_e=new long[2];
    	lg_e[0]=mDuration;
    	lg_e[1]=mDuration;
		cutPostion_use.add(lg_e);
		cutPostion_use1.clear();
		
		//非选中区间集合
		for(int i=0;i<cutPostion_use.size();i++){
			if((i+1)<cutPostion_use.size()){
				//不超边界
				if((cutPostion_use.get(i+1)[0]-cutPostion_use.get(i)[1])!=0){
					//所取区域的帧数不能为0，即去除时间段为0 的区间片段
					long[] lon=new long[2];
					lon[0]=cutPostion_use.get(i)[1];
					lon[1]=cutPostion_use.get(i+1)[0];
					cutPostion_use1.add(lon);
				}
			}
		}
		//合并前重置标记点集合
		resetFlag(cutPostion_use1);
        String workingPath=getRecordFileFolder()+"/trim";
        //裁剪操纵开始执行
        trimVideo(workingPath,cutPostion_use1);
    
    
    }

	
    /**
     * 重置标记集合
     */
    private void resetFlag(List<long[]> cutPostion_use1) {
    	 ArrayList<Integer> flags = info.getFlags();
    	 ArrayList<Integer> flags_temp=new ArrayList<Integer>();
    	boolean isContent=false;
    	if(flags!=null&&flags.size()>0){
    		for(int i=0;i<flags.size();i++){
        		for(int j=0;j<cutPostion_use1.size();j++){
        			if(flags.get(i)<cutPostion_use1.get(j)[1]&&flags.get(i)>cutPostion_use1.get(j)[0]){
        				isContent=true;
        			}
        		}
        		if(isContent){
        			flags_temp.add(flags.get(i));
        		}
        	}
    		bean.setFlags(flags_temp);
    		
    		
    	}
    	
    	 
    	
    	
    	
		
	}

	/**
	 * 裁剪视频
	 */
	private void trimVideo(String workingPath, List<long[]> newSeeks){
		progressDialog = ProgressDialog.show(EditVedioActivity.this,
				"裁剪中...", "请稍等...", true);
		File storagePath = new File(workingPath);             
		storagePath.mkdirs();  
		FfmpegManager manager = FfmpegManager.getInstance();
		File file = new File(mVedioPath);
		boolean exists = file.exists();
		manager.trimVideo(EditVedioActivity.this, file, storagePath, newSeeks, new CompletionListener() {
			@Override
			public void onProcessCompleted(String message,List<String> merger) {   
				if(merger!=null&&merger.size()>0){
					mMergerList=merger;
					mHandler.sendEmptyMessage(100);
				}
			}
		});
	}
	
	
	/**
	 * 合并裁剪过后的视频
	 */
	private void mererVideo(){
		String workingPath1=getRecordFileFolder()+"/merge";
		File storagePath1 = new File(workingPath1);  
		storagePath1.mkdirs();
		File myMovie = new File(storagePath1, String.format("cut-output-%s.mp4", System.currentTimeMillis()+"")); 
		finalPath = myMovie.getAbsolutePath();
		
		VideoStitchingRequest videoStitchingRequest = new VideoStitchingRequest.Builder()
		.inputVideoFilePath((ArrayList<String>) mMergerList)
		.outputPath(finalPath).build();
		FfmpegManager manager = FfmpegManager.getInstance();
		manager.stitchVideos(this, videoStitchingRequest,
		new CompletionListener() {
			@Override
			public void onProcessCompleted(String message,List<String> merger) {
				mHandler.sendEmptyMessage(101);
				mMessage=message;
			}
		});
	}
	
	
private void reRreshUI(){
		progressDialog.dismiss();
		progressDialog.cancel();
		progressDialog=null;
		if(mMessage!=null){
			Toast.makeText(this, "编辑成功！", Toast.LENGTH_SHORT).show();
			isEdit=true;//已经编辑过了
			mVedioPath=finalPath;
			init();
			
		}else{
			
		}
		
	}
	private static final String fileFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/jwzt_recorder";
	private ProgressDialog progressDialog;
	private String finalPath;
	private String getRecordFileFolder() {
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return fileFolder;
    }

	
	/**
	 * 标记保存操作
	 */
	private void saveFlag() {
		RecordDao recordDao=new RecordDao(new DatabaseContext(this));
		RecordDetail bean=new RecordDetail();
		bean.setName(getVideoName(mVedioPath));
		bean.setFormat("mp4");
		bean.setPath(mVedioPath);
		bean.setMarks(info.getTips());//断点拍摄的点的进度的值
		bean.setFlags(info.getFlags());
		recordDao.add(bean);
		
	}
	
	
	private String getVideoName(String path){
		int slash = path.lastIndexOf("/");
		 int dot = path.lastIndexOf(".");
		String substring = path.substring(slash+1, dot);
		return substring;
		
	}
	

}
