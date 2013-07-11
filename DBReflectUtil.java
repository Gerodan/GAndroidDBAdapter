package cn.g.GAndroidDBAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

public class DBReflectUtil {
	
	/*
	 * �������:JavaBean����Class clazz
	 * ���:��JavaBean������Pulbic�����ı�����(��,�ָ��String)
	 */
	public static String getFieldsString(Class clazz){
		StringBuilder fieldsStringBulider=new StringBuilder();
		String fieldsString="";
		Field[] fields = clazz.getFields();
		for (Field thisField : fields) {
			// ֻ��ӡ��Public�ı���
			fieldsStringBulider.append(thisField.getName());
			fieldsStringBulider.append(",");
		}
		fieldsString=fieldsStringBulider.substring(0, fieldsStringBulider.length()-1).toString();
		return fieldsString.toString();
	}
	
	/*
	 * �������:JavaBean����Class clazz
	 * ���:��JavaBean������Pulbic�����ı����������������(HashMap��Key�Ǳ�����,Value��������)
	 */
	public static HashMap<String,Class>  getFieldsHashMap(Class clazz){
		Field[] fields = clazz.getFields();
		HashMap<String,Class> fieldAndTypeMap=new HashMap<String, Class>();
		for (Field thisField : fields) {
			fieldAndTypeMap.put(thisField.getName(), thisField.getType());
		}
		return fieldAndTypeMap;
	}

	/*
	 * �������:��ѯ���ݿ�Ľ���α����Cursor resultCur,JavaBean����Class clazz,JavaBean���������Public�ı�����String filedsString
	 * ���:(����)ArrayList�Ķ����б�
	 */
	public static <T> ArrayList<T> getGenericListResult(Cursor resultCur, Class javaBean, String filedsString) {
		String [] filedsArray=filedsString.split(",");
		ArrayList<T> arrayResult=new ArrayList<T>();
		HashMap<String,Class> map=getFieldsHashMap(javaBean);
		//���������
		if (resultCur.moveToFirst()) {
			do {
				//��һ����¼��ÿ��������JavaBean��
				Object thisClassInstance = null;
				try {
					thisClassInstance = javaBean.newInstance();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//ÿһ����¼�б���JavaBean��Fields
				for(String fieldName:filedsArray){
					try {
						//�õ���һ�е�ֵ
						String fieldValue= resultCur.getString(resultCur.getColumnIndex(fieldName));
						//�õ��շ�String
						String camelString=getCamelString(fieldName);
						Method setName;
						Class fieldClassType=map.get(fieldName);
						
						//ȷ�������ĸ�����
						setName= javaBean.getMethod("set"+camelString, new Class[] { fieldClassType });
						//�����õķ������洫��Ӧ�Ĳ���
						setName.invoke(thisClassInstance, new Object[] { getArgValueByType(fieldClassType,fieldValue) });						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//�ӵ�ArrayResult��
				arrayResult.add((T)thisClassInstance);
 			} while (resultCur.moveToNext());
		}
		return arrayResult;
		
	}

	/*
	 * �������:JavaBean��ĳһ������������Class fieldClassType,Ҫ����JavaBean�����ĳһ��������ֵString filedsString
	 * ���:��ͬ���͵Ķ������ֵ
	 */
	private static Object getArgValueByType(Class fieldClassType, String fieldValue) {
		//�����ǲ�ͬ����
		if (fieldClassType==String.class){
			return new String(fieldValue);
		}
		else if(fieldClassType==Integer.class){
			return new Integer(fieldValue);
		}
		else if(fieldClassType==Double.class){
			return new Double(fieldValue);
		}

		return new String(fieldValue);
	}

	/*
	 * �������:JavaBean��ĳһ������������(ȫСд)
	 * ���:��ʽ�����շ��ַ���(����ĸ��д)
	 */
	private static String getCamelString(String fieldName) {
		String first = fieldName.substring(0, 1).toUpperCase();
		String rest = fieldName.substring(1, fieldName.length());
		String newStr = new StringBuffer(first).append(rest).toString();
		return newStr;
	}
	
	
	/*
	 * �������:JavaBean��ĳһ������,�ö����Field�����ַ���
	 * ���:����Field��Ӧ��˳��ƴ����ɵ�Field��ֵ
	 */
	public static <T> String getFieldsValString(T beanInstance,String filedsString) {
		String [] filedsArray=filedsString.split(",");
		String fieldsValString="";
		StringBuilder fieldsValStringBulider=new StringBuilder();
		for(String fieldName:filedsArray){
			Method setName;
			//�õ��շ�String
			String camelString=getCamelString(fieldName);
			
			//ȷ�������ĸ�����
			try {
				setName= beanInstance.getClass().getMethod("get"+camelString, new Class[] {});
				Object filedVal=setName.invoke(beanInstance, new Object[] {});
				//����,�ַ��������SQL��������' '������
				fieldsValStringBulider.append("'"+filedVal+"'");
				fieldsValStringBulider.append(",");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fieldsValString=fieldsValStringBulider.substring(0, fieldsValStringBulider.length()-1).toString();
		return fieldsValString.toString();
	}
	
	
	/*
	 * �������:JavaBean��ĳһ������,�ö����Field�����ַ���
	 * ���:����Field��Ӧ��˳��ƴ����ɵ�Field��ֵ
	 */
	public static <T> String getFiledsNameValString(T beanInstance,String filedsString) {
		String [] filedsArray=filedsString.split(",");
		String fieldsValString="";
		StringBuilder fieldsValStringBulider=new StringBuilder();
		for(String fieldName:filedsArray){
			Method setName;
			//�õ��շ�String
			String camelString=getCamelString(fieldName);
			//ȷ�������ĸ�����
			try {
				setName= beanInstance.getClass().getMethod("get"+camelString, new Class[] {});
				Object filedVal=setName.invoke(beanInstance, new Object[] {});
				//ֻ����JavaBean������ֵ������
				if(filedVal!=null||filedVal.toString().length()>0){
					//����,�ַ��������SQL��������' '������
					fieldsValStringBulider.append("'"+fieldName+"'");
					fieldsValStringBulider.append("= ");				
					fieldsValStringBulider.append("'"+filedVal+"'");
					fieldsValStringBulider.append(",");				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fieldsValString=fieldsValStringBulider.substring(0, fieldsValStringBulider.length()-1).toString();
		return fieldsValString.toString();
	}

}
