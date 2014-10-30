package com.qmusic.webdoengine;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.qmusic.activities.LoginActivity;
import com.qmusic.common.IAsyncDataCallback;
import com.qmusic.controls.dialogs.AlertDialogFragment;
import com.qmusic.controls.dialogs.LoadingDialogFragment;
import com.qmusic.controls.dialogs.SimpleFragmentDialogCallback;
import com.qmusic.uitls.BLog;

public class BJSInterface {
	static final String TAG = BJSInterface.class.getSimpleName();
	BWebHost webHost;

	public BJSInterface(BWebHost webHost) {
		this.webHost = webHost;
	}

	/**
	 * Show warning message, do NOT dismiss task detail activity
	 * 
	 * @param msg
	 */
	public void warning(final String msg) {
		final FragmentActivity context = webHost.getHost();
		if (context != null) {
			AlertDialogFragment fragment = AlertDialogFragment.getInstance(null, msg, context.getString(android.R.string.ok));
			fragment.getArguments().putInt("icon", android.R.drawable.ic_dialog_alert);
			fragment.show(context.getSupportFragmentManager());
		}
	}

	public void alert(final String msg, final String positiveStr, final String negativeStr, final String callback) {
		final FragmentActivity context = webHost.getHost();
		final BWebView webView = webHost.getWebView();
		if (context != null && webView != null) {
			AlertDialogFragment fragment = AlertDialogFragment.getInstance(null, msg, context.getString(android.R.string.ok));
			fragment.getArguments().putInt("icon", android.R.drawable.ic_dialog_info);
			fragment.setCallback(new SimpleFragmentDialogCallback() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						webView.sendJavascript(String.format("%s(\"OK\")", callback));
					} else if (which == DialogInterface.BUTTON_NEGATIVE) {
						webView.sendJavascript(String.format("%s(\"CANCEL\")", callback));
					}
				}

			});
			fragment.show(context.getSupportFragmentManager());
		}
	}

	/**
	 * Jump to another activity
	 * 
	 * @param params
	 *            : { page:'page name', callback:'the callback function',... }
	 */
	public void jumpTo(String params) {
		final FragmentActivity activity = webHost.getHost();
		if (activity != null && !TextUtils.isEmpty(params)) {
			JSONObject json = null;
			try {
				json = new JSONObject(params);
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			final String jsCallback = json.optString("callback");
			final String page = json.optString("page");
			Class<?> classInfo = null;
			if ("login".equalsIgnoreCase(page)) {
				classInfo = LoginActivity.class;
			}
			if (classInfo == null) {
				BLog.e(TAG, "Can't open page:" + page);
				return;
			}
			Intent intent = new Intent(activity, classInfo);
			BRoutingHelper.putExtra(intent, json);
			if (TextUtils.isEmpty(jsCallback)) {
				activity.startActivity(intent);
			} else {
				final IAsyncDataCallback<Intent> callback = new IAsyncDataCallback<Intent>() {
					@Override
					public void callback(final int result, final Intent data) {
						final BWebView webView = webHost.getWebView();
						// Note: handler the Intent case by case
						webView.sendJavascript(String.format("%s(%d,%s)", jsCallback, result, ""));
					}
				};
				webHost.startActivityForResult(intent, callback);
			}
		}
	}

	public void longTimeCallback(final String callback) {
		final BWebView webView = webHost.getWebView();
		if (webView != null) {
			final LoadingDialogFragment fragment = LoadingDialogFragment.getInstance();
			final FragmentActivity activity = webHost.getHost();
			final AsyncTask<Void, Void, Object> asyncTask = new AsyncTask<Void, Void, Object>() {
				@Override
				protected void onPreExecute() {
					fragment.show(activity.getSupportFragmentManager());
				}

				@Override
				protected Object doInBackground(Void... params) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return "OK" + System.currentTimeMillis();
				}

				@Override
				protected void onPostExecute(Object result) {
					fragment.dismissAllowingStateLoss();
					webView.sendJavascript(String.format("%s('%s');", callback, result));
				}
			};
			asyncTask.execute();
		}
	}

	public void loadImage(final String url, final String jsCallback, final String tag) {
		final String ASSERT_PREFEX = "file:///android_asset/";
		final BWebView webView = webHost.getWebView();
		if (webView == null) {
			BLog.w(TAG, "webview is null");
			return;
		}
		if (url.startsWith(ASSERT_PREFEX)) {
			webView.sendJavascript(String.format("%s('%s','%s');", jsCallback, url, tag));
		} else {
			AjaxCallback<File> callback = new AjaxCallback<File>() {
				@Override
				public void callback(String url, File object, AjaxStatus status) {
					String imageUrl = "";
					if (object != null) {
						imageUrl = AQUtility.getCacheFile(AQUtility.getCacheDir(AQUtility.getContext()), url).getAbsolutePath();
					}
					webView.sendJavascript(String.format("%s('%s','%s');", jsCallback, imageUrl, tag));
				}
			};
			callback.url(url).type(File.class).fileCache(true);
			callback.async(AQUtility.getContext());
		}
	}

}
