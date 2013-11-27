package com.qmusic.controls.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ActionSheetDialogFragment extends BaseDialogFragment {
	String title;
	String[] options;
	int defaultSelected;

	public static final ActionSheetDialogFragment getInstance(final String title, final String[] options,
			final int defaultSelected) {
		ActionSheetDialogFragment fragment = new ActionSheetDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putStringArray("options", options);
		bundle.putInt("defaultSelected", defaultSelected);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = ActionSheetDialogFragment.class.getSimpleName();
		Bundle arguments;
		if (savedInstanceState != null) {
			arguments = savedInstanceState;
		} else {
			arguments = getArguments();
		}
		if (arguments != null) {
			title = arguments.getString("title");
			options = arguments.getStringArray("options");
			defaultSelected = arguments.getInt("defaultSelected");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putString("title", title);
		arg0.putStringArray("options", options);
		arg0.putInt("defaultSelected", defaultSelected);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(options, defaultSelected, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActionSheetDialogFragment.this.dismiss();
						if (callback != null) {
							callback.onClick(dialog, which);
						}
					}
				}).setNegativeButton(android.R.string.cancel, callback).create();
		return dialog;
	}
}
