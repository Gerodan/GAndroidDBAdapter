package cn.g.GAndroidDBAdapter;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDBAdapter extends RawDBAdapter{
	//只需要表明即可
	private String DATABASE_TABLE_NAME;
	private SQLiteDatabase db;


	public MyDBAdapter(Context ctx, String dbName, String tableName, String createSQL) {
		super(ctx, dbName, tableName, createSQL);
		this.DATABASE_TABLE_NAME=tableName;
		this.db=super.getDBConn();
	}

	@Override
	public void delete(String strWhere) {
		this.db=super.getDBConn();
		StringBuilder delSQL=new StringBuilder();
		delSQL.append("DELETE FROM "+DATABASE_TABLE_NAME+" ");
		delSQL.append(strWhere);
		db.execSQL(delSQL.toString());
	}

	@Override
	public <T> ArrayList<T> getManyByWhere(Class javaBean,String strWhere) {
		this.db=super.getDBConn();
		String filedsString=DBReflectUtil.getFieldsString(javaBean);
		StringBuilder selectString=new StringBuilder(); 
		//拼接Select语句
		selectString.append("SELECT ");
		selectString.append(filedsString+" ");
		selectString.append("FROM "+DATABASE_TABLE_NAME+" ");
		selectString.append(strWhere);
		
		Cursor resultCur=db.rawQuery(selectString.toString(), null);		
		
		return DBReflectUtil.getGenericListResult(resultCur,javaBean,filedsString);
    }

	@Override
	public <T> T getOneByWhere(Class javaBean,String strWhere) throws ReturnNOTOneResultExcption {
		this.db=super.getDBConn();
		String filedsString=DBReflectUtil.getFieldsString(javaBean);
		StringBuilder selectString=new StringBuilder(); 
		//拼接Select语句
		selectString.append("SELECT ");
		selectString.append(filedsString+" ");
		selectString.append("FROM "+DATABASE_TABLE_NAME+" ");
		selectString.append(strWhere);
		
		Cursor resultCur=db.rawQuery(selectString.toString(), null);
		
		ArrayList<T> list= DBReflectUtil.getGenericListResult(resultCur,javaBean,filedsString);
		if(list.size()!=1){
			throw new ReturnNOTOneResultExcption("该ID号没有对应的值");
		}
		
		return list.get(0);	
	}

	@Override
	public <T> void insert(T beanInstance) {
		this.db=super.getDBConn();
		String filedsString=DBReflectUtil.getFieldsString(beanInstance.getClass());
		String filedsValString=DBReflectUtil.getFieldsValString(beanInstance,filedsString);
		StringBuilder insertString=new StringBuilder(); 
		//拼接Insert语句
		insertString.append("INSERT INTO ");
		insertString.append(DATABASE_TABLE_NAME+"( ");
		insertString.append(filedsString+") values (");
		insertString.append(filedsValString+" ");
		insertString.append(")");
		
		db.execSQL(insertString.toString());
	}

	@Override
	public <T> void update(T beanInstance,String strWhere) {
		this.db=super.getDBConn();
		String filedsString=DBReflectUtil.getFieldsString(beanInstance.getClass());
		String filedsNameValString=DBReflectUtil.getFiledsNameValString(beanInstance,filedsString);
		StringBuilder insertString=new StringBuilder(); 
		//拼接Insert语句
		insertString.append("UPDATE ");
		insertString.append(DATABASE_TABLE_NAME+" SET ");
		insertString.append(filedsNameValString+" ");
		insertString.append(strWhere);
		
		db.execSQL(insertString.toString());
	}

	@Override
	//设置过要操作的 数据表名 之后,返回操作对象,实现"链式编程"
	public MyDBAdapter setOperateTabelName(String tableName) {
		this.DATABASE_TABLE_NAME=tableName;
		return this;
	}

	@Override
	public String getNowOperateTabelName() {
		return this.DATABASE_TABLE_NAME;
	}

}
