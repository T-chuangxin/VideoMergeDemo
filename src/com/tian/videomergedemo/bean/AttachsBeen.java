package com.tian.videomergedemo.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 稿件附件对象
 * 
 * @author Administrator
 * 
 */
public class AttachsBeen implements Serializable{
	public static final int STATUS_NORMAL=0;//正常状态
	public static final int STATUS_SELECTED=1;//选中状态
	public static final int STATUS_UNSELECTED=2;//未选中状态
	public static final int STATUS_PLAYING=3;//播放状态
	private ArrayList<Integer> tips;//用于存储录制的视频打标记的进度的集合
	private ArrayList<Integer> flags;
	
	public static final int DOCUMENT=0;
	public static final int IMAGE=1;
	public static final int VIDEO=2;
	public static final int AUDIO=3;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String achsTitle;  //附件名
	private String achID;  //附件ID
	private String achsPath;     //地址
	private String achsPicurl;  //附件视频缩略图地址
	private int type;
	private int status;
	
	public ArrayList<Integer> getTips() {
		return tips;
	}
	public void setTips(ArrayList<Integer> tips) {
		this.tips = tips;
	}
	public ArrayList<Integer> getFlags() {
		return flags;
	}
	public void setFlags(ArrayList<Integer> flags) {
		this.flags = flags;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAchsTitle() {
		return achsTitle;
	}
	public void setAchsTitle(String achsTitle) {
		this.achsTitle = achsTitle;
	}
	public String getAchID() {
		return achID;
	}
	public void setAchID(String achID) {
		this.achID = achID;
	}
	public String getAchsPath() {
		return achsPath;
	}
	public void setAchsPath(String achsPath) {
		this.achsPath = achsPath;
	}
	public String getAchsPicurl() {
		return achsPicurl;
	}
	public void setAchsPicurl(String achsPicurl) {
		this.achsPicurl = achsPicurl;
	}
	@Override
	public String toString() {
		return "AttachsBeen [tips=" + tips + ", flags=" + flags
				+ ", achsTitle=" + achsTitle + ", achID=" + achID
				+ ", achsPath=" + achsPath + ", achsPicurl=" + achsPicurl
				+ ", type=" + type + ", status=" + status + "]";
	}
	
	
	
	
	
	

}
