package com.tian.videomergedemo;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ksyun.media.shortvideo.kit.KSYRecordKit;
import com.ksyun.media.streamer.capture.CameraCapture;
import com.ksyun.media.streamer.capture.camera.CameraTouchHelper;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ksyun.media.streamer.logstats.StatsLogReport;
import com.tian.videomergedemo.bean.RecordDetail;
import com.tian.videomergedemo.bean.ShortVideoConfig;
import com.tian.videomergedemo.dao.DatabaseContext;
import com.tian.videomergedemo.dao.RecordDao;
import com.tian.videomergedemo.inter.CompletionListener;
import com.tian.videomergedemo.manager.FfmpegManager;
import com.tian.videomergedemo.manager.VideoStitchingRequest;
import com.tian.videomergedemo.utils.SPConstant;
import com.tian.videomergedemo.utils.SelectPicDialog;
import com.tian.videomergedemo.utils.SelectPicDialog.OnSelectPicOptionClick;
import com.tian.videomergedemo.view.CameraHintView;
import com.tian.videomergedemo.view.RecordProgressController;

/**
 * 视频录制界面
 * @author Administrator
 *
 */
public class RecordActivity extends Activity implements OnClickListener {
	
	public static final int MAX_DURATION = 5 * 60 * 1000;
    public static final int MIN_DURATION = 5 * 1000;
	private ImageView flash;
	private ImageView camera;
	private ImageView clock;
	private TextView tv_resolution;
	private int screenWidth;
	private int screenHeight;
	private CameraHintView mCameraHintView;
	private GLSurfaceView mCameraPreviewView;
	private ImageView mPointerMaker;
	private ImageView mRecordControler;
	private ImageView mRecordStop;
	private Chronometer mChronometer;
	private SelectPicDialog selectPicDialog;
	private ShortVideoConfig mShortVideoConfig;
	private KSYRecordKit mKSYRecordKit;
	private Handler mMainHandler;
	private static final int MERGER_OK=1;

	private static final String fileFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/jwzt_recorder";
	
	private static String TAG = "RecordActivity";

	private ArrayList<String> videosToMerge=new ArrayList<String>();
	private String finalPath;
	public static final String ACTION = "composeFinish";
	



