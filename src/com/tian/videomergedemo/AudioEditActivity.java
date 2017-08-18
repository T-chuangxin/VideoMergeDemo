package com.tian.videomergedemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tian.videomergedemo.inter.ScrollViewListener;
import com.tian.videomergedemo.utils.AudioUtils;
import com.tian.videomergedemo.utils.CheapWAV;
import com.tian.videomergedemo.utils.DensityUtil;
import com.tian.videomergedemo.utils.SamplePlayer;
import com.tian.videomergedemo.utils.SoundFile;
import com.tian.videomergedemo.utils.U;
import com.tian.videomergedemo.view.ObservableScrollView;
import com.tian.videomergedemo.view.WaveSurfaceView;
import com.tian.videomergedemo.view.WaveformView_1;
import com.tian.videomergedemo.view.WaveformView_2;

/**
 * 音频编辑页面
 * @author afnasdf
 *
 */
public class AudioEditActivity extends Activity implements ScrollViewListener{
	
	private String mFilename;
	private int totalTime;
	private int width;
	private int height;
	private WaveformView_1 waveView;
	private WaveSurfaceView waveSfv;
	private ObservableScrollView mScrollView;
	private WaveSurfaceView waveSfv1;
	private WaveformView_2 waveView1;
	private ObservableScrollView mScrollView1;
	private List<Integer> flagPositions=new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_edit);
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
		Intent intent = getIntent();
        mFilename = intent.getData().toString();
        fileName = mFilename.substring(mFilename.lastIndexOf("/") + 1).replace(".wav", "");
		flags = getSharedPreferences(fileName, MODE_PRIVATE).getString("flags", null);
		if(flags!=null){
			flagsPositions = flags.split(",");
			for(int i=0;i<flagsPositions.length;i++){
				flagPositions.add(Integer.valueOf(flagsPositions[i]));
			}
		}
		
		
		
        totalTime=getSharedPreferences(mFilename.substring(mFilename.lastIndexOf("/")+1).replace(".", ""), MODE_PRIVATE).getInt("totle_time", 0)/1000;
        mHandler = new Handler();
		initView();
		
		
	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		ImageView back=(ImageView)this.findViewById(R.id.iv_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isEdit){
					new PopupWindows(AudioEditActivity.this,ll_bottom);
				}else{
					AudioEditActivity.this.finish();
				}
			}
		});
		
		ImageView save=(ImageView)this.findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(AudioEditActivity.this, "编辑成功保存！", 0).show();
				
				AudioEditActivity.this.finish();
			}
		});
		
		cutBtn = (ImageButton)this.findViewById(R.id.audio_play);
		
		cutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentPostion();
			}
		});
		
		ll_bottom = (LinearLayout)findViewById(R.id.ll_bottom);
		
		ImageButton	delBtn = (ImageButton)this.findViewById(R.id.ib_wav_del);
		delBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cutVideo();
			}
		});
		
		ImageButton ffwd=(ImageButton)this.findViewById(R.id.ffwd);
		ffwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new PopupWindows(AudioEditActivity.this,ll_bottom);
			}
		});
		
		
		View controler=this.findViewById(R.id.rl_control_play);
		
		play = this.findViewById(R.id.iv_play);
		pause = this.findViewById(R.id.iv_pause);
		tv_totalTime = (TextView)this.findViewById(R.id.tv_total_time);
		
		controler.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPlay();
			}
		});
		
		timeShow = (TextView)this.findViewById(R.id.tv_current_time);
		
		//第二个视图
		waveSfv = (WaveSurfaceView)this.findViewById(R.id.wavesfv);
		waveView = (WaveformView_1)this.findViewById(R.id.waveview);
		waveView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				waveView.showSelectArea(false);
			}
		});
		mScrollView = (ObservableScrollView)this.findViewById(R.id.hlv_scroll);
		mScrollView.setScrollViewListener(this);
		ll_wave_content = (LinearLayout)this.findViewById(R.id.ll_wave_content);
		ll_wave_content.setPadding(width/2-DensityUtil.dip2px(5), 0, width/2-DensityUtil.dip2px(5), 0);
		
		//第一个视图
		waveSfv1 = (WaveSurfaceView)this.findViewById(R.id.wavesfv1);
		waveView1 = (WaveformView_2)this.findViewById(R.id.waveview1);
