package com.tian.videomergedemo.dao;



import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tian.videomergedemo.bean.AttachsBeen;
import com.tian.videomergedemo.bean.RecordDetail;

/**
 * 操作录制的视频的信息的数据库表的类
 * @author howie
 *
 */
public class RecordDao {
	private SQLHelper helper;
	public RecordDao(Context context){
		helper=SQLHelper.getInstance(context);
	}
	public void add(RecordDetail bean){
		Gson gson=new Gson();
		SQLiteDatabase db = helper.getReadableDatabase();
		db.execSQL("insert into records  (name,path,format,tips,flags) values (?,?,?,?,?)",new Object[]{bean.getName(),bean.getPath(),bean.getFormat(),gson.toJson(bean.getMarks()),gson.toJson(bean.getFlags())});
		db.close();
	}
	/**
	 * 通过文件的路径查找某一条录制的视频的相关信息
	 * @return
	 */
	public RecordDetail queryByPath(){
		
		return null;
		
	}
	/**
	 * 判断一个视频是否是通过此app录制的,如果是通过app录制的就把查出来的视频的信息返回去
	 * @param bean
	 * @return
	 */
	public AttachsBeen isExist(AttachsBeen bean){
		boolean flag=false;
		Gson gson=null;
		SQLiteDatabase db = helper.getReadableDatabase();
//		db.execSQL("select * from programmes where name =?",new Object[]{bean.getName()});
		Cursor cursor = db.rawQuery("select flags,tips from records where path =?", new String[]{bean.getAchsPath()});
		
		while(cursor.moveToNext()){
			if(gson==null){
				gson=new Gson();
			}
			flag=true;
			String flags = cursor.getString(cursor.getColumnIndex("flags"));//所有的打标记的点的进度值的集合
			String tips=cursor.getString(cursor.getColumnIndex("tips"));
			ArrayList<Integer> flagList=gson.fromJson(flags, new TypeToken<ArrayList<Integer>>(){}.getType());
			ArrayList<Integer> tipList=gson.fromJson(tips, new TypeToken<ArrayList<Integer>>(){}.getType());
			bean.setFlags(flagList);//标记点的进度的集合
			bean.setTips(tipList);//断点的进度的集合
		}
		return bean;
	}

	public void update(RecordDetail recordDetail){
//		String sql_reset_sequence="update records set name =? where name ='programmes'";
	}
	
}
