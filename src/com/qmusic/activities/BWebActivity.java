package com.qmusic.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.qmusic.R;
import com.qmusic.controls.dialogs.BToast;
import com.qmusic.uitls.BLog;

public class BWebActivity extends BaseActivity {
	static final String TAG = BWebActivity.class.getSimpleName();
	public static final String TITLE = "title";
	public static final String URL = "url";
	public static final String HTML_DATA = "htmlData";
	public static final String SHOW_PROGRESS_BAR = "showProgressBar";
	ProgressBar progressBar;
	WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && (bundle.containsKey(URL) || bundle.containsKey(HTML_DATA))) {
			String title = bundle.getString(TITLE);
			if (TextUtils.isEmpty(title)) {
				title = getString(R.string.app_name);
			}
			setContentView(R.layout.activity_web);
			boolean showProgressBar = bundle.getBoolean(SHOW_PROGRESS_BAR, true);
			progressBar = (ProgressBar) findViewById(R.id.activity_web_progressbar);
			if (showProgressBar) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
			webView = (WebView) findViewById(R.id.activity_web_webview);
			WebSettings settins = webView.getSettings();
			settins.setSupportZoom(true);
			settins.setJavaScriptEnabled(true);
			settins.setBuiltInZoomControls(true);
			syncStaticSettings(settins, this);
			webView.setWebViewClient(new BWebClient());
			String url = bundle.getString("url");
			String htmlData = bundle.getString("htmlData");
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

	private void syncStaticSettings(WebSettings settings, Context context) {
		// settings.setDefaultFontSize(16);
		// settings.setDefaultFixedFontSize(13);
		// //settings.setPageCacheCapacity(1);
		//
		// settings.setNeedInitialFocus(false);
		// settings.setSupportMultipleWindows(true);
		// settings.setEnableSmoothTransition(true);
		//
		// //settings.setProperty("use_minimal_memory", "false");
		// settings.setAllowContentAccess(false);

		// HTML5 API flags
		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);

		// settings.setAppCacheMaxSize(5*1024*1024);
		settings.setAppCachePath(context.getDir("appcache", 0).getPath());
		settings.setDatabasePath(context.getDir("databases", 0).getPath());
		settings.setGeolocationDatabasePath(context.getDir("geolocation", 0).getPath());
	}

	static final String SCHEME_MAIL = "mailto:";
	static final String SCHEME_MARKET = "market://";

	final class BWebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			BLog.i(TAG, "loading " + url);
			if (url.startsWith(SCHEME_MAIL)) {
				try {
					Intent data = new Intent(Intent.ACTION_SENDTO);
					data.setData(Uri.parse(url));
					data.putExtra(Intent.EXTRA_SUBJECT, "");
					data.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(""));
					startActivity(data);
				} catch (ActivityNotFoundException ex) {
					BToast.toast("无法启动邮件客户端");
				}
				return true;
			} else if (url.startsWith(SCHEME_MARKET)) {
				try {
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					BToast.toast("无法启动应用商店");
				}
				return true;
			}
			progressBar.setVisibility(View.VISIBLE);
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			BLog.i(TAG, "loaded " + url);
			progressBar.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			BLog.i(TAG, "onReceivedError code= " + errorCode + " description=" + description);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			super.onReceivedSslError(view, handler, error);
			BLog.i(TAG, "onReceivedSslError error= " + error);
		}
	}
}
