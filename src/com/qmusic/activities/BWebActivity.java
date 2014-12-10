package com.qmusic.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.uitls.BLog;
import com.qmusic.webdoengine.BJSInterface;
import com.qmusic.webdoengine.BWebHost;
import com.qmusic.webdoengine.BWebView;

public class BWebActivity extends BaseActivity implements OnClickListener {
	public static final String TITLE = "title";
	public static final String URL = "url";
	public static final String HTML_DATA = "htmlData";
	public static final String SHOW_PROGRESS_BAR = "showProgressBar";
	ProgressBar progressBar;
	ViewGroup webViewContainer;
	BWebView webView;
	MyWebHost webHost;
	boolean showProgressBar;
	View btnBack, btnForward, btnClose;
	TextView txtTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		init();
	}

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
		if (bundle != null && (bundle.containsKey(URL) || bundle.containsKey(HTML_DATA))) {
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
	}

	@Override
	public void onBackPressed() {
		btnBack.performClick();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.activity_web_back_btn) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finishMyself();
			}
		} else if (viewId == R.id.activity_web_forward_btn) {
			webView.goForward();
		} else if (viewId == R.id.activity_web_close_btn) {
			finishMyself();
		}
	}

	private void updateNav() {
		boolean hideTitle = false;
		if (webView.canGoForward()) {
			hideTitle = true;
			btnForward.setVisibility(View.VISIBLE);
		} else {
			btnForward.setVisibility(View.GONE);
		}
		if (webView.canGoBackOrForward(-1)) {
			hideTitle = true;
			btnClose.setVisibility(View.VISIBLE);
		} else {
			btnClose.setVisibility(View.GONE);
		}
		if (hideTitle) {
			txtTitle.setVisibility(View.GONE);
		} else {
			txtTitle.setVisibility(View.VISIBLE);
		}
	}

	private void finishMyself() {
		// if (backToSmartList) {
		// Intent intent = new Intent(this, SmartTaskActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// }
		if (webView != null) {
			webView.detachWebview(webViewContainer);
		}
		finish();
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
				BWebActivity.this.updateNav();
			} else {
				return super.onMessage(arg0, arg1, obj);
			}
			return null;
		}
	}
}
