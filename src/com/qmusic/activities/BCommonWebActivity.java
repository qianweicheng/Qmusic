package com.qmusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.controls.CommonTitle;
import com.qmusic.webdoengine.BJSInterface;
import com.qmusic.webdoengine.BWebHost;
import com.qmusic.webdoengine.BWebView;
import com.qmusic.webdoengine.BWebdoEngine;

public class BCommonWebActivity extends BaseActivity {
	public static final String SHOW_PROGRESS_BAR = "showProgressBar";
	public static final String TITLE = "title";
	ProgressBar progressBar;
	boolean showProgressBar;
	ViewGroup webViewContainer;
	BWebView webView;
	MyWebHost webHost;
	int mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		webViewContainer = (ViewGroup) findViewById(R.id.activity_web_webview_container);
		webHost = new MyWebHost(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String title = bundle.getString(TITLE);
			if (TextUtils.isEmpty(title)) {
				title = "";// getString(R.string.app_name);
			}
			CommonTitle commonTitle = (CommonTitle) findViewById(R.id.activity_web_title);
			commonTitle.setTitle(title);
			showProgressBar = bundle.getBoolean(SHOW_PROGRESS_BAR, false);
			progressBar = (ProgressBar) findViewById(R.id.activity_web_progressbar);
			if (showProgressBar) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
			mode = bundle.getInt("mode");
		}
		if (mode == 2) {
			webView = BWebdoEngine.getWebview(BWebdoEngine.URL_HTML);
			webHost.setAnimateWebView(false);
		} else {
			webView = BWebdoEngine.getWebview(BWebdoEngine.URL_HTML_SPA);
		}
		webView.attachWebview(webHost, webViewContainer);
		webHost.onCreate();
	}

	@Override
	protected void onStart() {
		super.onStart();
		webHost.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webHost.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		webHost.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		webHost.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		webHost.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		webHost.onDestory();
		webView.detachWebview(webViewContainer);
	}

	class MyWebHost extends BWebHost {

		public MyWebHost(BCommonWebActivity activity) {
			super(activity);
			setJSInterface(new BJSInterface(this));
		}

		@Override
		public Object onMessage(int what, int arg1, Object obj) {
			super.onMessage(what, arg1, obj);
			if (what == BConstants.MSG_PAGE_START_LOADING) {
				if (showProgressBar) {
					progressBar.setVisibility(View.VISIBLE);
				}
			} else if (what == BConstants.MSG_PAGE_FINISH_LOADING) {
				if (showProgressBar) {
					progressBar.setVisibility(View.GONE);
				}
			} else {
				return super.onMessage(what, arg1, obj);
			}
			return null;
		}

	}
}
