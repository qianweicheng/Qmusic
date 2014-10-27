package com.qmusic.webdoengine;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import com.androidquery.util.AQUtility;
import com.qmusic.uitls.BUtilities;

public class BWebdoEngine {
	static HashMap<String, BWebView> cachedWebView;
	private static final String URL_HTML = "index.html";
	private static final String URL_HTML2 = "index2.html";

	@SuppressLint("NewApi")
	public static final void init(Context context) {
		cachedWebView = new HashMap<String, BWebView>();
		// final Context ctx = AQUtility.getContext();
		// =============cache for task detail=============
		cachedWebView.put(URL_HTML, createWebview(context));
		cachedWebView.put(URL_HTML2, createWebview2(context));
		// ==========================================
	}

	public static final BWebView attachWebview(BWebHost webHost, ViewGroup parent) {
		BWebView webview = cachedWebView.get(URL_HTML);
		if (webview == null) {
			final Context context = AQUtility.getContext();
			webview = createWebview(context);
			cachedWebView.put(URL_HTML, webview);
		}
		webview.attachWebview(webHost, parent);
		return webview;
	}

	public static final BWebView attachWebview2(BWebHost webHost, ViewGroup parent) {
		BWebView webview = cachedWebView.get(URL_HTML2);
		if (webview == null) {
			final Context context = AQUtility.getContext();
			webview = createWebview2(context);
			cachedWebView.put(URL_HTML2, webview);
		}
		webview.attachWebview(webHost, parent);
		return webview;
	}

	private static final BWebView createWebview(Context context) {
		BWebView webview = new BWebView(context);
		String url;
		File htmlCache = BUtilities.getHTMLFolder();
		if (htmlCache == null || !new File(htmlCache, URL_HTML).exists()) {
			url = "file:///android_asset/www/" + URL_HTML;
		} else {
			url = String.format("file://%s/%s", BUtilities.getHTMLFolder().getAbsolutePath(), URL_HTML);
		}
		webview.loadURL(url);
		return webview;

	}

	private static final BWebView createWebview2(Context context) {
		BWebView webview = new BWebView(context);
		String url;
		File htmlCache = BUtilities.getHTMLFolder();
		if (htmlCache == null || !new File(htmlCache, URL_HTML2).exists()) {
			url = "file:///android_asset/www/" + URL_HTML2;
		} else {
			url = String.format("file://%s/%s", BUtilities.getHTMLFolder().getAbsolutePath(), URL_HTML2);
		}
		webview.loadURL(url);
		return webview;

	}
}
