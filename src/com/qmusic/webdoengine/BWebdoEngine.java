package com.qmusic.webdoengine;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;

import com.qmusic.uitls.BUtilities;

public class BWebdoEngine {
	static HashMap<String, BWebView> cachedWebView;
	private static final String URL_HTML = "index.html";
	private static Context context;

	@SuppressLint("NewApi")
	public static final void init(Context context) {
		BWebdoEngine.context = context;
		cachedWebView = new HashMap<String, BWebView>();
		// =============cache for task detail=============
		cachedWebView.put(URL_HTML, createWebview(context));
		// ==========================================
	}

	public static final BWebView attachWebview(FragmentActivity activity, ViewGroup parent, Handler handler) {
		BWebView webview = cachedWebView.get(URL_HTML);
		if (webview == null) {
			webview = createWebview(context);
			cachedWebView.put(URL_HTML, webview);
		}
		webview.attachWebview(activity, parent, handler);
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
}