    private Handler mHandler=new Handler(){
    	
    	public void dispatchMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MERGER_OK:
				break;
			default:
				break;
			}
    		
    	};
    	
    };
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//保持屏幕长亮
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_video_record);
		WindowManager windowManager = (WindowManager) getApplication().
                getSystemService(getApplication().WINDOW_SERVICE);
		screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
		
        initData();
        
        initView();
		
		
		
	}

	/**
	 * 初始化默认录制参数
	 */
	private void initData() {
		mShortVideoConfig = new ShortVideoConfig();
        //帧率   
		mShortVideoConfig.fps = 20;
		//视频的码率
        mShortVideoConfig.videoBitrate = 1000;
        //音频的码率
        mShortVideoConfig.audioBitrate = 64;
        //默认的视频录制的分辨率（480）
        mShortVideoConfig.resolution = StreamerConstants.VIDEO_RESOLUTION_480P;
        //H264编码
        mShortVideoConfig.encodeType = AVConst.CODEC_ID_AVC;
        //功率（默认平衡模式）
        mShortVideoConfig.encodeProfile = VideoEncodeFormat.ENCODE_PROFILE_BALANCE;
        //默认软编模式
        mShortVideoConfig.encodeMethod = StreamerConstants.ENCODE_METHOD_SOFTWARE;
        
	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		mMainHandler = new Handler();
		mKSYRecordKit = new KSYRecordKit(this);
		ImageView back=(ImageView)this.findViewById(R.id.iv_back);
		camera = (ImageView)this.findViewById(R.id.iv_camera_switch);
		clock = (ImageView)this.findViewById(R.id.iv_clock);
		tv_resolution = (TextView)this.findViewById(R.id.tv_resolution);
		flash = (ImageView)this.findViewById(R.id.iv_flash);
		
		topView = (LinearLayout)this.findViewById(R.id.ll_top);
		
		
		mChronometer = (Chronometer)this.findViewById(R.id.tv_record_time);
		
		mCameraHintView = (CameraHintView)this.findViewById(R.id.camera_hint);
		mCameraPreviewView = (GLSurfaceView)this.findViewById(R.id.camera_preview);
		
		
		mPointerMaker = (ImageView)this.findViewById(R.id.iv_point_maker1);
		
		mRecordControler = (ImageView)this.findViewById(R.id.iv_record);
		
		mRecordStop = (ImageView)this.findViewById(R.id.iv_stop);
		
		mBarBottomLayout = (RelativeLayout)this.findViewById(R.id.rl_progress_bar);
		
		
		
		rl_root = (RelativeLayout)this.findViewById(R.id.rl_root);
		
		
		mRecordProgressCtl = new RecordProgressController(mBarBottomLayout);
		//每次进入取上次的设置的时间总长
		mRecordProgressCtl.setMaxDuration(getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, MODE_PRIVATE).getInt(SPConstant.RECORD_MAXTIME_DURATION, MAX_DURATION));
        mRecordProgressCtl.setRecordingLengthChangedListener(mRecordLengthChangedListener);
        mRecordProgressCtl.start();
		
		
		//
		mRecordControler.getDrawable().setLevel(1);
		mRecordStop.getDrawable().setLevel(1);
		mPointerMaker.getDrawable().setLevel(1);
		
		
		back.setOnClickListener(this);
		clock.setOnClickListener(this);
		camera.setOnClickListener(this);
		tv_resolution.setOnClickListener(this);
		flash.setOnClickListener(this);
		mPointerMaker.setOnClickListener(this);
		mRecordControler.setOnClickListener(this);
		mRecordStop.setOnClickListener(this);
		initCameraData();
		
		
	
	}

	
	/**
	 * 初始化相机，开始拍摄工作
	 */
	private void initCameraData() {
        mKSYRecordKit.setPreviewFps(mShortVideoConfig.fps);
        mKSYRecordKit.setTargetFps(mShortVideoConfig.fps);
        mKSYRecordKit.setVideoKBitrate(mShortVideoConfig.videoBitrate);
        mKSYRecordKit.setAudioKBitrate(mShortVideoConfig.audioBitrate);
        mKSYRecordKit.setPreviewResolution(mShortVideoConfig.resolution);
        mKSYRecordKit.setTargetResolution(mShortVideoConfig.resolution);
        mKSYRecordKit.setVideoCodecId(mShortVideoConfig.encodeType);
        mKSYRecordKit.setEncodeMethod(mShortVideoConfig.encodeMethod);
        mKSYRecordKit.setVideoEncodeProfile(mShortVideoConfig.encodeProfile);
        mKSYRecordKit.setRotateDegrees(0);
        mKSYRecordKit.setDisplayPreview(mCameraPreviewView);
        mKSYRecordKit.setEnableRepeatLastFrame(false);
        mKSYRecordKit.setCameraFacing(CameraCapture.FACING_FRONT);
        mKSYRecordKit.setFrontCameraMirror(false);
        mKSYRecordKit.setOnInfoListener(mOnInfoListener);
        mKSYRecordKit.setOnErrorListener(mOnErrorListener);
        mKSYRecordKit.setOnLogEventListener(mOnLogEventListener);
        
        
        CameraTouchHelper cameraTouchHelper = new CameraTouchHelper();
        cameraTouchHelper.setCameraCapture(mKSYRecordKit.getCameraCapture());
        mCameraPreviewView.setOnTouchListener(cameraTouchHelper);
        cameraTouchHelper.setCameraHintView(mCameraHintView);
        
        mKSYRecordKit.startCameraPreview();
		
	}

	/**
	 * 控件的点击事件处理
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back:
			//返回按钮
			this.finish();
			break;
		case R.id.iv_flash:
			//闪光灯
			onFlashClick();
			break;
			
		case R.id.iv_camera_switch:
			//摄像头切换
			onSwitchCamera();
			break;
		case R.id.iv_clock:
			//定时器(选择录制时长)
			showSelectDialog();
			break;
		case R.id.tv_resolution:
			//分辨率设置
			if(mShortVideoConfig.resolution == StreamerConstants.VIDEO_RESOLUTION_480P){
				mShortVideoConfig.resolution =StreamerConstants.VIDEO_RESOLUTION_540P;
				tv_resolution.setText("540P");
			}else if(mShortVideoConfig.resolution ==StreamerConstants.VIDEO_RESOLUTION_540P){
				mShortVideoConfig.resolution =StreamerConstants.VIDEO_RESOLUTION_720P;
				tv_resolution.setText("720P");
			}else{
				mShortVideoConfig.resolution =StreamerConstants.VIDEO_RESOLUTION_480P;
				tv_resolution.setText("480P");
			}
			mKSYRecordKit.setVideoKBitrate(mShortVideoConfig.videoBitrate);//设置视频的分辨率
			break;
		case R.id.iv_point_maker1:
			//断点标记按钮
			if(!mIsFileRecording){
				//录制暂停
				onBackoffClick();
				
			}else{
				//正在录制状态(添加标记)
				//TODO
				mRecordProgressCtl.setFlagPointer();
				Toast.makeText(RecordActivity.this, "您添加了标记点！", 0).show();
				
			}
			
			break;
		case R.id.iv_stop:
			//录制停止按钮（开始合并操作）
			stopRecord(false);
			if(videosToMerge.size()>0){
				new PopupWindows(RecordActivity.this,rl_root);
			}else{
				Toast.makeText(RecordActivity.this, "请录制视频", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.iv_record:
			//录制控制按钮
			onRecordClick();
			break;
		default:
			break;
		}
	}
	
	
	
	
	/**
	 * 返回按钮处理
	 */
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onBackoffClick();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private void onBackoffClick() {
        if (mKSYRecordKit.getRecordedFilesCount() >= 1) {
            if (!mPointerMaker.isSelected()) {
            	mPointerMaker.setSelected(true);
                mRecordProgressCtl.setLastClipPending();
            } else {
                mPointerMaker.setSelected(false);
                if (mIsFileRecording) {
                    stopRecord(false);
                }
                if(videosToMerge.size()>0){
                	videosToMerge.remove(videosToMerge.size()-1);//移除最后一个视频
                }
                //删除录制文件
                mKSYRecordKit.deleteRecordFile(mKSYRecordKit.getLastRecordedFiles());
                mRecordProgressCtl.rollback();
                updateDeleteView();
                mRecordControler.setEnabled(true);
            }
        } else {
            mChronometer.stop();
            mIsFileRecording = false;
            RecordActivity.this.finish();
        }
    }
	
	
	@Override
    public void onResume() {
        super.onResume();

        mKSYRecordKit.setDisplayPreview(mCameraPreviewView);
        mKSYRecordKit.onResume();
        mCameraHintView.hideAll();
    }
	
	@Override
    public void onPause() {
        super.onPause();
        mKSYRecordKit.onPause();//不写这行就出大事了！！！（mKSYRecordKit的后续资源就释放不掉）
        if (!mKSYRecordKit.isRecording() && !mKSYRecordKit.isFileRecording()) {
            mKSYRecordKit.stopCameraPreview();
        }
    }
	
	
	/**
	 * 注销使用的资源
	 */
	@Override
    public void onDestroy() {
        super.onDestroy();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }

        mRecordProgressCtl.stop();
        mRecordProgressCtl.setRecordingLengthChangedListener(null);
        mRecordProgressCtl.release();
        mKSYRecordKit.setOnLogEventListener(null);
        mKSYRecordKit.release();
        mKSYRecordKit=null;
        
    }
	
	
	
	private void startRecord() {
       
        mRecordUrl = getRecordFileFolder() + "/" + System.currentTimeMillis() + ".mp4";
        
        videosToMerge.add(mRecordUrl);//每次开始录制时记录
        
        mKSYRecordKit.setVoiceVolume(50);
        mKSYRecordKit.startRecord(mRecordUrl);
        mIsFileRecording = true;
        mRecordControler.getDrawable().setLevel(2);
    }
	
	
	
	private void onRecordClick() {
		topView.setVisibility(View.GONE);//隐藏标题栏
        if (mIsFileRecording) {
        	mPointerMaker.getDrawable().setLevel(2);
        	mChronometer.stop();
            stopRecord(false);
        } else {
        	mPointerMaker.getDrawable().setLevel(1);
            startRecord();
        }
        clearBackoff();
    }
	
	
	private boolean clearBackoff() {
        if (mPointerMaker.isSelected()) {
        	mPointerMaker.setSelected(false);
            mRecordProgressCtl.setLastClipNormal();
            return true;
        }
        return false;
    }
	
	
	
	private void onSwitchCamera() {
        mKSYRecordKit.switchCamera();
    }
	 private boolean mIsFlashOpened=false;
    private void onFlashClick() {
		if (mIsFlashOpened) {
            mKSYRecordKit.toggleTorch(false);
            mIsFlashOpened = false;
            flash.getDrawable().setLevel(1);
        } else {
            mKSYRecordKit.toggleTorch(true);
            mIsFlashOpened = true;
            flash.getDrawable().setLevel(2);
        }
    }
	private void showSelectDialog() {
		if (selectPicDialog == null) {
			selectPicDialog = new SelectPicDialog(this);
			selectPicDialog.setCancelable(true);
			selectPicDialog.setCanceledOnTouchOutside(true);
			selectPicDialog.setOnSelectPicClickListener(new OnSelectPicOptionClick() {
						@Override
						public void OnPicSelect(int id) {
							switch (id) {
							case R.id.tv_10s:
								RecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, RecordActivity.this.MODE_PRIVATE).edit()
								.putInt(SPConstant.RECORD_MAXTIME_DURATION, 10*1000).commit();
								mRecordProgressCtl.setMaxDuration(10*1000);//定时拍摄设置最大时间长度
								break;
							case R.id.tv_30s:
								RecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, RecordActivity.this.MODE_PRIVATE).edit()
								.putInt(SPConstant.RECORD_MAXTIME_DURATION, 30*1000).commit();
								mRecordProgressCtl.setMaxDuration(30*1000);//定时拍摄设置最大时间长度
								break;
							case R.id.tv_60s:
								RecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME, RecordActivity.this.MODE_PRIVATE).edit()
								.putInt(SPConstant.RECORD_MAXTIME_DURATION, 60*1000).commit();
								mRecordProgressCtl.setMaxDuration(60*1000);//定时拍摄设置最大时间长度
								break;
							case R.id.tv_90s:
								RecordActivity.this.getSharedPreferences(SPConstant.RECORD_MAXTIME_NAME,RecordActivity.this. MODE_PRIVATE).edit()
								.putInt(SPConstant.RECORD_MAXTIME_DURATION, 120*1000).commit();
								mRecordProgressCtl.setMaxDuration(120*1000);//定时拍摄设置最大时间长度
								break;
							case R.id.tv_120s:
								
								break;
							}
						}
					});
		}

		selectPicDialog.show();
	}

	
	private boolean isStart=false;
	
	
	//---------------------------------------------------- mOnInfoListener start
	
	private KSYStreamer.OnInfoListener mOnInfoListener = new KSYStreamer.OnInfoListener() {
        @Override
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    setCameraAntiBanding50Hz();
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_FACEING_CHANGED:
                    updateFaceunitParams();
                    break;
                case StreamerConstants.KSY_STREAMER_OPEN_FILE_SUCCESS:
                	//定时器开始
                	if(!isStart){
                		isStart=true;
                		mChronometer.setBase(SystemClock.elapsedRealtime());
                	}
                    mChronometer.start();
                    
                    //进度条开始刷新
                    mRecordProgressCtl.startRecording();
                    break;
                default:
                    break;
            }
        }
    };
	
    private void setCameraAntiBanding50Hz() {
        Camera.Parameters parameters = mKSYRecordKit.getCameraCapture().getCameraParameters();
        if (parameters != null) {
            parameters.setAntibanding(Camera.Parameters.ANTIBANDING_50HZ);
            mKSYRecordKit.getCameraCapture().setCameraParameters(parameters);
        }
    }
    
    /**
     * 美颜效果参数
     */
    private void updateFaceunitParams() {
//        if (mImgFaceunityFilter != null) {
//            mImgFaceunityFilter.setTargetSize(mKSYRecordKit.getTargetWidth(),
//                    mKSYRecordKit.getTargetHeight());
//
//            if (mKSYRecordKit.isFrontCamera()) {
//                mImgFaceunityFilter.setMirror(true);
//            } else {
//                mImgFaceunityFilter.setMirror(false);
//            }
//        }
    }
    
    
    //----------------------------------------------------end
    
    
    
    
    
    //-------------------------------------mOnErrorListener start
    
    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
        @Override
        public void onError(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:
                    Log.d(TAG, "KSY_STREAMER_ERROR_AV_ASYNC " + msg1 + "ms");
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                    Log.d(TAG, "KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED");
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                    Log.d(TAG, "KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:
                    Log.d(TAG, "KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:
                    Log.d(TAG, "KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                    Log.d(TAG, "KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    Log.d(TAG, "KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                    Log.d(TAG, "KSY_STREAMER_CAMERA_ERROR_UNKNOWN");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                    Log.d(TAG, "KSY_STREAMER_CAMERA_ERROR_START_FAILED");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    Log.d(TAG, "KSY_STREAMER_CAMERA_ERROR_SERVER_DIED");
                    break;
                //Camera was disconnected due to use by higher priority user.
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
                    Log.d(TAG, "KSY_STREAMER_CAMERA_ERROR_EVICTED");
                    break;
                default:
                    Log.d(TAG, "what=" + what + " msg1=" + msg1 + " msg2=" + msg2);
                    break;
            }
            switch (what) {
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    mKSYRecordKit.stopCameraPreview();
                    break;
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_CLOSE_FAILED:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_OPEN_FAILED:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_FORMAT_NOT_SUPPORTED:
                case StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_WRITE_FAILED:
                    stopRecord(false);
                    rollBackClipForError();
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN: {
                    stopRecord(false);
                    rollBackClipForError();
                    mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startRecord();
                        }
                    }, 3000);
                }
                break;
                default:
                    break;
            }
        }
    };
	private boolean mIsFileRecording;
	private String mRecordUrl;
    
    /**
     * 
     * @param finished
     */
    private void stopRecord(boolean finished) {
        //录制完成进入编辑
        //若录制文件大于1则需要触发文件合成
        if (finished) {
            if (mKSYRecordKit.getRecordedFilesCount() > 1) {
                String fileFolder = getRecordFileFolder();
                //合成文件路径
                final String outFile = fileFolder + "/" + "merger_" + System.currentTimeMillis() + ".mp4";

                mKSYRecordKit.stopRecord(outFile, new KSYRecordKit.MegerFilesFinishedListener() {
                    @Override
                    public void onFinished() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            	//TODO
                            	Toast.makeText(RecordActivity.this, "短视频录制结束！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                mKSYRecordKit.stopRecord();
            }

        } else {
            //普通录制停止
            mKSYRecordKit.stopRecord();
        }
        //更新进度显示
        mRecordProgressCtl.stopRecording();
        mRecordControler.getDrawable().setLevel(1);
        updateDeleteView();
        mIsFileRecording = false;
        stopChronometer();
    }

    private void updateDeleteView() {
        if (mKSYRecordKit.getRecordedFilesCount() >= 1) {
            mPointerMaker.getDrawable().setLevel(2);
        } else {
        	mPointerMaker.getDrawable().setLevel(1);
        }
    }
    
    
    private void stopChronometer() {
        if (mIsFileRecording) {
            return;
        }

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.stop();
    }
    
    
    
    
    
    
    //------------------------------------------------------------------------end
    private StatsLogReport.OnLogEventListener mOnLogEventListener =
            new StatsLogReport.OnLogEventListener() {
                @Override
                public void onLogEvent(StringBuilder singleLogContent) {
                    Log.i(TAG, "***onLogEvent : " + singleLogContent.toString());
                }
            };
	private LinearLayout topView;
	private RecordProgressController mRecordProgressCtl;
	private RelativeLayout mBarBottomLayout;
    
    
    
    
    private String getRecordFileFolder() {
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return fileFolder;
    }
    
    
    private RecordProgressController.RecordingLengthChangedListener mRecordLengthChangedListener =
            new RecordProgressController.RecordingLengthChangedListener() {
                @Override
                public void passMinPoint(boolean pass) {
                    if (pass) {
//                        mNextView.setVisibility(View.VISIBLE);
                    } else {
//                        mNextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void passMaxPoint() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopRecord(false);
                            mRecordControler.getDrawable().setLevel(1);
                            mRecordControler.setEnabled(false);
                            Toast.makeText(RecordActivity.this, "录制结束，请继续操作",
                                    Toast
                                            .LENGTH_SHORT).show();
                        }
                    });
                }
            };
	private RelativeLayout rl_root;

    
    
    
    
    private void rollBackClipForError() {
        int clipCount = mRecordProgressCtl.getClipListSize();
        int fileCount = mKSYRecordKit.getRecordedFilesCount();
        if (clipCount > fileCount) {
            int diff = clipCount - fileCount;
            for (int i = 0; i < diff; i++) {
                mRecordProgressCtl.rollback();
            }
        }
    }
    
    private String newName;
    public class PopupWindows extends PopupWindow{
		
		private EditText reName;

		public PopupWindows(Context mContext, View parent){
			View view = View.inflate(mContext, R.layout.layout_save_video_info, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			View ll_save =  view
					.findViewById(R.id.tv_save);
			
			reName = (EditText)view.findViewById(R.id.et_video_name);
			
			View ll_upload = view
					.findViewById(R.id.tv_upload);
			
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
					String workingPath=getRecordFileFolder()+"/merge";
					new MergeVideos(workingPath, videosToMerge).execute();//开始合并操作
					
					dismiss();
					
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
					dismiss();
					
					
				}
			});
		}
	}
 	
    
    private String mMessage=null;
    
    
    
    
    
