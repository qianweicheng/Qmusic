package com.qmusic.volley;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class QMusicStringRequest extends QmusicRequest<String> {
	public QMusicStringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, parseCacheHeaders(getUrl(), response));
	}
}
