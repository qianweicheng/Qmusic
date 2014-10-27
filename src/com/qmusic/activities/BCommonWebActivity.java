package com.qmusic.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.androidquery.util.AQUtility;
import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.controls.CommonTitle;
import com.qmusic.webdoengine.BJSInterface;
import com.qmusic.webdoengine.BRoutingHelper;
import com.qmusic.webdoengine.BWebHost;
import com.qmusic.webdoengine.BWebView;
import com.qmusic.webdoengine.BWebdoEngine;

public class BCommonWebActivity extends BaseActivity {
	public static final String SHOW_PROGRESS_BAR = "showProgressBar";
	public static final String TITLE = "title";
	ProgressBar progressBar;
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
			boolean showProgressBar = bundle.getBoolean(SHOW_PROGRESS_BAR, true);
			progressBar = (ProgressBar) findViewById(R.id.activity_web_progressbar);
			if (showProgressBar) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
			mode = bundle.getInt("mode");
		}
		if (mode == 2) {
			webView = BWebdoEngine.attachWebview2(webHost, webViewContainer);
		} else {
			webView = BWebdoEngine.attachWebview(webHost, webViewContainer);
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
		JSONObject params;

		public MyWebHost(BCommonWebActivity activity) {
			super(activity);
			setJSInterface(new BJSInterface(this));
		}

		@Override
		public Object handleMessage(int what, int arg1, Object obj) {
			if (what == BConstants.MSG_PAGE_FINISH_LOADING) {
				progressBar.setVisibility(View.GONE);
			} else if (what == BConstants.MSG_PAGE_START_LOADING) {
				progressBar.setVisibility(View.VISIBLE);
			} else if (what == BConstants.MSG_JUMP_TO_ACTIVITY) {
				params = (JSONObject) obj;
				final String activityInfo = params.optString("page");
				final Class<?> classInfo = BRoutingHelper.getActivityInfo(activityInfo);
				final Intent intent = new Intent(BCommonWebActivity.this, classInfo);
				BCommonWebActivity.this.startActivityForResult(intent, 100);
			} else {
				return super.sendMessage(what, arg1, obj);
			}
			return null;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent intent) {
			if (requestCode == 100) {
				final String callback = params.optString("callback");
				if (resultCode == RESULT_OK) {
					webView.sendJavascript(String.format("%s('%s');", callback, "OK"));
				} else {
					webView.sendJavascript(String.format("%s('%s');", callback, "Cancel"));
				}
			}
		}
	}
}