private class MergeVideos extends AsyncTask<String, Integer, String> {
		
		//The working path where the video files are located
		private String workingPath; 
		//The file names to merge
		private ArrayList<String> videosToMerge;
		//Dialog to show to the user
		private ProgressDialog progressDialog;
		
		private MergeVideos(String workingPath, ArrayList<String> videosToMerge) {
			this.workingPath = workingPath;
			this.videosToMerge = videosToMerge;
		}
		
		@Override
		protected void onPreExecute() {
			if(progressDialog==null){
				progressDialog = ProgressDialog.show(RecordActivity.this,
						"合并中...", "请稍等...", true);
			}else{
				progressDialog.show();
			}
			
		};
		
		@Override
		protected String doInBackground(String... params) {
			File storagePath = new File(workingPath);             
			storagePath.mkdirs();  
			File myMovie = new File(storagePath, String.format("output-%s.mp4", newName)); 
			finalPath=myMovie.getAbsolutePath();
			VideoStitchingRequest videoStitchingRequest = new VideoStitchingRequest.Builder()
			.inputVideoFilePath(videosToMerge)
			.outputPath(finalPath).build();
			FfmpegManager manager = FfmpegManager.getInstance();
			manager.stitchVideos(RecordActivity.this, videoStitchingRequest,
			new CompletionListener() {
				@Override
				public void onProcessCompleted(String message,List<String> merger) {
					mMessage=message;
				}
					
			});
			return mMessage;
		}
		
		@Override
		protected void onPostExecute(String value) {
			super.onPostExecute(value);
			progressDialog.dismiss();
			progressDialog.cancel();
			progressDialog=null;
			if(value!=null){
			Toast.makeText(RecordActivity.this, "啊哦，录制失败了！请重新尝试...", Toast.LENGTH_SHORT).show();
			}else{
				saveFlagPointer(mRecordProgressCtl.getFlagPointers());
				Intent intent = new Intent(RecordActivity.this,EditVedioActivity.class);
				intent.putExtra("vedio_path",finalPath);//把最终的路径传过去
				startActivity(intent);
				finish();
			}
		}
		
	}
    
    


    
	/**
	 * 存储视频标记点
	 */
	private void saveFlagPointer(List<Integer> flags){
		RecordDao recordDao=new RecordDao(new DatabaseContext(this));
		RecordDetail bean=new RecordDetail();
		bean.setName(newName+".mp4");
		bean.setFormat("mp4");
		bean.setPath(finalPath);
		bean.setFlags(flags);
		recordDao.add(bean);
	}
    
    
    

}