//		waveView1.setFlag(flagPositions);
		mScrollView1 = (ObservableScrollView)this.findViewById(R.id.hlv_scroll1);
		mScrollView1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mScrollView1.setScrollViewListener(this);
		ll_wave_content1 = (LinearLayout)this.findViewById(R.id.ll_wave_content1);
		timeLine = (LinearLayout)this.findViewById(R.id.ll_time_counter);
		timeLine1 = (LinearLayout)this.findViewById(R.id.ll_time_counter1);
		timeSize();
		ll_wave_content1.setPadding(width/2-DensityUtil.dip2px(5), 0, width/2-DensityUtil.dip2px(5), 0);
		
	     U.createDirectory();
	     if(waveSfv != null&&waveSfv1 != null) {
	         waveSfv.setLine_off(42);
	         //解决surfaceView黑色闪动效果
	         waveSfv.setZOrderOnTop(true);
	         waveSfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	         waveSfv1.setLine_off(42);
	         //解决surfaceView黑色闪动效果
	         waveSfv1.setZOrderOnTop(true);
	         waveSfv1.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	     }
	     waveView.setLine_offset(42);
	     waveView1.setLine_offset(42);
	     initWaveView();
	     timerCounter.start();
		
	}

	/**
	 * 音频的时间刻度
	 */
	private void timeSize() {
		tv_totalTime.setText(formatTime(totalTime)+"");
		timeLine.removeAllViews();
		totleLength = totalTime*DensityUtil.dip2px(60);
//		timeLine1.removeAllViews();
		ll_wave_content1.setLayoutParams(new FrameLayout.LayoutParams(totalTime*DensityUtil.dip2px(60),LayoutParams.MATCH_PARENT));
		ll_wave_content.setLayoutParams(new FrameLayout.LayoutParams(totalTime*DensityUtil.dip2px(60),LayoutParams.MATCH_PARENT));
		timeLine1.setLayoutParams(new RelativeLayout.LayoutParams(totalTime*DensityUtil.dip2px(60),LayoutParams.MATCH_PARENT));
		for(int i=0;i<totalTime;i++){
		LinearLayout line1=new LinearLayout(this);
		line1.setOrientation(LinearLayout.HORIZONTAL);
		line1.setLayoutParams(new LayoutParams(DensityUtil.dip2px(60),LinearLayout.LayoutParams.WRAP_CONTENT));
		line1.setGravity(Gravity.CENTER);
		
		TextView timeText=new TextView(this);
		timeText.setText(formatTime(i));
		timeText.setWidth(DensityUtil.dip2px(60)-2);
		timeText.setGravity(Gravity.CENTER_HORIZONTAL);
		TextPaint paint = timeText.getPaint();
		paint.setFakeBoldText(true); //字体加粗设置
		timeText.setTextColor(Color.rgb(204, 204, 204));
		View line2=new View(this);
		line2.setBackgroundColor(Color.rgb(204, 204, 204));
		line2.setPadding(0, 10, 0, 0);
		line1.addView(timeText);
		line1.addView(line2);
		timeLine.addView(line1);
		}
		
		
		
		
	}
	
	
	/**
	 * 时间格式化
	 * @param timeSec
	 * @return
	 */
	private String formatTime(int timeSec){
		if(timeSec<10){
			return "00:"+"0"+timeSec;
		}
		if(timeSec>=10&&timeSec<60){
			return "00:"+timeSec;
		}
		if(timeSec>=60){
			String str="";
			int m=timeSec/60;
			int s=timeSec%60;
			if(m<10){
				str="0"+m+":";
			}else{
				str=""+m+":";
			}
			if(s<10){
				str=str+"0"+s;
			}else{
				str=str+s;
			}
			return str;
		}
		return "";
		
	}
	
	
	/**
	 * 音频的裁剪删除操作
	 */
	protected void cutVideo() {
		cutPostion = waveView.getCutPostion();
			if(cutPostion!=null&&cutPostion.size()>0){
				onSave();
			}else{
				Toast.makeText(AudioEditActivity.this, "请选择删除音频端！", 0).show();
			}
	}
	
	
	private void onSave() {
        String mTitle = mFilename.substring(mFilename.lastIndexOf("/")+1).split("\\.")[0]+System.currentTimeMillis();  
        saveRingtone(mTitle);

    }
	private int mNewFileKind;

