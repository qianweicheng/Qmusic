package com.qmusic.controls.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

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
	public void onDetach() {
		super.onDetach();
		callback = null;
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
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
