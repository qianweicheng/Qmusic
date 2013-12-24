package com.qmusic.controls.dialogs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.qmusic.R;
import com.qmusic.common.BConstants;
import com.qmusic.common.IAsyncDataCallback;

public class BAccountSelectionFragment extends BaseDialogFragment {
	Account[] accounts;
	String[] accountsList;
	IAsyncDataCallback dataCallback;

	public static final BAccountSelectionFragment getInstance() {
		BAccountSelectionFragment fragment = new BAccountSelectionFragment();
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (dataCallback == null && activity instanceof IAsyncDataCallback) {
			dataCallback = (IAsyncDataCallback) activity;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		dataCallback = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Activity act = getActivity();
			AccountManager accountManager = AccountManager.get(act);
			accounts = accountManager.getAccounts();
			accountsList = new String[accounts.length + 1];
			accountsList[0] = act.getString(R.string.local);
			for (int i = 0; i < accounts.length; i++) {
				accountsList[i + 1] = accounts[i].name;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = null;
		if (accountsList != null) {
			dialog = new AlertDialog.Builder(getActivity()).setTitle("Select Account")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dataCallback != null) {
								dataCallback.callback(BConstants.OP_RESULT_CANCELED, null);
							}
						}
					}).setSingleChoiceItems(accountsList, -1, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dataCallback != null) {
								if (which == 0) {
									dataCallback.callback(BConstants.OP_RESULT_OK, null);
								} else {
									dataCallback.callback(BConstants.OP_RESULT_OK, accounts[which - 1]);
								}
							}
							dismiss();
						}
					}).create();
		}
		return dialog;
	}

	public void setCallback(IAsyncDataCallback dataCallback) {
		this.dataCallback = dataCallback;
	}
}
