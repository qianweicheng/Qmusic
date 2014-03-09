package com.qmusic.controls.dialogs;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;

import com.qmusic.R;
import com.qmusic.uitls.BUtilities;

public class MenuDialogFragment extends BaseDialogFragment {
	public static interface GetMenuView {
		View getMenuView();
	}

	int[] location;
	GetMenuView callback;
	int alignType;

	public static final MenuDialogFragment getInstance(View view) {
		return getInstance(view, RelativeLayout.ALIGN_LEFT);
	}

	/**
	 * 
	 * @param view
	 * @param alignType
	 *            RelativeLayout.ALIGN_LEFT,RelativeLayout.ALIGN_RIGHT
	 * @return
	 */
	public static final MenuDialogFragment getInstance(View view, int alignType) {
		int[] location = new int[5];
		view.getLocationInWindow(location);
		location[2] = view.getWidth();
		location[3] = view.getHeight();
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		location[4] = rect.top;// status bar height
		MenuDialogFragment fragment = new MenuDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putIntArray("location", location);
		bundle.putInt("alignType", alignType);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof GetMenuView) {
			callback = (GetMenuView) activity;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callback = null;

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
			alignType = arguments.getInt("alignType");
		}
		int style = DialogFragment.STYLE_NO_TITLE, theme = R.style.b_dialog_menu;
		setStyle(style, theme);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putIntArray("location", location);
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
		} else {
			params.gravity = Gravity.LEFT | Gravity.TOP;
			params.x = location[0];
		}
		params.y = location[1] + location[4];
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.alpha = 0.9f;
		// params.horizontalMargin = 0.1f;/*距离边界的百分比*/
		// params.horizontalWeight = 0;// 0.5f;
		params.windowAnimations = R.style.b_dialog_menu_animation_style;
		window.setAttributes(params);
	}

	@Override
	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
		Log.i(TAG, "onInflate");
		super.onInflate(activity, attrs, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		if (callback != null) {
			view = callback.getMenuView();
		} else {
			view = super.onCreateView(inflater, container, savedInstanceState);
		}
		return view;
	}

	// @SuppressLint("NewApi")
	// @Override
	// public Dialog onCreateDialog(Bundle savedInstanceState) {
	// Log.i(TAG, "onCreateDialog");
	// AlertDialog.Builder builder;
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	// builder = new AlertDialog.Builder(getActivity(), R.style.b_dialog_menu);
	// } else {
	// builder = new AlertDialog.Builder(getActivity());
	// }
	//
	// if (callback != null) {
	// View view = callback.getMenuView();
	// builder.setView(view);
	// } else {
	// builder.setMessage("view is null from GetMenuView");
	// }
	//
	// // builder.setMessage("Message");
	// builder.setTitle("Title");
	// Dialog dialog = builder.create();
	// return dialog;
	// }

	public void show(FragmentManager manager) {
		super.show(manager, "menu_dialog");
	}

	public void setCallback(GetMenuView callback) {
		this.callback = callback;
	}
}
