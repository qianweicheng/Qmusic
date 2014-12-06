/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qmusic.volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T>
 *            JSON type of response expected
 */
public abstract class QmusicRequest<T> extends Request<T> {
	/** Charset for request. */
	private static final String PROTOCOL_CHARSET = "utf-8";

	/** Content type for request. */
	private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
	private static String userAgent = "qmusic_1.0";
	private final Listener<T> mListener;

	private Map<String, String> params;

	public QmusicRequest(int method, String url, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			if (params != null) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> e : params.entrySet()) {
					String key = e.getKey();
					String value = e.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(key, value));
					}
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				HttpEntity entity = new UrlEncodedFormEntity(pairs, PROTOCOL_CHARSET);
				entity.writeTo(bos);
				return bos.toByteArray();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (!TextUtils.isEmpty(userAgent)) {
			if (headers == null) {
				headers = new HashMap<String, String>();
			}
			headers.put("User-Agent", userAgent);
		}
		return headers;
	}

	public static String getUserAgent() {
		return userAgent;
	}

	public static final void setUserAgent(String userAgent) {
		QmusicRequest.userAgent = userAgent;
	}
}
