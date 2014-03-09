package com.qmusic.controls.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;

import com.qmusic.R;
import com.qmusic.uitls.BUtilities;

public class TipsDialogFragment extends DialogFragment {
	int[] location;
	String tips;
	int alignType;

	public static TipsDialogFragment showTips(View view, String tips) {
		return showTips(view, tips, RelativeLayout.ALIGN_TOP);
	}

	public static TipsDialogFragment showTips(View view, String tips, int alignType) {
		int[] location = new int[5];
		view.getLocationInWindow(location);
		location[2] = view.getWidth();
		location[3] = view.getHeight();
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		location[4] = rect.top;// status bar height
		TipsDialogFragment fragment = new TipsDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("tips", tips);
		bundle.putIntArray("location", location);
		bundle.putInt("alignType", alignType);
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
			location = arguments.getIntArray("location");
			tips = arguments.getString("tips");
			alignType = arguments.getInt("alignType");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putIntArray("location", location);
		bundle.putString("tips", tips);
		bundle.putInt("alignType", alignType);
	}

	@Override
	public void onStart() {
		super.onStart();
		Window window = getDialog().getWindow();
		LayoutParams params = window.getAttributes();
		if (RelativeLayout.ALIGN_RIGHT == alignType) {
			int[] screenSize = BUtilities.getScreenSize(getActivity());
			params.gravity = Gravity.RIGHT | Gravity.TOP;
			params.x = screenSize[0] - location[0] - location[2];
			params.y = location[1] + location[4];
		} else if (RelativeLayout.ALIGN_LEFT == alignType) {
			params.gravity = Gravity.LEFT | Gravity.TOP;
			params.x = location[0];
			params.y = location[1] + location[4];
		} else if (RelativeLayout.ALIGN_TOP == alignType) {
			params.gravity = Gravity.LEFT | Gravity.TOP;
			params.x = location[0];
			// TODO how to get the dialog height?
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			params.y = location[1] + location[4] - location[3] - (int) (54 * displayMetrics.density);
		}
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.alpha = 0.9f;
		// params.horizontalMargin = 0.1f;/*距离边界的百分比*/
		// params.horizontalWeight = 0;// 0.5f;
		params.windowAnimations = R.style.b_dialog_menu_animation_style;
		window.setAttributes(params);
	}

	public void show(FragmentManager manager) {
		super.show(manager, "tips");
	}

	@SuppressLint("NewApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			builder = new AlertDialog.Builder(getActivity(), R.style.b_dialog_menu);
		} else {
			builder = new AlertDialog.Builder(getActivity());
		}
		builder.setMessage(tips);
		Dialog dialog = builder.create();
		return dialog;
	}

	public void setAlignType(int alignType) {
		this.alignType = alignType;
	}
}
