/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */
package com.qmusic.webdoengine;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.webkit.WebView;

import com.qmusic.MyApplication;
import com.qmusic.uitls.BLog;

public class BNativeToJsMessageQueue {
	private final LinkedList<String> queue = new LinkedList<String>();
	BridgeMode bridgeMode;
	WebView webView;

	public BNativeToJsMessageQueue(WebView webView) {
		this.bridgeMode = new LoadUrlBridgeMode();
		this.webView = webView;
	}

	/**
	 * Clears all messages and resets to the default bridge mode.
	 */
	public void reset() {
		synchronized (this) {
			queue.clear();
		}
	}

	private String popAndEncode() {
		synchronized (this) {
			if (queue.isEmpty()) {
				return null;
			}
			String message = queue.removeFirst();
			return message;
		}
	}

	/**
	 * support called from background thread
	 * 
	 * @param statement
	 */
	public void addJavaScript(String statement) {
		synchronized (this) {
			queue.add(statement);
			bridgeMode.onNativeToJsMessageAvailable();
		}
	}

	private interface BridgeMode {
		void onNativeToJsMessageAvailable();
	}

	/** Uses webView.loadUrl("javascript:") to execute messages. */
	private class LoadUrlBridgeMode implements BridgeMode {
		final Runnable runnable = new Runnable() {
			@SuppressLint("NewApi")
			public void run() {
				String js = popAndEncode();
				if (js != null) {
					int end = js.length() > 100 ? 100 : js.length() - 1;
					String jsDisp = js.substring(0, end);
					BLog.w("BWebView", "executing:" + jsDisp);
					// webView.loadUrl(jsFull);
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
						webView.evaluateJavascript(js, null);
					} else {
						webView.loadUrl("javascript:" + js);
					}
				}
			}
		};

		public void onNativeToJsMessageAvailable() {
			if (Looper.myLooper() == Looper.getMainLooper()) {
				runnable.run();
			} else {
				MyApplication.post(runnable);
			}
		}
	}
}
