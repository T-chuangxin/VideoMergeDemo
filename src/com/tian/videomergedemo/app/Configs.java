package com.tian.videomergedemo.app;

import java.io.File;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

//import com.hebei.jiting.jwzt.bean.FindDetailBean;

@SuppressLint("SdCardPath")
public class Configs {
//	public static String poi="";//附近
//	public static String location="";//当前位置
//	public static double Latitude=0;//经度
//	public static double Longitude=0;//纬度
	
	
	
	
	public static final int RESOLUTION_480P=0;
	public static final int RESOLUTION_720P=1;
	public static final int RESOLUTION_1080P=2;   
	public static final int RESOLUTION_1920P=3;
	public static final int RESOLUTION_default=4;
	/**合成的视频保存的路径*/
	public static final String COMPOSE_VIDEO_PATH="/CAIBIAN/MixVideo";
	/**最终合成的视频保存的路径*/
	public static final String FINAL_VIDEO_PATH="/CAIBIAN/MixVideos";
	/**添加完水印的视频的保存路径*/
	public static final String WATER_MARK_PATH="/CAIBIAN/WaterMark";
	/**裁剪过的音频保存的路径*/
	public static final String AUDIO_CUT_PATH="/CAIBIAN/AudioCut";
	/**拍照保存的路径*/
	public static final String PHOTO_PATH="/CAIBIAN/Photo";
	/**草稿的保存路径*/
	public static final String CAOGAO_PATH="/CAIBIAN/caogao/";
	/**文档的保存路径*/
	public static final String DOCUMENT_PATH="/CAIBIAN/document/";
	/**经过旋转的用于预览的视频的路径*/
	public static final String ROTATE_VIDEO_PATH="/CAIBIAN/Preview";
	/***图片保存位置*/
	public static final String DOWNLOAD_IMAGE_PATH="/CAIBIAN/download";
	public static int lastTime=0;
	/***头像上传时选择的需要裁剪的图片，由于图片可能比较大不能通过intent传递，所以定义到全局*/
	public static Bitmap bitmap;
	/***头像的图片裁剪后保存在本地的路径*/
	public static String filePath;
	/***拍摄的路径*/
	public static File tempFile;
	public static String saveCutFilePath;
	/***视频文件保存路径*/
	public static final String BLSavePath=Environment.getExternalStorageDirectory() + "/CAIBIAN/Video/";
	/***图片文件保存路径*/
	public static final String BLSavePathImg=Environment.getExternalStorageDirectory() + "/CAIBIAN/Images/";
	/***音频保存路径*/
	public static final String BLSavePathAudio=Environment.getExternalStorageDirectory() + "/CAIBIAN/Audio/";
	/***视频编辑的时候分段裁剪的视频的保存路径*/
	public static final String BLEditSegments=Environment.getExternalStorageDirectory() + "/CAIBIAN/EditSegments/";
	/****/
	public static final int VIDEO_FRAMERATE = 20;
	public static final String BASE_DIR = "/org.easydarwin.video";
	public static final String TMP_DIR = BASE_DIR + "/tmp";
	public static final String VS_DIR = BASE_DIR + "/.vs";
	public static final String VIDEO_TMP_DIR = TMP_DIR + "/.video";
	public static final float MAX_RECORD_TIME = 15 * 1000f;
	public static final float MIN_RECORD_TIME = 2 * 1000f;
	
	public static final int STICKER_BTN_HALF_SIZE = 30;
	
	/***网络请求域名（内网）*/
	public static String ICON_URL="http://192.168.1.192:8080";
//	public static String ICON_URL="http://192.168.1.166:8080";
	
	/***登录接口*/
	public static String loginUrl=ICON_URL+"/bvCaster_user/phone/loginNoCap.jspx?phoneNum=%s&password=%s";
	public static int loginCode=5000;
	
	/***修改用户头像*/
	public static String userHeadUrl=ICON_URL+"/bvCaster_user/phone/phoneUserImgUpdate.jspx?file=%s&userID=%s";
	public static int userHeadCode=5001;
	
	/***分类接口*/
	public static String typeUrl=ICON_URL+"/bvCaster_converge/phone/category/tree.jspx?userID=%s";
	public static int typeCode=5002;
	
	/*** 文稿列表（已上传列表）*/
	public static String manuscriptUrl= ICON_URL+"/bvCaster_converge/phone/media/list.jspx?userId=%s&startId=%s&size=%s";
	public static int manuscriptCode=5003;
	
	/*** 稿件已上传信息接口*/
	public static String manuscriptDetailsUrl= ICON_URL+"/bvCaster_converge/phone/media/view.jspx?id=%s";
	public static int manuscriptDetailsCode=5004;
	
	/***任务列表*/
	public static String taskListUrl=ICON_URL+"/bvCaster_direct/admin/phoneTaskList.jspx?userId=%s";
	/***根据不同的任务状态获取数据*/
	public static String taskListStatusUrl=ICON_URL+"/bvCaster_direct/admin/phoneTaskList.jspx?userId=%s&taskStatus=%s";
	public static int taskListCode=5005;
	
	/***任务详情*/
	public static String taskDetailsUrl=ICON_URL+"/bvCaster_direct/admin/phoneTaskView.jspx?id=%s";
	public static int taskDetailsCode=5006;
	
	/***首页获取信息列表*/
	public static String infoListUrl=ICON_URL+"/bvCaster_converge/phone/log/v_list.do?userId=%s&startId=%s&size=%s";
	public static int infoListCode=5007;
	
	/***获取房间列表*/
	public static String groupListUrl=ICON_URL+"/bvCaster_direct/admin/phoneGetGroupList.jspx?userId=%s";
	public static int groupListCode=5008;
	
	/***获取token*/
	public static String tokenUrl=ICON_URL+"/bvCaster_direct/admin/phoneIMLogin.jspx?userId=%s&username=%s";
	public static int tokenCode=5009;
	
	/***群组交流中发送消息的接口*/
	public static String sendMessageUrl=ICON_URL+"/bvCaster_direct/admin/phoneSendMessage.jspx?userId=%s&groupId=%s&content=%s&msgType=%s&IMEI=%s";
	public static int sendMessageCode=5010;
	
	/***获取首页统计数量接口*/
	public static String countUrl=ICON_URL+"/bvCaster_converge/phone/media/count.jspx?userId=26";
	public static int countCode=5011;
	
	
	
	
	
}
