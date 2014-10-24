package com.qmusic.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;
import com.qmusic.webdoengine.BWebView;

public class BWebActivity extends BaseActivity {
	static final String TAG = BWebActivity.class.getSimpleName();
	public static final String TITLE = "title";
	public static final String URL = "url";
	public static final String HTML_DATA = "htmlData";
	public static final String SHOW_PROGRESS_BAR = "showProgressBar";
	ProgressBar progressBar;
	ViewGroup webViewContainer;
	BWebView webview;
	MyHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && (bundle.containsKey(URL) || bundle.containsKey(HTML_DATA))) {
			handler = new MyHandler(this);
			String title = bundle.getString(TITLE);
			if (TextUtils.isEmpty(title)) {
				title = getString(R.string.app_name);
			}
			boolean showProgressBar = bundle.getBoolean(SHOW_PROGRESS_BAR, true);
			progressBar = (ProgressBar) findViewById(R.id.activity_web_progressbar);
			if (showProgressBar) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
			webViewContainer = (ViewGroup) findViewById(R.id.activity_web_webview_container);
			webview = new BWebView(this);
			webview.attachWebview(this, webViewContainer, handler);
			String url = bundle.getString(URL);
			String htmlData = bundle.getString(HTML_DATA);
			if (htmlData != null) {
				BLog.v(TAG, "htmlData:" + htmlData);
				webview.loadDataWithBaseURL(url, htmlData, "text/html", "utf-8", null);
			} else {
				webview.loadUrl(url);
			}
		} else {
			finish();
			BLog.e(TAG, "url is null");
		}
	}

	static class MyHandler extends Handler {
		BWebActivity activity;

		public MyHandler(BWebActivity activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == BConstants.MSG_PAGE_FINISH_LOADING) {
				activity.progressBar.setVisibility(View.GONE);
			} else if (msg.what == BConstants.MSG_PAGE_START_LOADING) {
				activity.progressBar.setVisibility(View.VISIBLE);
			}
		}
	}
}
