package com.qmusic.webdoengine;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.androidquery.util.AQUtility;
import com.qmusic.uitls.BUtilities;

public class BWebdoEngine {
	static HashMap<String, BWebView> cachedWebView;
	public static final String URL_HTML = "index.html";
	public static final String URL_HTML2 = "index2.html";

	@SuppressLint("NewApi")
	public static final void init(Context context) {
		cachedWebView = new HashMap<String, BWebView>();
		// final Context ctx = AQUtility.getContext();
		// =============cache for task detail=============
		cachedWebView.put(URL_HTML, createWebview(context));
		cachedWebView.put(URL_HTML2, createWebview2(context));
		// ==========================================
	}

	/**
	 * 
	 * @param type
	 *            : in which webView the statement should be executed, if it is
	 *            empty then all.
	 * @param statement
	 */
	public static final void sendJavascript(final String type, final String statement) {
		if (TextUtils.isEmpty(type)) {
			for (BWebView webView : cachedWebView.values()) {
				webView.sendJavascript(statement);
			}
		} else {
			BWebView webView = cachedWebView.get(type);
			if (webView != null) {
				webView.sendJavascript(statement);
			}
		}
	}

	public static final BWebView attachWebview(BWebHost webHost, ViewGroup parent) {
		BWebView webView = cachedWebView.get(URL_HTML);
		if (webView == null) {
			final Context context = AQUtility.getContext();
			webView = createWebview(context);
			cachedWebView.put(URL_HTML, webView);
		}
		webView.attachWebview(webHost, parent);
		return webView;
	}

	public static final BWebView attachWebview2(BWebHost webHost, ViewGroup parent) {
		BWebView webView = cachedWebView.get(URL_HTML2);
		if (webView == null) {
			final Context context = AQUtility.getContext();
			webView = createWebview2(context);
			cachedWebView.put(URL_HTML2, webView);
		}
		webView.attachWebview(webHost, parent);
		return webView;
	}

	private static final BWebView createWebview(Context context) {
		BWebView webView = new BWebView(context);
		String url;
		File htmlCache = BUtilities.getHTMLFolder();
		if (htmlCache == null || !new File(htmlCache, URL_HTML).exists()) {
			url = "file:///android_asset/www/" + URL_HTML;
		} else {
			url = String.format("file://%s/%s", BUtilities.getHTMLFolder().getAbsolutePath(), URL_HTML);
		}
		webView.loadUrl(url);
		return webView;

	}

	private static final BWebView createWebview2(Context context) {
		BWebView webView = new BWebView(context);
		String url;
		File htmlCache = BUtilities.getHTMLFolder();
		if (htmlCache == null || !new File(htmlCache, URL_HTML2).exists()) {
			url = "file:///android_asset/www/" + URL_HTML2;
		} else {
			url = String.format("file://%s/%s", BUtilities.getHTMLFolder().getAbsolutePath(), URL_HTML2);
		}
		webView.loadUrl(url);
		return webView;

	}

}
