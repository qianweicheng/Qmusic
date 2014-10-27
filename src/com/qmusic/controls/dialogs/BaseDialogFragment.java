package com.qmusic.controls.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.qmusic.uitls.BLog;

public abstract class BaseDialogFragment extends DialogFragment {
	String TAG = "BaseDialogFragment";
	IFragmentDialogCallback callback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (callback == null && activity instanceof IFragmentDialogCallback) {
			callback = (IFragmentDialogCallback) activity;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		BLog.d(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		BLog.d(TAG, "onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BLog.d(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callback = null;
		BLog.d(TAG, "onDetach");
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}

	@Override
	public void dismiss() {
		BLog.d(TAG, "dismiss");
		try {
			dismissAllowingStateLoss();
			// super.dismiss();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		BLog.d(TAG, "onDismiss");
		super.onDismiss(dialog);
		if (callback != null) {
			callback.dismiss();
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (callback != null) {
			callback.cancel();
		}
	}

	public void setCallback(IFragmentDialogCallback callback) {
		this.callback = callback;
	}

	public void show(FragmentManager manager) {
		show(manager, TAG);
	}
}
