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
		String createSQL="create table books (id text primary key , "
							+ "isbn text not null, title text not null, price numeric(7,3) not null,"
									+ "publisher text not null);";

		mdba= new MyDBAdapter(this, "BookDB", "books", createSQL);		
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
		//����ɾ��
		mdba.delete(" where id=4");
		
		//��������
		TitleBean tb=new TitleBean();
		tb.setId(UUID.randomUUID().toString());
		tb.setIsbn("4343432X2");
		tb.setPrice(50.65);
		tb.setPublisher("Huaqiyinhang");
		tb.setTitle("HeiHei");
		mdba.insert(tb);
		
		//�ر����ݿ�����
		mdba.closeConn();
		
	}

	private void queryOne() {
		//�����޸�(��ѡ��)
		//���ò�ѯһ��
		try {
			TitleBean titleBean = mdba.getOneByWhere(TitleBean.class," where id='afbd7264-939e-45af-9be4-9db299878317'");
			titleBean.setPrice(10.5);
			titleBean.setPublisher("�Ϲ��������");
			titleBean.setIsbn("Xiugaile");
			mdba.update(titleBean, " where id='"+titleBean.getId()+"'");
		} catch (ReturnNOTOneResultExcption e) {
			e.printStackTrace();
		}
		
		//�ر����ݿ�����
		mdba.closeConn();
	}

	private void queryAll() {
		//���ò�ѯ���
		ArrayList<TitleBean> titleList = mdba.getManyByWhere(TitleBean.class," ");
		
		//�ر����ݿ�����
		mdba.closeConn();
	}

	public void DisplayTitle(Cursor c) {
		Toast.makeText( this, "id: " + c.getString(0) + "\n" + "ISBN: " + c.getString(1)
							+ "\n" + "TITLE: " + c.getString(2) + "\n"
								+ "PUBLISHER: " + c.getString(3), Toast.LENGTH_LONG) .show();
	}


}
