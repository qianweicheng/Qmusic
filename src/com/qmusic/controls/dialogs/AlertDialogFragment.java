package com.qmusic.controls.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.androidquery.util.AQUtility;
import com.qmusic.R;
import com.qmusic.uitls.BLog;

public class AlertDialogFragment extends BaseDialogFragment {
	String title, msg, positiveStr, negativeStr, neutralStr;
	int icon = android.R.drawable.ic_dialog_info;

	/**
	 * Has only one button
	 * 
	 * @param title
	 * @return
	 */
	public static AlertDialogFragment getInstance(final String title, final String msg, final int positive) {
		return getInstance(title, msg, AQUtility.getContext().getString(positive));
	}

	/**
	 * Has only one button
	 * 
	 * @param title
	 * @return
	 */
	public static AlertDialogFragment getInstance(final String title, final String msg, final String positiveStr) {
		AlertDialogFragment fragment = new AlertDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("msg", msg);
		bundle.putString("positiveStr", positiveStr);
		fragment.setArguments(bundle);
		return fragment;
	}

	public static AlertDialogFragment getInstance(final String title, final String msg, final int positive,
			final int nagative) {
		Context ctx = AQUtility.getContext();
		return getInstance(title, msg, ctx.getString(positive), ctx.getString(nagative));
	}

	/**
	 * Has two buttons
	 * 
	 * @param title
	 * @return
	 */
	public static AlertDialogFragment getInstance(final String title, final String msg, final String positiveStr,
			final String nagativeStr) {
		AlertDialogFragment fragment = new AlertDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("msg", msg);
		bundle.putString("positiveStr", positiveStr);
		bundle.putString("negativeStr", nagativeStr);
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * Has three buttons
	 * 
	 * @param title
	 * @param msg
	 * @param positiveStr
	 * @param nagativeStr
	 * @param neutralStr
	 * @return
	 */
	public static AlertDialogFragment getInstance(final String title, final String msg, final String positiveStr,
			final String nagativeStr, final String neutralStr) {
		AlertDialogFragment fragment = new AlertDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("msg", msg);
		bundle.putString("positiveStr", positiveStr);
		bundle.putString("negativeStr", nagativeStr);
		bundle.putString("neutralStr", neutralStr);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments;
		if (savedInstanceState != null) {
			arguments = savedInstanceState;
		} else {
			arguments = getArguments();
		}
		if (arguments != null) {
			title = arguments.getString("title");
			msg = arguments.getString("msg");
			positiveStr = arguments.getString("positiveStr");
			negativeStr = arguments.getString("negativeStr");
			neutralStr = arguments.getString("neutralStr");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putString("title", title);
		arg0.putString("msg", msg);
		arg0.putString("positiveStr", positiveStr);
		arg0.putString("negativeStr", negativeStr);
		arg0.putString("neutralStr", neutralStr);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (title != null && title.length() > 60) {
			builder.setTitle(getActivity().getString(R.string.app_name));
			builder.setMessage(title);
			BLog.i(TAG, "Ignore msg:" + msg);
		} else {
			builder.setTitle(title);
			builder.setMessage(msg);
		}
		builder.setIcon(icon);
		if (TextUtils.isEmpty(positiveStr)) {
			builder.setPositiveButton(android.R.string.ok, callback);
		} else {
			builder.setPositiveButton(positiveStr, callback);
		}
		if (!TextUtils.isEmpty(negativeStr)) {
			builder.setNegativeButton(negativeStr, callback);
		}
		if (!TextUtils.isEmpty(neutralStr)) {
			builder.setNeutralButton(neutralStr, callback);
		}
		builder.setCancelable(false);
		Dialog dialog = builder.create();
		return dialog;
	}
}
