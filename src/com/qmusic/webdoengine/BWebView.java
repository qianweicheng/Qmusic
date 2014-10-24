package com.qmusic.webdoengine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qmusic.MyApplication;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;

@SuppressLint("NewApi")
public class BWebView extends WebView {
	static final String TAG = BWebView.class.getSimpleName();
	Handler handler;
	boolean isLoading;
	BJSInterface jsInterface;
	BNativeToJsMessageQueue queue;
	FragmentActivity activity;
	// ==============================
	// ==============================
	static {
		if (MyApplication.DEBUG && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
	}

	public BWebView(Context context) {
		super(context);
		init();
	}

	public BWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		setHorizontalScrollBarEnabled(false);
		setScrollbarFadingEnabled(true);
		getSettings().setJavaScriptEnabled(true);
		setWebChromeClient(new BWebChromeClient());
		setWebViewClient(new BWebViewClient());
		setVerticalScrollBarEnabled(false);// disable scrollbar so it won't
											// show in horizontal swipe
		// use GAJavaScript name space to be compatible with iOS version
		queue = new BNativeToJsMessageQueue(this);
		jsInterface = new BJSInterface(this);
		addJavascriptInterface(jsInterface, "GAJavaScript");
		// removeJavascriptInterface("GAJavaScript");
	}

	public void attachWebview(FragmentActivity activity, ViewGroup parent, Handler handler) {
		this.activity = activity;
		this.handler = handler;
		ViewParent oldParent = getParent();
		if (oldParent != parent) {
			BLog.w(TAG, "webview has already a parent.");
			if (oldParent != null) {
				((ViewGroup) oldParent).removeView(this);
			}
			parent.addView(this);
		}
		if (!isLoading && !TextUtils.isEmpty(getUrl()) && handler != null) {
			handler.sendEmptyMessage(BConstants.MSG_PAGE_FINISH_LOADING);
		}
	}

	public void detachWebview(ViewGroup parent) {
		ViewParent parent1 = getParent();
		if (null != parent1) {
			// Check if it is the right parent
			if (parent == parent1) {
				this.activity = null;
				this.handler = null;
				// sendJavascript("EDO.webdo.ui.clear();");
				// clearView();
				((ViewGroup) parent1).removeView(this);
			} else {
				BLog.w(TAG, "parent is not the webview's parent");
			}
		}
	}

	public void loadURL(String url) {
		BLog.w(TAG, "Loading webdo url =" + url);
		String currentURL = getUrl();
		if (currentURL != null && currentURL.startsWith(url)) {
			BLog.i(TAG, "no need reload url");
			invalidate();
		} else {
			loadUrl(url);
		}
	}

	public void sendJavascript(String statement) {
		queue.addJavaScript(statement);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Handler getHandler() {
		return handler;
	}

	public FragmentActivity getActivity() {
		return activity;
	}

	class BWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			BLog.d(TAG, "onPageStart:" + url);
			super.onPageStarted(view, url, favicon);
			isLoading = true;
			if (handler != null) {
				handler.sendEmptyMessage(BConstants.MSG_PAGE_START_LOADING);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			BLog.d(TAG, "onPageFinished:" + url);
			isLoading = false;
			if (handler != null) {
				handler.sendEmptyMessage(BConstants.MSG_PAGE_FINISH_LOADING);
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			BLog.i(TAG, url);
			View parent = (View) view.getParent();
			if (parent != null && parent.getContext() instanceof FragmentActivity) {
				BSchemeHelper.process((FragmentActivity) parent.getContext(), url);
				return true;
			} else {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}
	}

	static class BWebChromeClient extends WebChromeClient {

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			BLog.i(TAG, consoleMessage.sourceId() + "(" + consoleMessage.lineNumber() + "):" + consoleMessage.message());
			return true;
			// if open this, there will another log with the same output, one of
			// which is from onConsoleMessage
			// return super.onConsoleMessage(consoleMessage);
		}

		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			BLog.d(TAG, String.format("2-%s(%d):%s", sourceID, lineNumber, message));
		}
	}
}
