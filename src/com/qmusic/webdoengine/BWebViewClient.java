package com.qmusic.webdoengine;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;

public class BWebViewClient extends WebViewClient {
	static final String TAG = "BWebViewClient";
	BWebView webView;

	public BWebViewClient(final BWebView webView) {
		this.webView = webView;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		BLog.d(TAG, "onPageStart:" + url);
		super.onPageStarted(view, url, favicon);
		webView.setState(false);
		final BWebHost webHost = webView.getWebHost();
		if (webHost != null) {
			webHost.postMessage(BConstants.MSG_PAGE_START_LOADING, 0, null);
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		BLog.d(TAG, "onPageFinished:" + url);
		webView.setState(true);
		final BWebHost webHost = webView.getWebHost();
		if (webHost != null) {
			webHost.postMessage(BConstants.MSG_PAGE_FINISH_LOADING, 0, null);
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