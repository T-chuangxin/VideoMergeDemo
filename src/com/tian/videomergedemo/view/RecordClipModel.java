package com.tian.videomergedemo.view;

/**
 * record progress model
 */

public class RecordClipModel {
    public long timeInterval; //units:miliseconds
    public int state; // 0-recording, 1-recorded, 2-pending for delete, 3-upload anim,4-续拍视频
}
