package com.tian.videomergedemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.tian.videomergedemo.adapter.TimeAdapter;
import com.tian.videomergedemo.inter.ScrollViewListener;
import com.tian.videomergedemo.utils.AudioUtils;
import com.tian.videomergedemo.utils.CheapWAV;
import com.tian.videomergedemo.utils.DateUtils;
import com.tian.videomergedemo.utils.DensityUtil;
import com.tian.videomergedemo.utils.IsNonEmptyUtils;
import com.tian.videomergedemo.utils.SamplePlayer;
import com.tian.videomergedemo.utils.SoundFile;
import com.tian.videomergedemo.utils.U;
import com.tian.videomergedemo.utils.UserToast;
import com.tian.videomergedemo.view.ObservableScrollView;
import com.tian.videomergedemo.view.ProgressView_audio;
import com.tian.videomergedemo.view.WaveCanvas;
import com.tian.videomergedemo.view.WaveSurfaceView;
import com.tian.videomergedemo.view.WaveformView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 音频录制转格式
 * @author afnasdf
 *
 */
public class AudioActivity extends Activity implements OnClickListener{
	
	private List<Float> cutPostion_time=new ArrayList<Float>();
	private List<float[]> cut_times=new ArrayList<float[]>();
	private TextView timeCounter;
	private ImageView recordBtn;
	private boolean isRecord=false;
	private static final int FREQUENCY = 16000;// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	private static final int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;// 设置单声道声道
	private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;// 音频数据格式：每个样本16位
	public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;// 音频获取源
	private String mFileName = "test";//文件名
	private int mTimeCounter=-1;
	
	private List<Integer> timeFlag=new ArrayList<Integer>();//存储的是时间节点
	private int currentStatus=0;//默认为没在录制状态；1：录制状态；2为暂停装填；3为录制结束状态
	 
	private boolean isPause=false;//默认为非暂停操作
	private RelativeLayout bottomLayout;
	private RecyclerView time_coder;
//	private ProgressView_audio ssprogressView;
	private int maxRecordTime=60*60;
			
	private boolean isEditOrSave=false;//保存还是编辑(默认为保存)
	
	private Handler mHandler=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1://时间记录
				if(mTimeCounter==-1){
					timeCounter.setText("00:00:00");
					time_coder.scrollToPosition(0);
					mScrollView.scrollTo(0, 0);
					audio_progress.clearPausePoints();//清除标记点
				}else{
					time_coder.scrollBy(DensityUtil.dip2px(60)/10, 0);
					timeCounter.setText(DateUtils.formatSecond(mTimeCounter/1000));
				}
				break;
				
			case 2:
				if(!isEdit){
					mScrollView.scrollBy(DensityUtil.dip2px(60)/10, 0);
				}else{
					timeCounter.setText(DateUtils.formatSecond(totalTime/1000));
					mScrollView.scrollTo(currentX, 0);
					isEdit=false;
				}
				break;
			case 3:
					timeCounter.setText(DateUtils.formatSecond(totalTime/1000));
					if(totalTime==0){
						time_coder.scrollToPosition(0);
					}else{
						time_coder.scrollBy(currentX1, 0);
					}
				break;
				
