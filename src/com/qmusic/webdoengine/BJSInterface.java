package com.qmusic.webdoengine;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.qmusic.R;
import com.qmusic.common.BLocationManager;
import com.qmusic.controls.dialogs.AlertDialogFragment;
import com.qmusic.controls.dialogs.IFragmentDialogCallback;
import com.qmusic.controls.dialogs.BDateTimePickerFragment.OnDateTimePickerSelectedCallback;
import com.qmusic.uitls.BLog;

@SuppressWarnings("unused")
public class BJSInterface {
	static final String TAG = BJSInterface.class.getSimpleName();
	BWebView webview;

	public BJSInterface(BWebView webview) {
		this.webview = webview;
	}

	@JavascriptInterface
	public void performSelector(String method) {
		this.invokeJavaMethod(method);
	}

	@JavascriptInterface
	public void performSelector(String method, String arg) {
		this.invokeJavaMethod(method, arg);
	}

	/**
	 * A bridget that connects webdo javascrit and Java implementations webdo JS
	 * will call GAJavaScript.performSelector(method, parameters) the Java
	 * function, "performSelector", will find and invoke the function that
	 * defined by "method"
	 * 
	 * @param method
	 *            name of the Java function to invoke
	 * @param arg1
	 *            1st argument for the Java function
	 * @param arg2
	 *            2nd argument for the Java function
	 */
	@JavascriptInterface
	public void performSelector(String method, String arg1, String arg2) {
		this.invokeJavaMethod(method, arg1, arg2);
	}

	/** JS bridge */
	@SuppressWarnings("rawtypes")
	private void invokeJavaMethod(String... args) {
		try {
			int numberOfArgs = args.length;
			Class[] argClasses = null;
			if (numberOfArgs < 1) {
				BLog.e("BWebView", "Javascript called performSelector with no arguments");
				return;
			} else if (numberOfArgs > 1) {
				argClasses = new Class[numberOfArgs - 1];
				for (int i = 0; i < numberOfArgs - 1; i++) {
					argClasses[i] = String.class;
				}
				BLog.v(TAG, "args[0]=" + args[0]);
			}

			// originally the method name is for Objective C, on Android, the
			// ":" needs to be removed
			// e.g. executeTask:params: -> executeTaskparams
			String methodName = args[0].replaceAll(":", "");

			BLog.d(TAG, "MethodName=" + args[0] + " normalized method=" + methodName);

			Method method = this.getClass().getDeclaredMethod(methodName, argClasses);
			method.invoke(this, (Object[]) java.util.Arrays.copyOfRange(args, 1, numberOfArgs));
		} catch (Exception e) {
			e.printStackTrace();
			for (String a : args) {
				BLog.e(TAG, a);
			}
		}
	}

	/**
	 * Show warning message, do NOT dismiss task detail activity
	 * 
	 * @param msg
	 */
	private void warning(final String msg) {
		final FragmentActivity context = webview.getActivity();
		if (context != null) {
			AlertDialogFragment fragment = AlertDialogFragment.getInstance(null, msg, context.getString(android.R.string.ok));
			fragment.getArguments().putInt("icon", android.R.drawable.ic_dialog_alert);
			fragment.show(context.getSupportFragmentManager());
		}
	}

	private void alert(final String msg, final String positiveStr, final String negativeStr) {
		final FragmentActivity context = webview.getActivity();
		if (context != null) {
			AlertDialogFragment fragment = AlertDialogFragment.getInstance(null, msg, context.getString(android.R.string.ok));
			fragment.getArguments().putInt("icon", android.R.drawable.ic_dialog_info);
			fragment.setCallback(new IFragmentDialogCallback() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						webview.sendJavascript("EDO.webdo.ui.okClicked();");
					} else if (which == DialogInterface.BUTTON_NEGATIVE) {
						webview.sendJavascript("EDO.webdo.ui.cancelClicked();");
					}
				}

				@Override
				public void dismiss() {

				}

				@Override
				public void cancel() {

				}
			});
			fragment.show(context.getSupportFragmentManager());
		}
	}

	private void loadImagecallback(final String url, final String jsCallback) {
		final String ASSERT_PREFEX = "file:///android_asset/";
		if (url.startsWith(ASSERT_PREFEX)) {
			webview.sendJavascript(String.format("%s('%s','%s');", jsCallback, url, url));
		} else {
			AjaxCallback<File> callback = new AjaxCallback<File>() {
				@Override
				public void callback(String url, File object, AjaxStatus status) {
					String imageUrl = "";
					if (object != null) {
						imageUrl = AQUtility.getCacheFile(AQUtility.getCacheDir(AQUtility.getContext()), url).getAbsolutePath();
					}
					webview.sendJavascript(String.format("%s('%s','%s');", jsCallback, imageUrl, url));
				}
			};
			callback.url(url).type(File.class).fileCache(true);
			callback.async(AQUtility.getContext());
		}
	}

}
