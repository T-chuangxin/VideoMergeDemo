package com.tian.videomergedemo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于记录录制的视频的信息的bean
 * @author howie
 *
 */
public class RecordDetail {
	private String name;
	private String path;//存储的绝对路径
	private String format;//格式
	private ArrayList<Integer> pauses;//断点录制的时刻点对应的位置
	private ArrayList<Integer> marks;//所有的标记点对应的时刻的进度的集合
	private List<Integer> flags;//用于记录所有的断点录制的视频的打标记的点的进度的集合
	
	public List<Integer> getFlags() {
		return flags;
	}
	public void setFlags(List<Integer> flags) {
		this.flags = flags;
	}
	public ArrayList<Integer> getPauses() {
		return pauses;
	}
	public void setPauses(ArrayList<Integer> pauses) {
		this.pauses = pauses;
	}
	public ArrayList<Integer> getMarks() {
		return marks;
	}
	public void setMarks(ArrayList<Integer> marks) {
		this.marks = marks;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	
	
	
}
