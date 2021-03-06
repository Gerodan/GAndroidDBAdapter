package cn.g.GAndroidDBAdapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.g.R;
import cn.g.R.id;
import cn.g.R.layout;
import cn.g.netPic.NetPicListActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DBActivity extends Activity implements OnClickListener {
	private Button button3;
	private TextView tv;
	private Handler handler;
	private ProgressDialog pDialog;
	private MyDBAdapter mdba;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db);
		initControls();
		initDBConn();

	}

	private void initDBConn() { 
		//初始化,实例化表students
		String createSQL="create table students (id text primary key , "
			+ "name text not null, marjor text not null, age numeric(7,1) not null);";
		
		mdba= new MyDBAdapter(this, "BookDB", "students");
		//mdba.createTabel(createSQL);
		//初始化,实例化表books
	    createSQL="create table books (id text primary key , "
					+ "isbn text not null, title text not null, price numeric(7,3) not null,"
							+ "publisher text not null);";

		mdba= new MyDBAdapter(this, "BookDB", "books");
		//mdba.createTabel(createSQL);	
	} 

	private void initControls() {
		this.findViewById(R.id.button06).setOnClickListener(this);
		this.findViewById(R.id.button07).setOnClickListener(this);
		this.findViewById(R.id.button08).setOnClickListener(this);
		tv = (TextView) this.findViewById(R.id.testview02);
	}

	@Override
	public void onClick(View views) {
		switch (views.getId()) {
		case R.id.button06:
			queryAll();
			break;
		case R.id.button07:
			queryOne();
			break;
		case R.id.button08:
			createOne();
			break;
		}

	}

	private void createOne() {
		//调用删除
		mdba.delete(" where id=4");
		
		//调用新增
		TitleBean tb=new TitleBean();
		tb.setId(UUID.randomUUID().toString());
		tb.setIsbn("4343432X2");
		tb.setPrice(50.65);
		tb.setPublisher("Huaqiyinhang");
		tb.setTitle("HeiHei");
		mdba.setOperateTabelName("books").insert(tb);
		
		StudentBean stub=new StudentBean();
		stub.setAge(20);
		stub.setId(UUID.randomUUID().toString());
		stub.setMarjor("数学");
		stub.setName("张飞");
		mdba.setOperateTabelName("students").insert(stub);
		
		//关闭数据库连接
		mdba.closeConn();
		
	}

	private void queryOne() {
		//调用修改(先选出)
		//调用查询一个
		try { 
			TitleBean titleBean = mdba.setOperateTabelName("books").getOneByWhere(TitleBean.class," where id='51a5c456-9c7c-418d-957d-4b5f2b0208ec'");
			titleBean.setPrice(10.5);
			titleBean.setPublisher("合工大出版社");
			titleBean.setIsbn("Xiugaile");
			mdba.update(titleBean, " where id='"+titleBean.getId()+"'");
		} catch (ReturnNOTOneResultExcption e) {
			e.printStackTrace();
		}
		
		//关闭数据库连接
		mdba.closeConn();
	}

	private void queryAll() {
		//调用查询多个
		ArrayList<TitleBean> titleList = mdba.setOperateTabelName("books").getManyByWhere(TitleBean.class," ");
		
		//关闭数据库连接
		mdba.closeConn();
	}

	public void DisplayTitle(Cursor c) {
		Toast.makeText( this, "id: " + c.getString(0) + "\n" + "ISBN: " + c.getString(1)
							+ "\n" + "TITLE: " + c.getString(2) + "\n"
								+ "PUBLISHER: " + c.getString(3), Toast.LENGTH_LONG) .show();
	}


}
