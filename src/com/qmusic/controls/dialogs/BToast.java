package com.qmusic.controls.dialogs;

import android.widget.Toast;

import com.androidquery.util.AQUtility;

public final class BToast {
	public static final void toast(String msg) {
		Toast.makeText(AQUtility.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static final void toast(int msg) {
		Toast.makeText(AQUtility.getContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
