package cn.g.GAndroidDBAdapter;

import java.util.ArrayList;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class RawDBAdapter {
	private static final String TAG = "RawDBAdapter";
	
	//�������ݿ�ʹ�����������ִ��,��Ҫ����
	private String DATABASE_NAME;
	private String DATABASE_TABLE_NAME;
	
	private static final int DATABASE_VERSION = 1;
	private final Context context;
	private DataBaseHelper dBHelper;

	//new������ʱ�򴴽���DB,�����˱�
	public RawDBAdapter(Context ctx ,String dbName,String tableName) {
		this.context = ctx;
		this.DATABASE_NAME=dbName;
		this.DATABASE_TABLE_NAME=tableName;
		dBHelper = new DataBaseHelper(context);
	}

	//SQLite��������(����,�޸İ汾)
	private  class DataBaseHelper extends SQLiteOpenHelper {
		DataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//����Ҫ��new�����ʱ�򴴽����ݿ�
		}

		//���ݿ�������ʱ�����(ɾ�����о�����,ֻ���Ʊ�ṹ)
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "�������ݿ�汾�� " + oldVersion + " �� "+ newVersion + ", ɾ�����о�����");
			db.execSQL("DROP TABLE IF EXISTS" +DATABASE_TABLE_NAME);
			onCreate(db);
		}
	}


    //-----------------DB���Ӳ���----------------------
	// ---�õ����ݿ�����(�ɶ�д)---
	public SQLiteDatabase getDBConn() {
			return dBHelper.getWritableDatabase();
	}
	
	// ---�õ�DBHelper(��DB������汾�Ĺ���)---
	public DataBaseHelper getDBHelper() {
		return dBHelper;
	}
	
	// ---�ر����ݿ�---
	public void closeConn() {
		//�о͹ر�,�����ùر�
		if(dBHelper != null){
			dBHelper.close();
		}
	}
	
	//���ڲ��������ݿ�
	public abstract MyDBAdapter setOperateTabelName(String tableName);
	public abstract String getNowOperateTabelName();
	public abstract void createTabel(String createTableSQL);

	
	
    //-----------------DB��CRUD����----------------------
	// ---����---
	public abstract <T> void insert(T beanInstance);

	// ---ɾ��һ��---
	public abstract void delete(String strWhere);

	// ---�����ܶ�---
	public abstract <T> ArrayList<T> getManyByWhere(Class javaBean,String strWhere);

	// ---����һ��---
	public abstract <T> T getOneByWhere(Class javaBean,String strWhere) throws ReturnNOTOneResultExcption;

	// ---����---
	public abstract <T> void update(T beanInstance,String strWhere);
	
	// ---ִ���������ӵ�SQL---
	public abstract void executeMySQL(String sql);
}
