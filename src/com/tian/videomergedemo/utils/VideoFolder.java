package com.tian.videomergedemo.utils;



import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author TCX
 *
 */
public class VideoFolder implements Serializable {

    public String name;  //当前文件夹的名字
    public String path;  //当前文件夹的路径
    public VideoItem cover;   //当前文件夹需要要显示的缩略图，默认为最近的一次视频
    public ArrayList<VideoItem> videos;  //当前文件夹下所有视频的集合

    /** 只要文件夹的路径和名字相同，就认为是相同的文件夹 */
    @Override
    public boolean equals(Object o) {
        try {
            VideoFolder other = (VideoFolder) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}

