package com.richemont.ccfe.core.utils.tool;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.richemont.ccfe.core.exception.TechnicalException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class JSONUtils.
 * 
 * @author Will
 */
public final class JSONUtils {

	/**
	 * The Class SingletonHolder.
	 */
	private static final class SingletonHolder {

		/** The instance. */
		private static ObjectMapper instance;
	}

	/**
	 * get objectMapper instance.
	 *
	 * @param createNew
	 *            the create new
	 * @return the mapper instance
	 */
	private static synchronized ObjectMapper getMapperInstance(boolean createNew) {
		if (createNew || SingletonHolder.instance == null) {
			SingletonHolder.instance = new ObjectMapper();
		}
		return SingletonHolder.instance;
	}

	/**
	 * Json to object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param json
	 *            the json
	 * @param clazz
	 *            the clazz
	 * @return the t
	 * @throws TechnicalException
	 *             the techincal exception
	 */
	public static <T> T jsonToObject(String json, Class<T> clazz)
			throws TechnicalException {
		try {
			return getMapperInstance(false).readValue(json, clazz);
		} catch (IOException e) {
			throw new TechnicalException(e.getMessage(), e);
		}
	}

	/**
	 * Object to json.
	 *
	 * @param <T>
	 *            the generic type
	 * @param object
	 *            the object
	 * @return the string
	 * @throws TechnicalException
	 *             the techincal exception
	 */
	public static <T> String objectToJson(Object object)
			throws TechnicalException {

		try {
			return getMapperInstance(false).writeValueAsString(object);
		} catch (IOException e) {
			throw new TechnicalException(e.getMessage(), e);
		}
	}

	/**
	 * Object to json with subTypes registration.
	 *
	 * @param <T>
	 *            the generic type
	 * @param object
	 *            the object
	 * @param subTypeClasses
	 *            the sub type classes
	 * @return the string
	 * @throws TechnicalException
	 *             the exception
	 */
	@SafeVarargs
	public static <T> String objectToJsonExtended(Object object,
			Class<T>... subTypeClasses) throws TechnicalException {
		try {
			ObjectMapper mapper = getMapperInstance(!ArrayUtils
					.isEmpty(subTypeClasses));
			mapper.registerSubtypes(subTypeClasses);
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new TechnicalException(e.getMessage(), e);
		}
	}

	public static Object get(JSONObject jsonObject, String key) throws JSONException {
		String[] keys = key.split("\\.");
		for (String k : keys) {
			Object o = jsonObject.get(k);
			if (o instanceof JSONObject) {
				jsonObject = (JSONObject) o;
			}
		}
		return jsonObject.get(keys[keys.length - 1]);
	}

}