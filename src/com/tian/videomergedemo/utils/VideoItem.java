package com.tian.videomergedemo.utils;

import java.io.Serializable;

/**
 * 
 * @author TCX
 *
 */
public class VideoItem implements Serializable {
	public boolean isSelected;
    public String name;       //视频的名字
    public String path;       //视频的路径
    public long size;         //视频的大小
    public int width;         //视频的宽度
    public int height;        //视频的高度
    public String mimeType;   //视频的类型
    public long addTime;      //视频的创建时间
    public long timeLong;      //视频的时长

    /** 视频的路径和创建时间相同就认为是同一个视频 */
    @Override
    public boolean equals(Object o) {
        try {
            VideoItem other = (VideoItem) o;
            return this.path.equalsIgnoreCase(other.path) && this.addTime == other.addTime;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
