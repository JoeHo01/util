package com.jo.analysis.web.httpclient;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

/**
 * HttpUtil
 *
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018-4-20
 *
 */
public class HttpUtil {

	/**
	 * Builds the basic auth.
	 *
	 * @param user the user
	 * @param password the password
	 * @return the string
	 */
	public static String buildBasicAuth(String user, String password) {
		if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
			return null;
		} else {
			return "Basic " + Base64.encodeBase64String((user + ":" + password).getBytes());
		}
	}
}
