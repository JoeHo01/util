package com.jo.analysis.web.mvc.base;

import com.jo.analysis.web.mvc.entity.POJO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/4/23
 *
 */
public class POJOHelper {

	private static final Logger logger = LoggerFactory.getLogger(POJOHelper.class);

	private static final POJOCache POJO_CACHE = POJOCache.get();

	public static <P extends POJO> P convert(Map<String, Object> value, Class<P> clazz) {
		// get fields in class
		ArrayList<Field> fields = getField(clazz);
		return setValue(clazz, fields, value);
	}

	public static <P extends POJO> ArrayList<P> convert(List<Map<String, Object>> values, Class<P> clazz) {
		// get fields in class
		ArrayList<Field> fields = getField(clazz);
		ArrayList<P> list = new ArrayList<>();
		if (!CollectionUtils.isEmpty(values)) for (Map<String, Object> value : values) {
			list.add(setValue(clazz, fields, value));
		}
		return list;
	}

	public static Method methodGet(Class clazz, String fieldName) {
		return POJO_CACHE.getPOJO(clazz).getMethodGET(fieldName);
	}

	public static Method methodSet(Class clazz, String fieldName) {
		return POJO_CACHE.getPOJO(clazz).getMethodSET(fieldName);
	}

	public static String getColumn(Class clazz, String fieldName) {
		return POJO_CACHE.getPOJO(clazz).getColumn(fieldName);
	}

	public static ArrayList<Field> getField(Class clazz) {
		return POJO_CACHE.getPOJO(clazz).getField();
	}

	public static POJO getPOJO(Class clazz) {
		return POJO_CACHE.getPOJO(clazz);
	}

	private static <P extends POJO> P setValue(Class<P> clazz, ArrayList<Field> fields, Map<String, Object> value) {
		// new POJO
		P p;
		try {
			p = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		// set value annotated by @Column
		for (Field field : fields) {
			Method set = methodSet(clazz, field.getName());
			Object o = value.get(getColumn(clazz, field.getName()));
			if (set != null && o != null) try {
				set.invoke(p, o);
			} catch (IllegalAccessException | InvocationTargetException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return p;
	}
}
