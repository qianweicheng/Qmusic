package com.qmusic.webdoengine;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.androidquery.util.AQUtility;
import com.qmusic.uitls.BUtilities;

public class BWebdoEngine {
	static HashMap<String, BWebView> cachedWebView;
	public static final String URL_HTML_SPA = "html/index_spa.html";
	public static final String URL_HTML = "html/index.html";

	@SuppressLint("NewApi")
	public static final void init() {
		cachedWebView = new HashMap<String, BWebView>();
		// =============cache for task detail=============
		cachedWebView.put(URL_HTML_SPA, getWebview(URL_HTML_SPA));
		cachedWebView.put(URL_HTML, getWebview(URL_HTML));
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

	/**
	 * return a cached Webview, if there is no such Webview, then create a new
	 * one
	 * 
	 * @param relativeUrl
	 *            : the relative url root path is asset/www/
	 * @return
	 */
	public static final BWebView getWebview(String relativeUrl) {
		BWebView webView = cachedWebView.get(relativeUrl);
		if (webView == null) {
			final Context context = AQUtility.getContext();
			webView = new BWebView(context);
			String url;
			File htmlCache = BUtilities.getHTMLFolder();
			if (htmlCache == null || !new File(htmlCache, relativeUrl).exists()) {
				url = "file:///android_asset/www/" + relativeUrl;
			} else {
				url = String.format("file://%s/%s", BUtilities.getHTMLFolder().getAbsolutePath(), relativeUrl);
			}
			webView.loadUrl(url);
			cachedWebView.put(relativeUrl, webView);
		}
		return webView;
	}
}
