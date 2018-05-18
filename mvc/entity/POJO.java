package com.jo.analysis.web.mvc.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/4/23
 * Description:
 */

public class POJO {
	private Class<? extends POJO> clazz;

	private Map<String, Method> method;

	private Map<String, String> column;

	private ArrayList<Field> field;

	public POJO() {
		method = new HashMap<>();
		column = new HashMap<>();
	}

	public Class<? extends POJO> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends POJO> clazz) {
		this.clazz = clazz;
	}

	public Method getMethodGET(String key) {
		return method.get("G_" + key);
	}

	public Method getMethodSET(String key) {
		return method.get("S_" + key);
	}

	public void setMethodGET(String key, Method method) {
		this.method.put("G_" + key, method);
	}

	public void setMethodSET(String key, Method method) {
		this.method.put("S_" + key, method);
	}

	public String getColumn(String key) {
		return column.get(key);
	}

	public void setColumn(String key, String column) {
		this.column.put(key, column);
	}

	public ArrayList<Field> getField() {
		return field;
	}

	public void setField(ArrayList<Field> field) {
		this.field = field;
	}
}
