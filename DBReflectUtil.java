package cn.g.GAndroidDBAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

public class DBReflectUtil {
	
	/*
	 * 输入参数:JavaBean的类Class clazz
	 * 输出:该JavaBean的所有Pulbic变量的变量名(以,分割的String)
	 */
	public static String getFieldsString(Class clazz){
		StringBuilder fieldsStringBulider=new StringBuilder();
		String fieldsString="";
		Field[] fields = clazz.getFields();
		for (Field thisField : fields) {
			// 只打印出Public的变量
			fieldsStringBulider.append(thisField.getName());
			fieldsStringBulider.append(",");
		}
		fieldsString=fieldsStringBulider.substring(0, fieldsStringBulider.length()-1).toString();
		return fieldsString.toString();
	}
	
	/*
	 * 输入参数:JavaBean的类Class clazz
	 * 输出:该JavaBean的所有Pulbic变量的变量名和其变量类型(HashMap的Key是变量名,Value是其类型)
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
	 * 输入参数:查询数据库的结果游标对象Cursor resultCur,JavaBean的类Class clazz,JavaBean对象的所有Public的变量名String filedsString
	 * 输出:(泛型)ArrayList的对象列表
	 */
	public static <T> ArrayList<T> getGenericListResult(Cursor resultCur, Class javaBean, String filedsString) {
		String [] filedsArray=filedsString.split(",");
		ArrayList<T> arrayResult=new ArrayList<T>();
		HashMap<String,Class> map=getFieldsHashMap(javaBean);
		//遍历结果集
		if (resultCur.moveToFirst()) {
			do {
				//将一条记录中每个列塞到JavaBean中
				Object thisClassInstance = null;
				try {
					thisClassInstance = javaBean.newInstance();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//每一条记录中遍历JavaBean的Fields
				for(String fieldName:filedsArray){
					try {
						//得到这一列的值
						String fieldValue= resultCur.getString(resultCur.getColumnIndex(fieldName));
						//得到驼峰String
						String camelString=getCamelString(fieldName);
						Method setName;
						Class fieldClassType=map.get(fieldName);
						
						//确定调用哪个方法
						setName= javaBean.getMethod("set"+camelString, new Class[] { fieldClassType });
						//往调用的方法里面传对应的参数
						setName.invoke(thisClassInstance, new Object[] { getArgValueByType(fieldClassType,fieldValue) });						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//加到ArrayResult中
				arrayResult.add((T)thisClassInstance);
 			} while (resultCur.moveToNext());
		}
		return arrayResult;
		
	}

	/*
	 * 输入参数:JavaBean的某一个变量的类型Class fieldClassType,要塞入JavaBean对象的某一个变量的值String filedsString
	 * 输出:不同类型的对象变量值
	 */
	private static Object getArgValueByType(Class fieldClassType, String fieldValue) {
		//参数是不同类型
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
	 * 输入参数:JavaBean的某一个变量的名字(全小写)
	 * 输出:格式化的驼峰字符串(首字母大写)
	 */
	private static String getCamelString(String fieldName) {
		String first = fieldName.substring(0, 1).toUpperCase();
		String rest = fieldName.substring(1, fieldName.length());
		String newStr = new StringBuffer(first).append(rest).toString();
		return newStr;
	}
	
	
	/*
	 * 输入参数:JavaBean的某一个对象,该对象的Field名称字符串
	 * 输出:按照Field对应的顺序拼接完成的Field的值
	 */
	public static <T> String getFieldsValString(T beanInstance,String filedsString) {
		String [] filedsArray=filedsString.split(",");
		String fieldsValString="";
		StringBuilder fieldsValStringBulider=new StringBuilder();
		for(String fieldName:filedsArray){
			Method setName;
			//得到驼峰String
			String camelString=getCamelString(fieldName);
			
			//确定调用哪个方法
			try {
				setName= beanInstance.getClass().getMethod("get"+camelString, new Class[] {});
				Object filedVal=setName.invoke(beanInstance, new Object[] {});
				//数字,字符串插入的SQL都可以用' '括起来
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
	 * 输入参数:JavaBean的某一个对象,该对象的Field名称字符串
	 * 输出:按照Field对应的顺序拼接完成的Field的值
	 */
	public static <T> String getFiledsNameValString(T beanInstance,String filedsString) {
		String [] filedsArray=filedsString.split(",");
		String fieldsValString="";
		StringBuilder fieldsValStringBulider=new StringBuilder();
		for(String fieldName:filedsArray){
			Method setName;
			//得到驼峰String
			String camelString=getCamelString(fieldName);
			//确定调用哪个方法
			try {
				setName= beanInstance.getClass().getMethod("get"+camelString, new Class[] {});
				Object filedVal=setName.invoke(beanInstance, new Object[] {});
				//只更新JavaBean里面有值的属性
				if(filedVal!=null||filedVal.toString().length()>0){
					//数字,字符串插入的SQL都可以用' '括起来
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
