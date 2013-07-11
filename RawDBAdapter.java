package cn.g.GAndroidDBAdapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class RawDBAdapter {
	private static final String TAG = "RawDBAdapter";
	
	//创建数据库和创建表在这里执行,需要名字
	private String DATABASE_NAME;
	private String DATABASE_TABLE_NAME;
	private String DATABASE_CREATE_SQL;
	
	private static final int DATABASE_VERSION = 1;
	private final Context context;
	private DataBaseHelper dBHelper;

	//new这个类的时候创建了DB,创建了表
	public RawDBAdapter(Context ctx ,String dbName,String tableName,String createSQL) {
		this.context = ctx;
		this.DATABASE_NAME=dbName;
		this.DATABASE_TABLE_NAME=tableName;
		this.DATABASE_CREATE_SQL=createSQL;
		dBHelper = new DataBaseHelper(context);
	}

	//SQLite基本操作(建库,修改版本)
	private  class DataBaseHelper extends SQLiteOpenHelper {
		DataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_SQL);
		}

		//数据库升级的时候调用(删除所有旧数据,只复制表结构)
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "更新数据库版本从 " + oldVersion + " 到 "+ newVersion + ", 删除所有旧数据");
			db.execSQL("DROP TABLE IF EXISTS" +DATABASE_TABLE_NAME);
			onCreate(db);
		}
	}


    //-----------------DB连接操作----------------------
	// ---得到数据库连接(可读写)---
	public SQLiteDatabase getDBConn() {
			return dBHelper.getWritableDatabase();
	}
	
	// ---关闭数据库---
	public void closeConn() {
		//有就关闭,否则不用关闭
		if(dBHelper != null){
			dBHelper.close();
		}
	}
	
	
    //-----------------DB的CRUD操作----------------------
	// ---新增---
	public abstract <T> void insert(T beanInstance);

	// ---删除一个---
	public abstract void delete(String strWhere);

	// ---检索很多---
	public abstract <T> ArrayList<T> getManyByWhere(Class javaBean,String strWhere);

	// ---检索一个---
	public abstract <T> T getOneByWhere(Class javaBean,String strWhere) throws ReturnNOTOneResultExcption;

	// ---更新---
	public abstract <T> void update(T beanInstance,String strWhere);
}