			case 10:
//				if(!isEditOrSave){
////					//保存操作（关闭本页面）
////					Intent intent=new Intent();
////					intent.putExtra("audioPath", outFile.getAbsolutePath());
////					setResult(2, intent);//2任意
////					AudioActivity.this.finish();
//				}else{
					//跳转到编辑界面
//					Intent intent = new Intent(AudioActivity.this,AudioEditActivity.class);
//					intent.setData(Uri.parse(outFile.getAbsolutePath()));
//					intent.putExtra("time", totalTime/1000);
//					startActivity(intent);
//					finish();
//				}
				break;
			default:
				break;
			}
		};
	};
	private int swidth;
	private ImageView switchBtnOff;
	private ImageView switchBtnOn;
	
	private int currentX=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_recorder);
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        swidth = metrics.widthPixels;
		timerCounter.start();
		  initView();
	        U.createDirectory();
	        if(waveSfv != null) {
	            waveSfv.setLine_off(0);
	            //解决surfaceView黑色闪动效果
	            waveSfv.setZOrderOnTop(true);
	            waveSfv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	        }
	        waveView.setLine_offset(0);
	    }
	
	
	    /**
	     * 初始化页面控件
	     */
	    private void initView() {
	    	time_coder = (RecyclerView)this.findViewById(R.id.time_coder);
	    	time_coder.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
	    	time_coder.setAdapter(new TimeAdapter((swidth-DensityUtil.dip2px(10))/2));
	    	time_coder.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//取消触摸
					return true;
				}
			});
	    	
	         waveSfv = (WaveSurfaceView)this.findViewById(R.id.wavesfv);
	    	 switchBtn = (RelativeLayout)this.findViewById(R.id.iv_record);
	    	 switchBtn.setOnClickListener(this);
	    	 
	    	 
	    	 switchBtnOff = (ImageView)this.findViewById(R.id.iv_record_off);
//	    	 switchBtnOff.setOnClickListener(this);
	    	 switchBtnOn = (ImageView)this.findViewById(R.id.iv_record_on);
