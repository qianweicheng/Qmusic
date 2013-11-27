package com.qmusic.controls.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import com.qmusic.controls.BProgressDialog;

public class LoadingDialogFragment extends BaseDialogFragment {
	String title, msg;

	public static LoadingDialogFragment getInstance(final String msg, boolean cancelable) {
		LoadingDialogFragment fragment = new LoadingDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		fragment.setArguments(bundle);
		fragment.setCancelable(cancelable);
		return fragment;
	}

	/**
	 * waiting dialog
	 * 
	 * @param title
	 * @param msg
	 * @return
	 */
	public static LoadingDialogFragment getInstance(final String title, final String msg) {
		LoadingDialogFragment fragment = new LoadingDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("msg", msg);
		fragment.setArguments(bundle);
		fragment.setCancelable(false);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = LoadingDialogFragment.class.getSimpleName();
		Bundle arguments;
		if (savedInstanceState != null) {
			arguments = savedInstanceState;
		} else {
			arguments = getArguments();
		}
		if (arguments != null) {
			title = arguments.getString("title");
			msg = arguments.getString("msg");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putString("title", title);
		arg0.putString("msg", msg);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog;
		if (TextUtils.isEmpty(title)) {
			dialog = new BProgressDialog(getActivity(), msg);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(title);
			builder.setMessage(msg);
			// builder.setIcon(android.R.drawable.ic_dialog_info);
			dialog = builder.create();
		}
		return dialog;
	}
}
