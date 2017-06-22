package com.tian.videomergedemo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "caibian.db";// 数据库名称
	public static final int VERSION = 1;
	private static final String SQL_CREATE_RECORD="create table records (_id integer primary key autoincrement,name text,format text,path text,tips text,flags text)";//文件名/文件的格式和文件存储的路径/tips所有的断点拍摄点的进度的集合、flags所有的打标记的点的进度的集合
	/**创建录制的视频只有一段的视频的路径的数据库表的建表语句*/
	private static final String SQL_CREATE_SINGLE="create table singles (_id integer primary key autoincrement,path text)";
//	public String chanlStatus;
//	public String menuPic;
//	public String menuUrl;
	private Context context;
	private static SQLHelper sSQLHelper=null;
	public SQLHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	public static SQLHelper getInstance(Context context){
		if (sSQLHelper == null)
			sSQLHelper = new SQLHelper(context);
		return sSQLHelper;
		
	}

	public Context getContext(){
		return context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 创建数据库后，对数据库的操作
		
		db.execSQL(SQL_CREATE_RECORD);
		db.execSQL(SQL_CREATE_SINGLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作
		onCreate(db);
	}

}
