package com.tian.videomergedemo.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 封装合成视频信息的bean
 * @author howie
 *
 */
public class ComposeInfo implements Serializable{
	private String name;
	private String videoName;
	//标记点
	private ArrayList<Float> pausePoints;
	/**断点拍摄的时候的断点的进度的值的集合*/
	private ArrayList<Integer> tips;
	/**断点拍摄的时候的打标记的点的进度的值的集合*/
	private ArrayList<Integer> flags;   
	
	private int resolution;
	private ArrayList<String> list;
	private String path;
	private String final_path;
	
	public ArrayList<Integer> getFlags() {
		return flags;
	}
	public void setFlags(ArrayList<Integer> flags) {
		this.flags = flags;
	}
	public ArrayList<Integer> getTips() {
		return tips;
	}
	public void setTips(ArrayList<Integer> tips) {
		this.tips = tips;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFinal_path() {
		return final_path;
	}
	public void setFinal_path(String final_path) {
		this.final_path = final_path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public ArrayList<Float> getPausePoints() {
		return pausePoints;
	}
	public void setPausePoints(ArrayList<Float> pausePoints) {
		this.pausePoints = pausePoints;
	}
	public int getResolution() {
		return resolution;
	}
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	public ArrayList<String> getList() {
		return list;
	}
	public void setList(ArrayList<String> list) {
		this.list = list;
	}
	
	
}
