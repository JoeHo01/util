package com.jo.analysis.web.httpclient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity HttpResponse
 *
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018-4-20
 *
 * The packaged return by HttpService.
 * There are headers and responseBody in it.
 *
 */
public class HttpResponse {
	Logger logger = LoggerFactory.getLogger(HttpResponse.class);

	private Map<String, String> headers;

	private String responseBody;

	public HttpResponse() {
		this.headers = new HashMap<>();
	}

	public HttpResponse(Header[] headers, HttpEntity httpEntity) {
		this.headers = new HashMap<>();
		setHeaders(headers);
		setResponseBody(httpEntity);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getHeader(String name) {
		return this.headers.get(name);
	}

	public void setHeaders(Header[] headers) {
		for (Header header : headers) {
			this.headers.put(header.getName(), header.getValue());
		}
	}

	public String getResponseBody() {
		return responseBody;
	}

	public String getResponseBody(String charset) {
		try {
			return new String(this.responseBody.getBytes("ISO-8859-1"), charset);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return responseBody;
		}
	}

	public void setResponseBody(HttpEntity httpEntity) {
		try {
			this.responseBody = EntityUtils.toString(httpEntity, "ISO-8859-1");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
