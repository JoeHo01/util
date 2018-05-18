package com.jo.analysis.web.mvc.base;

/**
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/5/18
 */

import com.jo.analysis.web.mvc.base.annotation.Column;
import com.jo.analysis.web.mvc.entity.POJO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * POJO Cache
 */
public class POJOCache {

	private static POJOCache POJOCache;

	Logger logger = LoggerFactory.getLogger(POJOCache.class);

	private Map<Class, POJO> entityMap;

	public static POJOCache get() {
		if (POJOCache == null) POJOCache = new POJOCache();
		return POJOCache;
	}

	private POJOCache() {
		entityMap = new HashMap<>();
		init();
	}

	public POJO getPOJO(Class clazz) {
		return entityMap.get(clazz);
	}

	private void init() {
		// get all classes extending POJO
		List<Class<? extends POJO>> classes = getClassAssigned(POJO.class);
		// cache POJO
		if (!CollectionUtils.isEmpty(classes)) for (Class<? extends POJO> clazz : classes) {
			POJO pojo = new POJO();
			ArrayList<Field> fields = fieldWithSuper(clazz);

			// set class
			pojo.setClazz(clazz);
			// set fields
			pojo.setField(fields);

			if (fields != null) for (Field field : fields) try {
				// key
				String fieldName = field.getName();

				// set Method GET
				Method get = clazz.getMethod(buildMethodName("get", fieldName));
				if (get != null) pojo.setMethodGET(fieldName, get);

				// set Method GET
				Method set = clazz.getMethod(buildMethodName("set", fieldName), field.getType());
				if (set != null) pojo.setMethodSET(fieldName, set);

				// set column
				Column column = field.getAnnotation(Column.class);
				if (column != null) pojo.setColumn(fieldName, column.value());

			} catch (NoSuchMethodException e) {
				logger.error(e.getMessage(), e);
			}
			entityMap.put(clazz, pojo);
		}
	}

	private <T> List<Class<? extends T>> getClassAssigned(Class<T> clazz) {
		String pack = clazz.getPackage().getName();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL url = classloader.getResource(pack.replace('.', '/'));
		assert url != null;
		try {
			return getClass(new File(url.getFile()), pack, clazz);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private <T> List<Class<? extends T>> getClass(File dir, String pack, Class<T> clazz) throws ClassNotFoundException {
		ArrayList<Class<? extends T>> classes = new ArrayList<>();
		if (!dir.exists()) return classes;
		File[] files = dir.listFiles();
		if (files != null) for (File f : files) {
			if (f.isDirectory()) classes.addAll(getClass(f, pack + "." + f.getName(), clazz));
			String name = f.getName();
			if (name.endsWith(".class")) {
				Class<?> c = Class.forName(pack + "." + name.substring(0, name.length() - 6));
				if (clazz.isAssignableFrom(c) && c != clazz) classes.add((Class<? extends T>) c);
			}
		}
		return classes;
	}

	private String buildMethodName(String prefix, String fieldName) {
		if (StringUtils.isEmpty(fieldName)) return fieldName;
		String firstCase = fieldName.substring(0, 1).toUpperCase();
		return prefix + firstCase + fieldName.substring(1);
	}

	private ArrayList<Field> fieldWithSuper(Class<? extends POJO> clazz) {
		ArrayList<Field> list = new ArrayList<>();
		if (clazz == null) return null;

		Class<?> superclass = clazz.getSuperclass();
		if (superclass != POJO.class) list.addAll(fieldWithSuper((Class<? extends POJO>) superclass));

		Collections.addAll(list, clazz.getDeclaredFields());
		return list;
	}
}
