package com.jo.analysis.web.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * HTTP request sender
 *
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/4/20
 *
 * Send HTTP GET/POST/PUT/DELETE request.
 * Return the HttpResponse.
 *
 */
@Component
public class HttpService {

	private static Logger LOG = LoggerFactory.getLogger(HttpService.class);

	/** The Constant DEFAULT_RESPONSE_TYPE. */
	private final String DEFAULT_RESPONSE_TYPE = "application/json;charset=UTF-8";

	/** The Constant UTF_8. */
	private final String UTF_8 = "UTF-8";

	/** The CONTENT_TYPE */
	private final String CONTENT_TYPE = "Content-Type";

	/** The Constant AUTHORIZATION */
	private final String AUTHORIZATION = "Authorization";

	/**
	 * Http GET Request
	 *
	 * @param headers       headers
	 * @param url           url
	 * @param params        params
	 * @param authorization authorization
	 * @return HttpResponse
	 */
	public HttpResponse get(Map<String, String> headers, String url, Map<String, Object> params, String authorization) {
		return sendRequest(RequestBuilder.get(), headers, url, params, null, authorization);
	}

	/**
	 * Http POST Request
	 *
	 * @param headers       headers
	 * @param url           url
	 * @param requestBody   requestBody
	 * @param authorization authorization
	 * @return HttpResponse
	 */
	public HttpResponse post(Map<String, String> headers, String url, Map<String, Object> params, HttpEntity requestBody, String authorization) {
		if (CollectionUtils.isEmpty(headers)) headers.put(CONTENT_TYPE, DEFAULT_RESPONSE_TYPE);
		return sendRequest(RequestBuilder.post(), headers, url, params, requestBody, authorization);
	}

	/**
	 * Http PUT Request
	 *
	 * @param headers       headers
	 * @param url           url
	 * @param requestBody   requestBody
	 * @param authorization authorization
	 * @return HttpResponse
	 */
	public HttpResponse put(Map<String, String> headers, String url, Map<String, Object> params, HttpEntity requestBody, String authorization) {
		if (CollectionUtils.isEmpty(headers)) headers.put(CONTENT_TYPE, DEFAULT_RESPONSE_TYPE);
		return sendRequest(RequestBuilder.put(), headers, url, params, requestBody, authorization);
	}

	/**
	 * Http DELETE Request
	 *
	 * @param headers       headers
	 * @param url           url
	 * @param requestBody   requestBody
	 * @param authorization authorization
	 * @return HttpResponse
	 */
	public HttpResponse delete(Map<String, String> headers, String url, Map<String, Object> params, HttpEntity requestBody, String authorization) {
		if (CollectionUtils.isEmpty(headers)) headers.put(CONTENT_TYPE, DEFAULT_RESPONSE_TYPE);
		return sendRequest(RequestBuilder.delete(), headers, url, params, requestBody, authorization);
	}

	/**
	 * @param request       http request
	 * @param headers       headers
	 * @param url           url
	 * @param params        params
	 * @param requestBody   requestBody
	 * @param authorization authorization
	 * @return HttpResponse
	 */
	private HttpResponse sendRequest(RequestBuilder request, Map<String, String> headers, String url, Map<String, Object> params, HttpEntity requestBody, String authorization) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		request = buildRequest(request, headers, url, params, requestBody, authorization);
		CloseableHttpResponse response = null;
		HttpResponse httpResponse = null;
		try {
			response = httpClient.execute(request.build());
			httpResponse = new HttpResponse(response.getAllHeaders(), response.getEntity());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if(response != null) response.close();
				httpClient.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return httpResponse;
	}

	/**
	 * @param request       request
	 * @param headers       headers
	 * @param url           url
	 * @param params        params
	 * @param requestBody   requestBody
	 * @param authorization authorization
	 * @return request
	 */
	private RequestBuilder buildRequest(RequestBuilder request, Map<String, String> headers, String url, Map<String, Object> params, HttpEntity requestBody, String authorization) {
		// set headers
		if (headers != null && headers.size() > 0) {
			for (String headerKey : headers.keySet()) {
				request.setHeader(headerKey, headers.get(headerKey));
			}
		}
		// set url
		request.setUri(buildUrl(url, params));
		// set requestBody
		if (requestBody != null) request.setEntity(requestBody);
		// set Authorization
		if (authorization != null) request.setHeader(AUTHORIZATION, authorization);
		return request;
	}

	/**
	 * @param url    input URL to be completed
	 * @param params request parameters to send to the remote service
	 * @return full URL with params
	 */
	private String buildUrl(String url, Map<String, Object> params) {
		if (CollectionUtils.isEmpty(params)) return url;
		StringBuilder paramsURL = new StringBuilder();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			if (paramsURL.length() > 0) paramsURL.append("&");
			String key = param.getKey();
			if (!StringUtils.isEmpty(key)) {
				try {
					Object val = param.getValue() == null ? "" : param.getValue();
					paramsURL.append(URLEncoder.encode(key, UTF_8)).append("=");
					paramsURL.append(URLEncoder.encode(val.toString(), UTF_8));
				} catch (UnsupportedEncodingException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		return url + "?" + paramsURL.toString();
	}
}

