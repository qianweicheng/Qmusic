package com.qmusic.webdoengine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

import com.qmusic.common.BConstants;
import com.qmusic.controls.dialogs.AlertDialogFragment;
import com.qmusic.controls.dialogs.SimpleFragmentDialogCallback;
import com.qmusic.uitls.BLog;

public class BWebChromeClient extends WebChromeClient {
	static final String TAG = "BWebChromeClient";
	BWebView webView;

	public BWebChromeClient(BWebView webView) {
		this.webView = webView;
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
		return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
	}

	@Override
	public void onCloseWindow(WebView window) {
		super.onCloseWindow(window);
	}

	@Override
	public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
		super.onGeolocationPermissionsShowPrompt(origin, callback);
	}

	@Override
	public void onGeolocationPermissionsHidePrompt() {
		super.onGeolocationPermissionsHidePrompt();
	}

	@Override
	public boolean onJsAlert(final WebView view, final String url, final String message, final JsResult result) {
		final BWebHost webHost = webView.getWebHost();
		final FragmentActivity activity = webHost.getHost();
		if (webHost == null || activity == null) {
			return false;
		}
		AlertDialogFragment fragment = AlertDialogFragment.getInstance(null, message, android.R.string.ok);
		fragment.setCallback(new SimpleFragmentDialogCallback() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}

			@Override
			public void cancel() {
				result.cancel();
			}
		});
		fragment.show(activity.getSupportFragmentManager());
		return true;
	}

	@Override
	public boolean onJsBeforeUnload(final WebView view, final String url, final String message, final JsResult result) {
		return super.onJsBeforeUnload(view, url, message, result);
	}

	@Override
	public boolean onJsConfirm(final WebView view, final String url, final String message, final JsResult result) {
		final BWebHost webHost = webView.getWebHost();
		final FragmentActivity activity = webHost.getHost();
		if (webHost == null || activity == null) {
			return false;
		}
		AlertDialogFragment fragment = AlertDialogFragment.getInstance(null, message, android.R.string.ok, android.R.string.cancel);
		fragment.setCallback(new SimpleFragmentDialogCallback() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					result.confirm();
				} else {
					result.cancel();
				}
			}

			@Override
			public void cancel() {
				result.cancel();
			}
		});
		fragment.show(activity.getSupportFragmentManager());
		return true;
	}

	@Override
	public boolean onJsPrompt(final WebView view, final String url, final String message, final String defaultValue, final JsPromptResult result) {
		final BWebHost webHost = webView.getWebHost();
		if (webHost != null) {
			Pair<String, String> pair = new Pair<String, String>(message, defaultValue);
			Object r = webHost.exec(BConstants.MSG_PAGE_JS_CALL, 0, pair);
			if (r != null) {
				result.confirm(r.toString());
				return true;
			}
		}
		final FragmentActivity activity = webHost.getHost();
		if (activity != null) {
			final JsPromptResult res = result;
			AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
			dlg.setMessage(message);
			final EditText input = new EditText(activity);
			if (defaultValue != null) {
				input.setText(defaultValue);
			}
			dlg.setView(input);
			dlg.setCancelable(false);
			dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String usertext = input.getText().toString();
					res.confirm(usertext);
				}
			});
			dlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					res.cancel();
				}
			});
			dlg.show();
		}
		return true;
	}

	@Override
	public void onProgressChanged(final WebView view, final int newProgress) {
		super.onProgressChanged(view, newProgress);
		BWebHost webHost = webView.getWebHost();
		if (webHost != null) {
			webHost.postMessage(BConstants.MSG_PAGE_UPDATE, 0, null);
		}
	}

	@Override
	public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
		BLog.i(TAG, consoleMessage.sourceId() + "(" + consoleMessage.lineNumber() + "):" + consoleMessage.message());
		return true;
		// if open this, there will another log with the same output, one of
		// which is from onConsoleMessage
		// return super.onConsoleMessage(consoleMessage);
	}

	public void onConsoleMessage(final String message, final int lineNumber, final String sourceID) {
		BLog.d(TAG, String.format("2-%s(%d):%s", sourceID, lineNumber, message));
	}
}