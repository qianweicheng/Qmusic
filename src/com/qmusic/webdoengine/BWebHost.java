package com.qmusic.webdoengine;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.androidquery.util.AQUtility;
import com.qmusic.uitls.BLog;

public abstract class BWebHost {
	static final String TAG = "BWebHost";
	private FragmentActivity activity;
	private Object jsInterface;
	private BWebView webView;

	public BWebHost(FragmentActivity activity) {
		this.activity = activity;
	}

	public FragmentActivity getHost() {
		return activity;
	}

	public BWebView getWebView() {
		return webView;
	}

	public void setWebView(BWebView webView) {
		this.webView = webView;
	}

	public Object getJSInterface() {
		return jsInterface;
	}

	public void setJSInterface(Object jsInterface) {
		this.jsInterface = jsInterface;
	}

	public void onCreate() {
		// Note:do nothing
	}

	public void onStart() {
		webView.sendJavascript("onStart();");
	}

	public void onResume() {
		webView.sendJavascript("onResume();");
	}

	public void onPause() {
		webView.sendJavascript("onPause();");
	}

	public void onStop() {
		webView.sendJavascript("onStop();");
	}

	public void onDestory() {
		activity = null;
		webView = null;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	}

	/**
	 * call directly
	 * 
	 * @param what
	 * @param arg1
	 * @param obj
	 * @return
	 */
	public final Object sendMessage(int what, int arg1, Object obj) {
		Object result = handleMessage(what, arg1, obj);
		return result;
	}

	/**
	 * use message queue
	 * 
	 * @param what
	 * @param arg1
	 * @param obj
	 */
	public final void postMessage(final int what, final int arg1, final Object obj) {
		AQUtility.post(new Runnable() {
			@Override
			public void run() {
				handleMessage(what, arg1, obj);
			}
		});
	}

	/**
	 * only be called by sub-class
	 * 
	 * @param what
	 * @param arg1
	 * @param obj
	 * @return
	 */
	protected Object handleMessage(int what, int arg1, Object obj) {
		BLog.w(TAG, String.format("arg0:%d,arg1:%d,obj:%s", what, arg1, obj));
		return null;
	}
}
