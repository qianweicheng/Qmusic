package com.qmusic.webdoengine;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qmusic.MyApplication;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;

@SuppressLint("NewApi")
public class BWebView extends WebView {
	static final String TAG = BWebView.class.getSimpleName();
	BWebHost webHost;
	boolean isLoading;
	BNativeToJsMessageQueue queue;
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
		addJavascriptInterface(this, "GAJavaScript");
		// removeJavascriptInterface("GAJavaScript");
	}

	public void attachWebview(BWebHost webHost, ViewGroup parent) {
		this.webHost = webHost;
		this.webHost.setWebView(this);
		ViewParent oldParent = getParent();
		if (oldParent == null) {
			parent.addView(this);
		} else if (oldParent != parent) {
			BLog.w(TAG, "webview has already a parent.");
			((ViewGroup) oldParent).removeView(this);
			parent.addView(this);
		}
		if (!isLoading && !TextUtils.isEmpty(getUrl())) {
			webHost.sendMessage(BConstants.MSG_PAGE_FINISH_LOADING, 0, null);
		}
	}

	public void detachWebview(ViewGroup parent) {
		ViewParent parent1 = getParent();
		if (null != parent1) {
			// Check if it is the right parent
			if (parent == parent1) {
				this.webHost = null;
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
		if (isLoading) {
			BLog.e(TAG, "execute js before page loaded:" + statement);
		} else {
			queue.addJavaScript(statement);
		}
	}

	public void setWebHost(BWebHost webHost) {
		this.webHost = webHost;
	}

	public BWebHost getWebHost() {
		return webHost;
	}

	public FragmentActivity getActivity() {
		return webHost.getHost();
	}

	// ===========================================================
	@JavascriptInterface
	public void performSelector(String method) {
		this.invokeJavaMethod(method);
	}

	@JavascriptInterface
	public void performSelector(String method, String arg) {
		this.invokeJavaMethod(method, arg);
	}

	/**
	 * A bridget that connects webdo javascrit and Java implementations webdo JS
	 * will call GAJavaScript.performSelector(method, parameters) the Java
	 * function, "performSelector", will find and invoke the function that
	 * defined by "method"
	 * 
	 * @param method
	 *            name of the Java function to invoke
	 * @param arg1
	 *            1st argument for the Java function
	 * @param arg2
	 *            2nd argument for the Java function
	 */
	@JavascriptInterface
	public void performSelector(String method, String arg1, String arg2) {
		this.invokeJavaMethod(method, arg1, arg2);
	}

	/** JS bridge */
	@SuppressWarnings("rawtypes")
	private void invokeJavaMethod(String... args) {
		try {
			if (webHost == null) {
				BLog.e(TAG, "webHost is null");
				return;
			}
			Object jsInterface = webHost.getJSInterface();
			int numberOfArgs = args.length;
			Class[] argClasses = null;
			if (numberOfArgs < 1) {
				BLog.e(TAG, "Javascript called performSelector with no arguments");
				return;
			} else if (numberOfArgs > 1) {
				argClasses = new Class[numberOfArgs - 1];
				for (int i = 0; i < numberOfArgs - 1; i++) {
					argClasses[i] = String.class;
				}
				BLog.v(TAG, "args[0]=" + args[0]);
			}

			// originally the method name is for Objective C, on Android, the
			// ":" needs to be removed
			// e.g. executeTask:params: -> executeTaskparams
			final String methodName = args[0].replaceAll(":", "");
			BLog.d(TAG, "MethodName=" + args[0] + " normalized method=" + methodName);
			Method method = jsInterface.getClass().getDeclaredMethod(methodName, argClasses);
			method.invoke(jsInterface, (Object[]) java.util.Arrays.copyOfRange(args, 1, numberOfArgs));
		} catch (Exception e) {
			e.printStackTrace();
			for (String a : args) {
				BLog.e(TAG, a);
			}
		}
	}

	// ===========================================================
	class BWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			BLog.d(TAG, "onPageStart:" + url);
			super.onPageStarted(view, url, favicon);
			isLoading = true;
			if (webHost != null) {
				webHost.sendMessage(BConstants.MSG_PAGE_START_LOADING, 0, null);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			BLog.d(TAG, "onPageFinished:" + url);
			isLoading = false;
			if (webHost != null) {
				webHost.sendMessage(BConstants.MSG_PAGE_FINISH_LOADING, 0, null);
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			BLog.i(TAG, url);
			View parent = (View) view.getParent();
			if (parent != null && parent.getContext() instanceof FragmentActivity) {
				BRoutingHelper.process((FragmentActivity) parent.getContext(), url);
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
