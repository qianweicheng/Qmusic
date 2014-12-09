package com.qmusic.volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;

import android.util.Pair;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.loopj.android.http.AsyncHttpClient;

public class QMusicFileRequest extends QmusicRequest<HttpEntity> {
	protected static final int BUFFER_SIZE = 4096;
	File targetFile;
	Listener<Pair<Integer, Integer>> listener;

	public QMusicFileRequest(final int method, final String url, final File targetFile, final Listener<Pair<Integer, Integer>> listener,
			final ErrorListener errorListener) {
		super(method, url, new Listener<HttpEntity>() {
			@Override
			public void onResponse(HttpEntity response) {
				listener.onResponse(new Pair<Integer, Integer>(-1, -1));
			}
		}, errorListener);
		this.listener = listener;
		this.targetFile = targetFile;
		setShouldCache(false);
	}

	@Override
	protected Response<HttpEntity> parseNetworkResponse(NetworkResponse response) {
		if (response instanceof QMusicNetworkResponse) {
			if (response.statusCode == 200) {
				writeToFile((QMusicNetworkResponse) response);
				return Response.success(null, parseCacheHeaders(getUrl(), response));
			} else {
				return Response.error(new NetworkError(response));
			}
		} else {
			return Response.error(new NetworkError(response));
		}
	}

	private final void writeToFile(QMusicNetworkResponse qResponse) {
		if (qResponse.entity != null) {
			InputStream instream = null;
			try {
				instream = qResponse.entity.getContent();
				if (instream != null) {
					long contentLength = qResponse.entity.getContentLength();
					FileOutputStream buffer = new FileOutputStream(targetFile);
					try {
						byte[] tmp = new byte[BUFFER_SIZE];
						int l, count = 0;
						// do not send messages if request has been cancelled
						while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
							count += l;
							buffer.write(tmp, 0, l);
							Pair<Integer, Integer> data = new Pair<Integer, Integer>(count, (int) contentLength);
							listener.onResponse(data);
						}
					} finally {
						AsyncHttpClient.silentCloseInputStream(instream);
						buffer.flush();
						AsyncHttpClient.silentCloseOutputStream(buffer);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (instream != null) {
					try {
						instream.close();
					} catch (Exception ignoreErr) {
					}
				}
			}
		}
	}
}
