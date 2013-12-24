package com.qmusic.controls.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.qmusic.R;

public class InputDialogFragment extends BaseDialogFragment {
	String title, defaultStr, hint;
	int inputType;

	/**
	 * EditText editInput = (EditText) ((AlertDialog) arg0)
	 * .findViewById(R.id.dialog_settings_input_edit); String editInputString =
	 * editInput.getEditableText().toString().trim();
	 * 
	 * @param title
	 * @param defaultStr
	 * @param hint
	 * @param inputType
	 * @param callback
	 * @return
	 */
	public static InputDialogFragment getInstance(final String title, final String defaultStr, final String hint,
			final int inputType) {
		InputDialogFragment fragment = new InputDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("defaultStr", defaultStr);
		bundle.putString("hint", hint);
		bundle.putInt("inputType", inputType);
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
			defaultStr = arguments.getString("defaultStr");
			hint = arguments.getString("hint");
			inputType = arguments.getInt("inputType");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putString("title", title);
		arg0.putString("defaultStr", defaultStr);
		arg0.putString("hint", hint);
		arg0.putInt("inputType", inputType);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View textEntryView = factory.inflate(R.layout.dialog_settings, null);
		EditText editInput = (EditText) textEntryView.findViewById(R.id.dialog_settings_input_edit);
		if (!TextUtils.isEmpty(defaultStr)) {
			editInput.setText(defaultStr);
		}
		editInput.setHint(hint);
		editInput.setInputType(inputType);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		builder.setView(textEntryView);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, callback);
		Dialog dialog = builder.create();
		return dialog;
	}
}