//	    	 switchBtnOn.setOnClickListener(this);
	    	 audio_progress = (ProgressView_audio)this.findViewById(R.id.audio_progress);
	    	 LinearLayout ll_content=(LinearLayout)this.findViewById(R.id.ll_content);
	    	 ll_content.setPadding((swidth-DensityUtil.dip2px(10))/2, 0, (swidth-DensityUtil.dip2px(10))/2, 0);
	    	 mScrollView = (ObservableScrollView)this.findViewById(R.id.hlv_scroll);
	    	 mScrollView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//取消触摸
					return true;
				}
			});
	    	 mScrollView.setScrollViewListener(new ScrollViewListener() {
				
				@Override
				public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
						int oldx, int oldy,boolean i) {
						//获取当前滑动的距离
						currentX=x;
					
				}
			});
	    	 audio_progress.setMaxRecordTime(maxRecordTime);
	    	 waveView = (WaveformView)this.findViewById(R.id.waveview);
	    	 audioMaker = (ImageView)this.findViewById(R.id.iv_record_mark);//音频标记按钮
	    	 audioMaker.setOnClickListener(this);
	    	 audioControl = (ImageView)this.findViewById(R.id.iv_record_control);
	    	 audioControl.setOnClickListener(this);
	    	 timeCounter=(TextView)this.findViewById(R.id.tv_record_time);//计时
	    	 audioMaker.setBackgroundResource(R.drawable.ic_record_mark);
	    	 audioControl.setBackgroundResource(R.drawable.ic_record_contral);
	    	 ImageView titleBack=(ImageView)this.findViewById(R.id.iv_back);
	    	 titleBack.setOnClickListener(this);
	    	 bottomLayout = (RelativeLayout)this.findViewById(R.id.rl_all);
		}
	    private int totalTime=0;
	    @Override
		public void onClick(View view) {
	    	switch (view.getId()) {
	        case R.id.iv_record:
	        if (waveCanvas == null || !waveCanvas.isRecording) {
	        	currentStatus=1;
	        	mTimeCounter=0;
	            switchBtnOn.setVisibility(View.VISIBLE);
	            switchBtnOff.setVisibility(View.GONE);
	            waveSfv.setVisibility(View.VISIBLE);
	            audioMaker.setBackgroundResource(R.drawable.ic_record_can_maker);
	            audioControl.setBackgroundResource(R.drawable.ic_record_audio_op);
	            waveView.setVisibility(View.INVISIBLE);
	            initAudio();
	        } else {
	        	//录制(暂停操作)
	        	switch (currentStatus) {
				case 0:
//					Toast.makeText(AudioActivity.this, "请选择其他操作！", Toast.LENGTH_SHORT).show();
					break;
				case 1://录制过程可对其进行暂停或开始操作
					currentStatus=2;
					totalTime=mTimeCounter;
	        		waveCanvas.pause();
	        		audioMaker.setBackgroundResource(R.drawable.ic_record_audio_del);
	        		cutPostion_time.add(mTimeCounter*1.0f/1000);//记录暂停的时间点
	        		audio_progress.addPausePoint((float) currentX);
	        		switchBtnOn.setVisibility(View.GONE);
		            switchBtnOff.setVisibility(View.VISIBLE);
					break;
				case 2:
					currentStatus=1;
					mTimeCounter=totalTime;
	        		waveCanvas.reStart();
	        		audioMaker.setBackgroundResource(R.drawable.ic_record_can_maker);
	        		switchBtnOn.setVisibility(View.VISIBLE);
		            switchBtnOff.setVisibility(View.GONE);
					break;
				case 3:
					
					//TODO
					currentStatus=1;
		        	mTimeCounter=0;
		            switchBtnOn.setVisibility(View.VISIBLE);
		            switchBtnOff.setVisibility(View.GONE);
		            waveSfv.setVisibility(View.VISIBLE);
		            audioMaker.setBackgroundResource(R.drawable.ic_record_can_maker);
		            audioControl.setBackgroundResource(R.drawable.ic_record_audio_op);
		            waveView.setVisibility(View.INVISIBLE);
		            initAudio();
					break;
				default:
					break;
				}
	        }
	            break;
//	        case R.id.play:
//	               onPlay(0);
//	            break;
	        case R.id.iv_record_mark://进行音频的标记操作
	        	switch (currentStatus) {
				case 0://初始状态
					currentStatus=0;
					break;
				case 1://录制状态(action 为打标记)
					currentStatus=1;
					timeFlag.add(mTimeCounter);
					waveCanvas.addCurrentPostion();
					break;
				case 2://暂停状态
					currentStatus=2;
					//进行音频的回删操作
					//删除最近的一段
					List<Float> pausePoint = audio_progress.getPausePoint();
					delAudio(pausePoint);
					
					break;
				case 3://录制完成状态（action 为删除）
					timeFlag.clear();//清除时间点
					currentStatus=0;
					waveSfv.setVisibility(View.INVISIBLE);
					audioMaker.setBackgroundResource(R.drawable.ic_record_mark);
					audioControl.setBackgroundResource(R.drawable.ic_record_contral);
					File mFile1 = new File(U.DATA_DIRECTORY + mFileName + ".wav");
					File mFile2 = new File(U.DATA_DIRECTORY + mFileName + ".pcm");
					if(mFile1.exists()&&mFile2.exists()){
						mFile1.delete();
						mFile2.delete();
					}
					 mHandler.sendEmptyMessage(1);
					 waveSfv.setVisibility(View.VISIBLE);
			         waveView.setVisibility(View.INVISIBLE);
					break;

				default:
					break;
				}
	        	break;
	        case R.id.iv_record_control://录制音频过程中的暂停和重新开始的操作
	        	//录制完成，音频进行展示界面
	        	switch (currentStatus) {
				case 0://初始状态
					currentStatus=0;
					break;
				case 1://录制状态(action 为打标记)
					totalTime=mTimeCounter;
		        	mTimeCounter=-1;
		        	currentStatus=3;
		        	switchBtnOn.setVisibility(View.GONE);
		            switchBtnOff.setVisibility(View.VISIBLE);
		        	audioMaker.setBackgroundResource(R.drawable.ic_record_audio_del);
		        	audioControl.setBackgroundResource(R.drawable.ic_record_audio_save);
		        	waveCanvas.Stop();
		        	waveCanvas.clearMarkPosition();//录制完成后需要清除标记点
		            waveCanvas = null;
		            initWaveView();
					break;
				case 2://暂停状态
					totalTime=mTimeCounter;
		        	mTimeCounter=-1;
		        	currentStatus=3;
		        	switchBtnOn.setVisibility(View.GONE);
		            switchBtnOff.setVisibility(View.VISIBLE);
		        	audioMaker.setBackgroundResource(R.drawable.ic_record_audio_del);
		        	audioControl.setBackgroundResource(R.drawable.ic_record_audio_save);
		        	waveCanvas.Stop();
		        	waveCanvas.clearMarkPosition();//录制完成后需要清除标记点
		            waveCanvas = null;
		            initWaveView();
		            break;
				case 3://录制完成状态（action 为删除）
					new PopupWindows(AudioActivity.this,bottomLayout);
					break;

				default:
					break;
				}
	        	break;
	        	
	        case R.id.iv_back:
	        	//返回按钮的退出操作（退出之前需要将，录制的文件删除）
	        	File mFile1 = new File(U.DATA_DIRECTORY + mFileName + ".wav");
				File mFile2 = new File(U.DATA_DIRECTORY + mFileName + ".pcm");
				if(mFile1.exists()&&mFile2.exists()){
					mFile1.delete();
					mFile2.delete();
				}
	        	finish();
	        	break;
	        	
	    }
		}
	    
	    private boolean isEdit=false;
	    
	    /**
	     * 音频回删操作
	     * @param pausePoint
	     */
	    private void delAudio(List<Float> pausePoint) {
	    	if(pausePoint.size()>1){
	    		//多段音频
	    		float position1=pausePoint.get(pausePoint.size()-1);
	    		float position2=pausePoint.get(pausePoint.size()-2);
	    		currentX1 = (int) (position2-position1);
	    		currentX = (int) (position2);
	    		float[] cut_time=new float[2];
	    		cut_time[1]=cutPostion_time.get(cutPostion_time.size()-1);
	    		cut_time[0]=cutPostion_time.get(cutPostion_time.size()-2);
	    		totalTime=(int) (cut_time[0]*1000);
	    		
	    		
	    		//删除区段标记的时间点
	    		if(timeFlag.size()>0){//注意角标越界
	    			int timeFlagSize=timeFlag.size()-1;
	    			while((timeFlagSize>=0)&&totalTime<=timeFlag.get(timeFlagSize)){
	    				timeFlag.remove(timeFlagSize);
	    				timeFlagSize=timeFlag.size()-1;
		    		}
	    		}
	    		cut_times.add(cut_time);
	    		cutPostion_time.remove(cutPostion_time.size()-1);//删除最后一个时间点
	    		pausePoint.remove(pausePoint.size()-1);//删除最后标记点
	    		audio_progress.setPausePoint(pausePoint);
	    		
	    	}else{
	    		//只有一段音频
	    		float position1=pausePoint.get(pausePoint.size()-1);
	    		float[] cut_time=new float[2];
	    		cut_time[1]=cutPostion_time.get(cutPostion_time.size()-1);
	    		cut_time[0]=0;
	    		currentX=(int) (0-position1);
	    		totalTime=(int) (cut_time[0]*1000);
	    		cut_times.add(cut_time);
	    		pausePoint.remove(pausePoint.size()-1);//删除最后标记点
	    		audio_progress.setPausePoint(pausePoint);
	    		timeFlag.clear();//删除最后一段，需删除所标记时间点
	    		//所有状态归0
	    		totalTime=0;
	        	mTimeCounter=-1;
	        	currentStatus=0;
	        	switchBtnOn.setVisibility(View.GONE);
	            switchBtnOff.setVisibility(View.VISIBLE);
	        	waveCanvas.Stop();
	        	waveCanvas.clear();//清除数据
	        	waveCanvas.clearMarkPosition();//录制完成后需要清除标记点
	        	waveSfv.setVisibility(View.INVISIBLE);
		         waveView.setVisibility(View.INVISIBLE);
	    		//最后一段删除后，从新开始录制
				audioMaker.setBackgroundResource(R.drawable.ic_record_mark);
				audioControl.setBackgroundResource(R.drawable.ic_record_contral);
				
				
				File mFile1 = new File(U.DATA_DIRECTORY + mFileName + ".wav");
				File mFile2 = new File(U.DATA_DIRECTORY + mFileName + ".pcm");
				if(mFile1.exists()&&mFile2.exists()){
					mFile1.delete();
					mFile2.delete();
				}
				cut_times.clear();//当最后一段删除的时候，重新录制，不需要进行合并操作
				cutPostion_time.clear();//删除暂停计时点
	    	}
	    	
	    	waveSfv.setVisibility(View.VISIBLE);
	        waveView.setVisibility(View.INVISIBLE);
	    	isEdit=true;
	    	mHandler.sendEmptyMessage(3);
	    	mHandler.sendEmptyMessage(2);
		}


		private void  initWaveView(){
			loadFromFile();
	    }

	    File mFile;
	    Thread mLoadSoundFileThread;
	    SoundFile mSoundFile;
	    boolean mLoadingKeepGoing;
	    SamplePlayer mPlayer;
	    /** 载入wav文件显示波形 */
	    private void loadFromFile() {
	        try {
	            Thread.sleep(300);//让文件写入完成后再载入波形 适当的休眠下
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        mFile = new File(U.DATA_DIRECTORY + mFileName + ".wav");
	        mLoadingKeepGoing = true;
	        // Load the sound file in a background thread
	        mLoadSoundFileThread = new Thread() {
	            public void run() {
	                try {
	                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(),null);
	                    if (mSoundFile == null) {
	                        return;
	                    }
	                    mPlayer = new SamplePlayer(mSoundFile);
	                } catch (final Exception e) {
	                    e.printStackTrace();
	                    return;
	                }
	                if (mLoadingKeepGoing) {
	                    Runnable runnable = new Runnable() {
	                        public void run() {
	                            finishOpeningSoundFile();
//	                            waveSfv.setVisibility(View.INVISIBLE);
//	                            waveView.setVisibility(View.VISIBLE);
	                        }
	                    };
	                    AudioActivity.this.runOnUiThread(runnable);
	                }
	            }
	        };
	        mLoadSoundFileThread.start();
	    }



	    float mDensity;
	    /**waveview载入波形完成*/
	    private void finishOpeningSoundFile() {
	        waveView.setSoundFile(mSoundFile);
	        waveView.recomputeHeights(mDensity);
	    }

	    /**
	     * 初始化录音
	     */
	    private void initAudio(){
	    	timeFlag.clear();
	        recBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
	                CHANNELCONGIFIGURATION, AUDIOENCODING);//设置录音缓冲区(一般为20ms,1280)
	        audioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
	                FREQUENCY, // 16000HZ采样频率
	                CHANNELCONGIFIGURATION,// 录制通道
	                AUDIO_SOURCE,// 录制编码格式
	                recBufSize);
	        waveCanvas = new WaveCanvas();
	        waveCanvas.baseLine = waveSfv.getHeight() / 2;
	        waveCanvas.Start(audioRecord, recBufSize, waveSfv, mFileName, U.DATA_DIRECTORY, new Handler.Callback() {
	            @Override
	            public boolean handleMessage(Message msg) {
	                return true;
	            }
	        },(swidth-DensityUtil.dip2px(10))/2,this);
	    }




	    private int mPlayStartMsec;
	    private int mPlayEndMsec;
	    private final int UPDATE_WAV = 100;
	    /**播放音频，@param startPosition 开始播放的时间*/
	    private synchronized void onPlay(int startPosition) {
	        if (mPlayer == null)
	            return;
	        if (mPlayer != null && mPlayer.isPlaying()) {
	            mPlayer.pause();
	            updateTime.removeMessages(UPDATE_WAV);
	        }
	            mPlayStartMsec = waveView.pixelsToMillisecs(startPosition);
	            mPlayEndMsec = waveView.pixelsToMillisecsTotal();
	            mPlayer.setOnCompletionListener(new SamplePlayer.OnCompletionListener() {
	                @Override
	                public void onCompletion() {
	                    waveView.setPlayback(-1);
	                    updateDisplay();
	                    updateTime.removeMessages(UPDATE_WAV);
	                    Toast.makeText(getApplicationContext(),"播放完成",Toast.LENGTH_LONG).show();
	                }
	            });
	            mPlayer.seekTo(mPlayStartMsec);
	            mPlayer.start();
	            Message msg = new Message();
	            msg.what = UPDATE_WAV;
	            updateTime.sendMessage(msg);
	    }

	    Handler updateTime = new Handler() {
	        public void handleMessage(Message msg) {
	            updateDisplay();
	            updateTime.sendMessageDelayed(new Message(), 10);
	        };
	    };
	    
		private WaveSurfaceView waveSfv;
		private WaveformView waveView;
		private int recBufSize;
		private AudioRecord audioRecord;
		private WaveCanvas waveCanvas;
		private RelativeLayout switchBtn;
		private ImageView audioMaker;
		private ImageView audioControl;

	    /**更新upd
	     * ateview 中的播放进度*/
	    private void updateDisplay() {
	            int now = mPlayer.getCurrentPosition();// nullpointer
	            int frames = waveView.millisecsToPixels(now);
	            waveView.setPlayback(frames);//通过这个更新当前播放的位置
	            if (now >= mPlayEndMsec ) {
	                waveView.setPlayFinish(1);
	                if (mPlayer != null && mPlayer.isPlaying()) {
	                    mPlayer.pause();
	                    updateTime.removeMessages(UPDATE_WAV);
	                }
	            }else{
	                waveView.setPlayFinish(0);
	            }
	            waveView.invalidate();//刷新真个视图
	    }

	    
	    private Timer timer_speed;
	 	private Thread timerCounter= new Thread(new Runnable() {  
	 		@Override  
	        public void run() {  
	            try {
	            TimerTask timerTask_speed = new TimerTask() {  
	              @Override  
	              public void run() { 
	 				if(mTimeCounter!=-1&&currentStatus!=2){
	 					mTimeCounter=mTimeCounter+100;
	 					mHandler.sendEmptyMessage(1);
	 					mHandler.sendEmptyMessage(2);
	             	 }
	              		}
	                 };  
	                 if(timer_speed==null){
	                	 timer_speed = new Timer();  
	                 }
	                	 timer_speed.schedule(timerTask_speed, 0, 100); 
	                
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    });
		private ProgressView_audio audio_progress;
		private ObservableScrollView mScrollView;
		private int currentX1;
		


		@Override
		protected void onPause() {
			if(timer_speed!=null){
				timer_speed.cancel();
				timer_speed=null;
			}
			
			if(waveCanvas!=null){
				waveCanvas.Stop();
				waveCanvas.clear();
				waveCanvas=null;
			}
			super.onPause();
		}

		public class PopupWindows extends PopupWindow{

			private String newName;
			private EditText reName;

			public PopupWindows(Context mContext, View parent){
				View view = View.inflate(mContext, R.layout.pop_info, null);
				view.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.fade_ins));
				LinearLayout ll_popup = (LinearLayout) view
						.findViewById(R.id.ll_popup);
				LinearLayout ll_save = (LinearLayout) view
						.findViewById(R.id.ll_save_audio);
				
				reName = (EditText)view.findViewById(R.id.ed_rename);
				reName.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before,int count) {
						String content=reName.getText().toString();
						if(IsNonEmptyUtils.isString(content)){
							if(content.length()>=10){
								UserToast.toSetToast(AudioActivity.this, "请在10个字以内");
							}
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,int after) {
					}
					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				LinearLayout ll_upload = (LinearLayout) view
						.findViewById(R.id.ll_upload);
				ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
						R.anim.push_bottom_in_2));
				setWidth(LayoutParams.MATCH_PARENT);
				setHeight(LayoutParams.WRAP_CONTENT);
				setFocusable(true);
				setOutsideTouchable(true);
				
				setContentView(view);
				showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
				update();
				ll_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						newName = reName.getText().toString();
						if(newName!=null&&!"".equals(newName)){
							
						}else{
							newName=System.currentTimeMillis()+"";
						}
						 mFile = new File(U.DATA_DIRECTORY + mFileName + ".wav");
						File oldPcm=new File(U.DATA_DIRECTORY + mFileName + ".pcm");
						if(mFile.exists()&&oldPcm.exists()){
							File newNameFile_wav=new File(U.DATA_DIRECTORY+newName+".wav");
//							File newNameFile_pcm=new File(U.DATA_DIRECTORY+newName+".pcm");
							mFile.renameTo(newNameFile_wav);
							oldPcm.delete();
						}else{
							Toast.makeText(AudioActivity.this, "你操作的文件不存在！", 0).show();
						}
						//保存视频录制的总时长
						isEditOrSave=false;
						dismiss();
						saveTimeFlag(newName);
						saveRingtone(newName);
						
						
					}
				});
				ll_upload.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						newName = reName.getText().toString();
						if(newName!=null&&!"".equals(newName)){
							
						}else{
							newName=System.currentTimeMillis()+"";
						}
						 mFile = new File(U.DATA_DIRECTORY + mFileName + ".wav");
						File oldPcm=new File(U.DATA_DIRECTORY + mFileName + ".pcm");
						if(mFile.exists()&&oldPcm.exists()){
							File newNameFile_wav=new File(U.DATA_DIRECTORY+newName+".wav");
							mFile.renameTo(newNameFile_wav);
							oldPcm.delete();//删除源数据源
						}else{
							Toast.makeText(AudioActivity.this, "你操作的文件不存在！", 0).show();
						}
						isEditOrSave=true;
						dismiss();
						saveTimeFlag(newName);
						saveRingtone(newName);
						
						
						
						
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
		private List<long[]> cutPostion_temp=new ArrayList<long[]>();
		private List<long[]> cutPostion_use=new ArrayList<long[]>();
		private List<long[]> cutPostion_use1=new ArrayList<long[]>();
		private ProgressDialog mProgressDialog;
		private List<String> cutPaths=new ArrayList<String>();
		protected File outFile;
		private String positions="";
		
		
		
		/**
		 * 在保存的同时进行音频的删除操作
		 * @param title
		 */
		private void saveRingtone(final String name) {
	    	final String outPath =U.DATA_DIRECTORY+ name + ".wav";
	        mProgressDialog = new ProgressDialog(this);
	        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        mProgressDialog.setTitle("操作中");
	        mProgressDialog.setIndeterminate(true);
	        mProgressDialog.setCancelable(false);
	        mProgressDialog.show();

	        new Thread() { 
				public void run() { 
	                outFile = new File(outPath);
	                try {
	                	CheapWAV a=new CheapWAV();
	                	a.ReadFile(new File(U.DATA_DIRECTORY + name + ".wav"));//加载wav文件
	                	int numFrames = a.getNumFrames();//获取音频文件总帧数
	                	
	                	cutPostion_temp.clear();
	         	        for(int i=0;i<cut_times.size();i++){
	         	        	long[] temp_fs=new long[2];
	         	        	float[] fs = cut_times.get(i);
	         	        	//计算切割帧数区间
	         	        	
	         	        	temp_fs[0] = waveView.secondsToFrames(fs[0]);
	         	            temp_fs[1] = waveView.secondsToFrames(fs[1]);
	         	        	
//	         	        	 temp_fs[0] = (long) (fs[0]*numFrames/(totalTime/1000));
//	         	             temp_fs[1] = (long) (fs[1]*numFrames/(totalTime/1000));
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
	        			
	        			File file=new File(U.DATA_DIRECTORY + mFileName + ".wav");
	        			if(file.exists()){
	        				file.delete();
	        			}
	        			
	        			//合并剪贴的片段文件
	        			if(cutPaths.size()>0){
	        				AudioUtils.mergeAudioFiles(outFile.getAbsolutePath(), cutPaths);
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
	                	mHandler.sendEmptyMessage(10);
	                	
	                } catch (Exception e) {
	                    mProgressDialog.dismiss();
	                    e.printStackTrace();
	                }
	                mProgressDialog.dismiss();
	            }
	        }.start();
	    }
		
		
		
		
		
		/**
		 * 保存打标记的点
		 * @param name SP .xml
		 */
		private void saveTimeFlag(String name){
			
			SharedPreferences sp=getSharedPreferences(name, MODE_PRIVATE);
			if(timeFlag.size()>0){
				for(int i=0;i<timeFlag.size();i++){
					if(i!=(timeFlag.size()-1)){
						positions = positions+timeFlag.get(i)+",";
					}else{
						positions=positions+timeFlag.get(i);
					}
				}
				Editor edit = sp.edit();
				edit.putString("flags", positions);
				edit.putInt("size", timeFlag.size());
				edit.commit();
			}
			
			
		}
		
		
		
		
	 	
	 	
	}
