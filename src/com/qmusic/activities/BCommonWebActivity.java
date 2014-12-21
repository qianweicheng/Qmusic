package com.qmusic.activities;

import java.util.Date;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qmusic.R;
import com.qmusic.uitls.BLog;
import com.qmusic.webdoengine.BWebdoEngine;

public class BCommonWebActivity extends BWebActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void init() {
		btnBack = findViewById(R.id.activity_web_back_btn);
		btnForward = findViewById(R.id.activity_web_forward_btn);
		btnClose = findViewById(R.id.activity_web_close_btn);
		txtTitle = (TextView) findViewById(R.id.activity_web_title_txt);
		btnBack.setOnClickListener(this);
		btnForward.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnForward.setVisibility(View.GONE);
		btnClose.setVisibility(View.GONE);
		webViewContainer = (ViewGroup) findViewById(R.id.activity_web_container);
		webHost = new MyWebHost(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String title = bundle.getString(TITLE);
			if (TextUtils.isEmpty(title)) {
				txtTitle.setText(title);
				txtTitle.setVisibility(View.GONE);
			} else {
				txtTitle.setText(title);
				txtTitle.setVisibility(View.VISIBLE);
				txtTitle.setSelected(true);
			}
			showProgressBar = bundle.getBoolean(SHOW_PROGRESS_BAR, false);
			progressBar = (ProgressBar) findViewById(R.id.activity_web_progressbar);
			if (showProgressBar) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
			int mode = bundle.getInt("mode");
			if (mode == 2) {
				webView = BWebdoEngine.getWebview(BWebdoEngine.URL_HTML);
				webHost.setAnimateWebView(false);
			} else {
				webView = BWebdoEngine.getWebview(BWebdoEngine.URL_HTML_SPA);
			}
			webView.attachWebview(webHost, webViewContainer);
			webHost.onCreate();
		} else {
			finish();
			BLog.e(TAG, "bundle is null");
		}
	}

	@Override
	public void onBackPressed() {
		String date = new Date().toString();
		webView.sendJavascript(String.format("Qm.nativeEvent('back-clicked','%s');", date));
	}
}
