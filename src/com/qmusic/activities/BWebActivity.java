package com.qmusic.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.androidquery.util.AQUtility;
import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.controls.CommonTitle;
import com.qmusic.uitls.BLog;
import com.qmusic.webdoengine.BJSInterface;
import com.qmusic.webdoengine.BWebHost;
import com.qmusic.webdoengine.BWebView;

public class BWebActivity extends BaseActivity {
	public static final String TITLE = "title";
	public static final String URL = "url";
	public static final String HTML_DATA = "htmlData";
	public static final String SHOW_PROGRESS_BAR = "showProgressBar";
	ProgressBar progressBar;
	ViewGroup webViewContainer;
	BWebView webView;
	MyWebHost webHost;
	boolean showProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		webViewContainer = (ViewGroup) findViewById(R.id.activity_web_webview_container);
		webHost = new MyWebHost(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && (bundle.containsKey(URL) || bundle.containsKey(HTML_DATA))) {
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
			webView = new BWebView(this);
			webView.attachWebview(webHost, webViewContainer);
			webHost.onCreate();
			String url = bundle.getString(URL);
			String htmlData = bundle.getString(HTML_DATA);
			if (htmlData != null) {
				BLog.v(TAG, "htmlData:" + htmlData);
				webView.loadDataWithBaseURL(url, htmlData, "text/html", "utf-8", null);
			} else {
				webView.loadUrl(url);
			}
		} else {
			finish();
			BLog.e(TAG, "url is null");
		}
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
		AQUtility.postDelayed(new Runnable() {

			@Override
			public void run() {
				webView.dispatchWindowVisibilityChanged(View.VISIBLE);
				webView.invalidate();
			}
		}, 1000);
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
	protected void onDestroy() {
		super.onDestroy();
		webHost.onDestory();
		webView.detachWebview(webViewContainer);
	}

	class MyWebHost extends BWebHost {
		public MyWebHost(FragmentActivity activity) {
			super(activity);
			setJSInterface(new BJSInterface(this));
		}

		@Override
		public Object onMessage(int arg0, int arg1, Object obj) {
			if (arg0 == BConstants.MSG_PAGE_START_LOADING) {
				if (showProgressBar) {
					progressBar.setVisibility(View.VISIBLE);
				}
			} else if (arg0 == BConstants.MSG_PAGE_FINISH_LOADING) {
				if (showProgressBar) {
					progressBar.setVisibility(View.GONE);
				}
			} else {
				return super.onMessage(arg0, arg1, obj);
			}
			return null;
		}
	}
}