//	private int mStartPos;
//	private int mEndPos;
	private File outFile;
	
    private void saveRingtone(final CharSequence title) {
    	isEdit = true;
//        final String outPath = makeRingtoneFilename(title, mExtension);
    	final String outPath =U.DATA_DIRECTORY+"/result_"+mFilename.substring(mFilename.lastIndexOf("/")+1);
        String mDstFilename = outPath;
        cutPostion_temp.clear();
        for(int i=0;i<cutPostion.size();i++){
        	long[] temp_fs=new long[2];
        	float[] fs = cutPostion.get(i);
        	int pixelsToMillisecsTotal = waveView.pixelsToMillisecsTotal();
        	
        	double start=fs[0]*pixelsToMillisecsTotal/totleLength/1000;
        	double end=fs[1]*pixelsToMillisecsTotal/totleLength/1000;
        	
        	temp_fs[0] = waveView.secondsToFrames(start);
            temp_fs[1] = waveView.secondsToFrames(end);
        	cutPostion_temp.add(temp_fs);
        }
        
       
        
        //start
        if(flagsPositions!=null){
        	 List<Integer> flagPositions_sub=new ArrayList<Integer>();
        	 //倒序遍历集合
            for(int i=cutPostion.size()-1;i>=0;i--){
            	float[] fs = cutPostion.get(i);
            	int pixelsToMillisecsTotal = waveView.pixelsToMillisecsTotal();
            	//最后的编辑区间
            	double start=fs[0]*pixelsToMillisecsTotal/totleLength/1000;
            	double end=fs[1]*pixelsToMillisecsTotal/totleLength/1000;
            	//清除删除区域的标记点
            	for(int j=flagsPositions.length-1;j>=0;j--){//必须保证每个元素都要遍历的到
            		boolean temp=false;
            		double pos = (double)Integer.valueOf(flagsPositions[j])/1000;
            		if((pos<=end)&&(pos>=start)){
            			flagPositions.set(j, 0);
            			temp=true;
            		}
            		
            		if(pos>end&&!temp){//在删除区间的右侧（需进行相应时间点的操作运算）
            			flagPositions.set(j, (int) (flagPositions.get(j)-(end-start)*1000));
            		}
            		
            	}
            }
            
            for(int i=0;i<flagPositions.size();i++){
            	if(flagPositions.get(i)!=0){
            		flagPositions_sub.add(flagPositions.get(i));
            	}
            }
            flagPositions=flagPositions_sub;
            
            flagsPositions_sub=new String[flagPositions.size()];
            
            for(int i=0;i<flagPositions.size();i++){
            	flagsPositions_sub[i]=flagPositions.get(i)+"";
            }
            flagsPositions=flagsPositions_sub;
        }
       
        //end
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("操作中...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        new Thread() { 
			public void run() { 
                outFile = new File(outPath);
                try {
                	CheapWAV a=new CheapWAV();
                	a.ReadFile(new File(mFilename));
                	int numFrames = a.getNumFrames();//获取音频文件总帧数
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
                	//最后的区间
                	long[] lg_e=new long[2];
                	lg_e[0]=numFrames;
                	lg_e[1]=numFrames;
        			cutPostion_use.add(lg_e);
        			
        			cutPostion_use1.clear();
        			for(int i=0;i<cutPostion_use.size();i++){
        				if((i+1)<cutPostion_use.size()){
        					//不超边界
        					if((cutPostion_use.get(i+1)[0]-cutPostion_use.get(i)[1])!=0){
        						//所取区域的帧数不能为0
        						long[] lon=new long[2];
        						lon[0]=cutPostion_use.get(i)[1];
        						lon[1]=cutPostion_use.get(i+1)[0];
        						cutPostion_use1.add(lon);
        					}
        				}
        			}
        			
        			if(cutPostion_use1.size()==0){
        				//全部删除
        				myHandler.sendEmptyMessage(100);
        				return;
        			}
        			
        			
        			
        			File out=new File(U.DATA_DIRECTORY+"/cut_files/");
        			if(!out.exists()){
        				out.mkdirs();
        			}
        			cutPaths.clear();
        			for(int i=0;i<cutPostion_use1.size();i++){
        				File outputFile=new File(U.DATA_DIRECTORY+"/cut_files/"+"cut_"+i+".wav");
        				cutPaths.add(outputFile.getAbsolutePath());
        				a.WriteFile(outputFile, (int)cutPostion_use1.get(i)[0], 
        						(int)(cutPostion_use1.get(i)[1]-cutPostion_use1.get(i)[0]));
        			}
        			
        			File file=new File(mFilename);
        			if(file.exists()){
        				file.delete();
        			}
        			
        			//合并剪贴的片段文件
        			if(cutPaths.size()>0){
        				AudioUtils.mergeAudioFiles(mFilename, cutPaths);
        			}
        			
        			//遍历删除临时文件
        			for(int i=0;i<cutPaths.size();i++){
        				File f=new File(cutPaths.get(i));
        				if(f.exists()){
        					f.delete();
        				}
        			}
        			//删除文件夹
        			out.delete();
        			cutPaths.clear();
                	myHandler.sendEmptyMessage(10);
                	
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                }

                mProgressDialog.dismiss();
                Runnable runnable = new Runnable() {
                        public void run() {
                        	
                        }
                    };
                mHandler.post(runnable);
            }
        }.start();
    }

	
    
	/**
	 * activity销毁之前需先销毁播放器
	 */
	 @Override
	    protected void onDestroy() {
	        if (mPlayer != null && mPlayer.isPlaying()) {
	            mPlayer.stop();
	            mPlayer.release();
	            mPlayer=null;
	        }
	        //注销定时器
	        timer_speed.cancel();
	        timer_speed=null;
	        mPlayer = null;
	        
	        waveView1.clearFlag();//清除标记点
	        
	        super.onDestroy();
	    }

	 
	 private int mCurrentTime=0;
	
	/**
	 * 开始播放音频文件
	 */
	protected void startPlay() {
		
		if(mPlayer!=null&&mPlayer.isPlaying()){
			play.setVisibility(View.VISIBLE);
			pause.setVisibility(View.GONE);
			mPlayer.pause();
			mTimeCounter=-1;
		}else{
			play.setVisibility(View.GONE);
			pause.setVisibility(View.VISIBLE);
			if(mPlayer==null){
				mScrollView.scrollTo(0, 0);
				mScrollView1.scrollTo(0, 0);
			    try {
		            mPlayer = new MediaPlayer();
		            mPlayer.setDataSource(mFile1.getAbsolutePath());
		            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		            mPlayer.prepare();
		            mPlayer.start();
		            totalTime = mPlayer.getDuration();
		            mTimeCounter=0;
		            mPlayer.setOnCompletionListener(new OnCompletionListener() {
		                public synchronized void onCompletion(MediaPlayer arg0) {
		                	play.setVisibility(View.VISIBLE);
		        			pause.setVisibility(View.GONE);
		                	mScrollView.scrollTo(totleLength, 0);
		            		mScrollView1.scrollTo(totleLength, 0);
		                	mTimeCounter=-1;
		                	mPlayer=null;
		                }
		            });
		        } catch (final java.io.IOException e) {
		        	Toast.makeText(this, "文件播放出错！", Toast.LENGTH_SHORT).show();
		        }
			}else{
				int start = currentPosition*waveView.pixelsToMillisecsTotal()/totleLength;
				mTimeCounter=0;
				mPlayer.seekTo(start);
				mPlayer.start();
			}
		}
	}
	

	private void  initWaveView(){
	     loadFromFile1();
	    }

	    Thread mLoadSoundFileThread;
	    boolean mLoadingKeepGoing1;
	    SamplePlayer mPlayer1;
	    private SoundFile mSoundFile1;
		private File mFile1;
	    /** 载入wav文件显示波形 */
	    private void loadFromFile1() {
	        try {
	            Thread.sleep(300);//让文件写入完成后再载入波形 适当的休眠下
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        mFile1 = new File(mFilename);
	        mLoadingKeepGoing1 = true;
	        mLoadSoundFileThread = new Thread() {
				public void run() {
	                try {
	                    mSoundFile1 = SoundFile.create(mFile1.getAbsolutePath(),null);
	                    if (mSoundFile1 == null) {
	                        return;
	                    }
	                    mPlayer1 = new SamplePlayer(mSoundFile1);
	                } catch (final Exception e) {
	                    e.printStackTrace();
	                    return;
	                }
	                if (mLoadingKeepGoing1) {
	                    Runnable runnable = new Runnable() {
	                        public void run() {
	                            finishOpeningSoundFile1();
	                            waveSfv.setVisibility(View.INVISIBLE);
	                            waveView.setVisibility(View.VISIBLE);
	                        }
	                    };
	                    AudioEditActivity.this.runOnUiThread(runnable);
	                }
	            }
	        };
	        mLoadSoundFileThread.start();
	    }



	    float mDensity1;
		private LinearLayout timeLine;
		private MediaPlayer mPlayer;
	    /**waveview载入波形完成*/
	    private void finishOpeningSoundFile1() {
	    	 DisplayMetrics metrics = new DisplayMetrics();
		        getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        
		    waveView.setSoundFile(mSoundFile1);
	        mDensity1 = metrics.density;
	        waveView.recomputeHeights(mDensity1);
	        
	        
	        waveView1.setSoundFile(mSoundFile1);
	        waveView1.recomputeHeights(mDensity1);
	        myHandler.sendEmptyMessage(4);
	    
	    
	    }

	    
	    /**
	     * 进行断点处理
	     */
	    private void setCurrentPostion(){
	    	waveView.setCutPostion(currentPosition);
	    }
	    
	    private int currentPosition=0;
	    
		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x,
				int y, int oldx, int oldy,boolean s) {
			
			waveView.showSelectArea(true);
			currentPosition=x;
			if(x==0){
				timeShow.setText("00:00");
			}else{
				int currentSecond=waveView.pixelsToMillisecsTotal()/totleLength;
				if(currentSecond==0){
					if(x>(totleLength/2)){
						timeShow.setText("00:01");
					}else{
						timeShow.setText("00:00");
					}
				}else{
					int t=x*waveView.pixelsToMillisecsTotal()/totleLength/1000;
					timeShow.setText(formatTime(t));
				}
			}
			mScrollView.scrollTo(x, 0);
			mScrollView1.scrollTo(x, 0);
		}
	    
	    
	    
	    
		private Handler myHandler=new Handler(){
	    	public void dispatchMessage(Message msg) {
	    		switch (msg.what) {
				case 1:
					try {
			        	mScrollView.scrollTo((totleLength*mPlayer.getCurrentPosition())/mPlayer.getDuration(), 0);
			        	mScrollView1.scrollTo((totleLength*mPlayer.getCurrentPosition())/mPlayer.getDuration(), 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 10:
					
					Toast.makeText(AudioEditActivity.this, "编辑成功！", Toast.LENGTH_SHORT).show();
					String absolutePath = outFile.getAbsolutePath();
					File file_pcm = new File(outFile.getAbsolutePath().replace(".wav", ".pcm"));
					if(outFile.exists()&&file_pcm.exists()){
						File newNameFile_wav=new File(U.DATA_DIRECTORY+mFilename.substring(mFilename.lastIndexOf("/")));
						File newNameFile_pcm=new File((U.DATA_DIRECTORY+mFilename.substring(mFilename.lastIndexOf("/"))).replace(".wav", ".pcm"));
						outFile.renameTo(newNameFile_wav);
						file_pcm.renameTo(newNameFile_pcm);
						
					}
					waveView1.setFlag(flagPositions);
					//需要从新加载界面
//					mFilename=outFile.getAbsolutePath();
					initWaveView();
					waveView.clearPosition();
					
					break;
				case 4:
					totalTime=waveView.pixelsToMillisecsTotal()/1000;
					waveView1.setFlag(flagPositions);
					timeSize();
					break;
					
				case 100:
					//切割段全选，则退出当前界面或者取消停留在本页面
					deletAll();
					break;
					
				default:
					break;
				}
	    	};
	    };
	    
	    
	    private int mTimeCounter=-1;
	    private Timer timer_speed;
	 	private Thread timerCounter= new Thread(new Runnable() {  
	 		@Override  
	        public void run() {  
	            try {
	            TimerTask timerTask_speed = new TimerTask() {  
	              @Override  
	              public void run() { 
	 				if(mTimeCounter!=-1){
	 					mTimeCounter=mTimeCounter+1;
	 					myHandler.sendEmptyMessage(1);
	             	 }
	              		}
	                 };  
	                 if(timer_speed==null){
	                	 timer_speed = new Timer();  
	                 }
	                timer_speed.schedule(timerTask_speed, 0, 10); 
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	              
	        }  
	    });
		private RelativeLayout bottomLayout;
		private ImageButton cutBtn;
		private ProgressDialog mProgressDialog;
		private Handler mHandler;
		private TextView timeShow;
		private List<float[]> cutPostion;
		private List<long[]> cutPostion_temp=new ArrayList<long[]>();
		private List<long[]> cutPostion_use=new ArrayList<long[]>();
		private List<long[]> cutPostion_use1=new ArrayList<long[]>();
		private List<String> cutPaths=new ArrayList<String>();
		private LinearLayout ll_wave_content1;
		private LinearLayout ll_wave_content;
		private LinearLayout timeLine1;
		private int totleLength;
		private View play;
		private View pause;
		private TextView tv_totalTime;
		private LinearLayout ll_bottom;
		private String flags;
		private String[] flagsPositions;
		private String[] flagsPositions_sub;
		private String positions="";
		private boolean isEdit=false;
		private String fileName;
		private AlertDialog alertDialog1;
	
		public class PopupWindows extends PopupWindow{

			private String newName;
			private EditText reName;

			public PopupWindows(Context mContext, View parent){
				View view = View.inflate(mContext, R.layout.pop_info, null);
				view.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_ins));
				LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
				LinearLayout ll_save = (LinearLayout) view.findViewById(R.id.ll_save_audio);
				
				reName = (EditText)view.findViewById(R.id.ed_rename);
				TextView rightBtn = (TextView)view.findViewById(R.id.tv_right_btn);
				rightBtn.setText("上传");
				LinearLayout ll_upload = (LinearLayout) view.findViewById(R.id.ll_upload);
				ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_bottom_in_2));
				setWidth(LayoutParams.MATCH_PARENT);
				setHeight(LayoutParams.WRAP_CONTENT);
				setFocusable(true);
				setOutsideTouchable(true);
				setContentView(view);
				showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
				update();
				
				//保存操作
				ll_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						newName = reName.getText().toString();
						if(newName!=null&&!"".equals(newName)){
							
						}else{
							newName=System.currentTimeMillis()+"";
						}
						File oldWav=new File(mFilename);
						File newNameFile_wav=null;
						if(oldWav.exists()){
							newNameFile_wav=new File(U.DATA_DIRECTORY+newName+".wav");
//							File newNameFile_pcm=new File(U.DATA_DIRECTORY+newName+".pcm");
							oldWav.renameTo(newNameFile_wav);
//							oldPcm.delete();
						}else{
							Toast.makeText(AudioEditActivity.this, "你操作的文件不存在！", 0).show();
						}
						//保存视频录制的总时长
						setInfor(newName);
						saveTimeFlag(newName);
						
						Intent intentEdit=new Intent();
						intentEdit.putExtra("audioEditPath", newNameFile_wav.getAbsolutePath());
						setResult(3, intentEdit);//2任意
						AudioEditActivity.this.finish();
						dismiss();
					}
				});
				
				//上传操作
				ll_upload.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						newName = reName.getText().toString();
						if(newName!=null&&!"".equals(newName)){
							
						}else{
							newName=System.currentTimeMillis()+"";
						}
						File oldWav=new File( mFilename);
						if(oldWav.exists()){
							File newNameFile_wav=new File(U.DATA_DIRECTORY+newName+".wav");
//							File newNameFile_pcm=new File(U.DATA_DIRECTORY+newName+".pcm");
							oldWav.renameTo(newNameFile_wav);
//							oldPcm.delete();
//							oldPcm.renameTo(newNameFile_pcm);
						}else{
							Toast.makeText(AudioEditActivity.this, "你操作的文件不存在！", 0).show();
						}
						setInfor(newName);
						saveTimeFlag(newName);
						dismiss();
					}
				});
			}
		}

		/**
		 * 录制信息进行保存操作
		 */
		private  void setInfor(String name){
			
			SharedPreferences sp = getSharedPreferences(name+"wav", MODE_PRIVATE);
			Editor edit = sp.edit();
			int totleTime = waveView.pixelsToMillisecsTotal();
			edit.putInt("totle_time", totleTime);
			edit.commit();
			
		}
		
		/**
		 * 保存打标记的点
		 * @param name SP .xml
		 */
		private void saveTimeFlag(String name){
			
			SharedPreferences sp=getSharedPreferences(name, MODE_PRIVATE);
			
			if(flagPositions.size()>0){
				for(int i=0;i<flagPositions.size();i++){
					if(flagPositions.get(i)!=0){
						if(i!=(flagPositions.size()-1)){
							positions = positions+flagPositions.get(i)+",";
						}else{
							positions=positions+flagPositions.get(i);
						}
					}
				}
				Editor edit = sp.edit();
				edit.clear();
				edit.putString("flags", positions);
				edit.putInt("size", flagPositions.size());
				edit.commit();
			}
			
			
		}
		
		
		
		
		
		 /**
		 * 显示是否进行删除操作的提示
		 */
		private void deletAll(){
			if (alertDialog1 == null) {
				alertDialog1 = new AlertDialog.Builder(this)
						.create();
				 OnKeyListener keylistener = new DialogInterface.OnKeyListener() {  
			        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {  
			            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
			                return true;  
			            } else {  
			                return false;  
			            }  
			        }  
			    }; 
			    alertDialog1.setOnKeyListener(keylistener);//保证按返回键的时候alertDialog也不会消失
				
			}
			alertDialog1.show();
			View tipView = View.inflate(this, R.layout.edit_alert_layout, null);
			TextView tv_tip=(TextView) tipView.findViewById(R.id.tv_tip);
			tv_tip.setText("确认要全部删除吗？");
			View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
			//不再提醒
			tv_yes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
					
				}
			});
			tipView.findViewById(R.id.tv_no).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//停留在当前界面
					alertDialog1.dismiss();
					
				}
			});
			alertDialog1.setContentView(tipView);

		}
		
		
		
}
