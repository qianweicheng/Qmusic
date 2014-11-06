package com.qmusic.webdoengine;

import java.lang.reflect.Method;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qmusic.MyApplication;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;

@SuppressLint("NewApi")
public class BWebView extends WebView {
	private static final String TAG = BWebView.class.getSimpleName();
	private BWebHost webHost;
	private boolean isReady;
	private BNativeToJsMessageQueue queue;
	private LinkedList<String> cachedJavascript;
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

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		setHorizontalScrollBarEnabled(false);
		setScrollbarFadingEnabled(true);
		final WebSettings settings = getSettings();
		setWebChromeClient(new BWebChromeClient(this));
		setWebViewClient(new BWebViewClient(this));
		setVerticalScrollBarEnabled(false);// disable scrollbar so it won't
											// show in horizontal swipe
		settings.setJavaScriptEnabled(true);
		settings.setSaveFormData(false);
		settings.setSavePassword(false);
		settings.setDatabaseEnabled(true);
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
		webHost.postMessage(BConstants.MSG_PAGE_START_LOADING, 0, null);
		if (isReady) {
			webHost.postMessage(BConstants.MSG_PAGE_FINISH_LOADING, 0, null);
		}
	}

	public void detachWebview(ViewGroup parent) {
		ViewParent parent1 = getParent();
		if (null != parent1) {
			// Check if it is the right parent
			if (parent == parent1) {
				this.webHost = null;
				parent.removeView(this);
			} else {
				BLog.w(TAG, "parent is not the webview's parent");
			}
		}
	}

	public void sendJavascript(String statement) {
		if (isReady) {
			queue.addJavaScript(statement);
		} else {
			if (cachedJavascript == null) {
				cachedJavascript = new LinkedList<String>();
			}
			cachedJavascript.add(statement);
			BLog.w(TAG, "execute js before page loaded:" + statement);
		}
	}

	public void setWebHost(BWebHost webHost) {
		this.webHost = webHost;
		this.webHost.setWebView(this);
		webHost.postMessage(BConstants.MSG_PAGE_START_LOADING, 0, null);
		if (isReady) {
			webHost.postMessage(BConstants.MSG_PAGE_FINISH_LOADING, 0, null);
		}
	}

	public BWebHost getWebHost() {
		return webHost;
	}

	public boolean getState() {
		return isReady;
	}

	public void setState(boolean isReady) {
		this.isReady = isReady;
		if (isReady && cachedJavascript != null && cachedJavascript.size() > 0) {
			for (String statement : cachedJavascript) {
				queue.addJavaScript(statement);
			}
			cachedJavascript.clear();
		}
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

	@JavascriptInterface
	public void performSelector(String method, String arg1, String arg2) {
		this.invokeJavaMethod(method, arg1, arg2);
	}

	@JavascriptInterface
	public void performSelector(String method, String arg1, String arg2, String arg3) {
		this.invokeJavaMethod(method, arg1, arg2, arg3);
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
			if (jsInterface == null) {
				BLog.e(TAG, "jsInterface is null");
				return;
			}
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

}
