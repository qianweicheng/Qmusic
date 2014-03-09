package com.qmusic.controls.dialogs;

import android.app.Service;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.qmusic.R;

public final class BToast {
	public static final void toast(String msg) {
		Toast.makeText(AQUtility.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static final void toast(int msg) {
		Toast.makeText(AQUtility.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static final void toast(View view, String msg) {
		toast(view, msg, RelativeLayout.ALIGN_TOP);
	}

	/**
	 * 
	 * @param view
	 *            The view where the toast should be aligned
	 * @param msg
	 * @param alignType
	 *            RelativeLayout.ALIGN_TOP,RelativeLayout.ALIGN_BOTTOM
	 */
	public static final void toast(View view, String msg, int alignType) {
		Rect outRect = new Rect();
		view.getWindowVisibleDisplayFrame(outRect);
		int[] location = new int[2];
		view.getLocationInWindow(location);
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		Toast toast = Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT);
		if (alignType == RelativeLayout.ALIGN_TOP) {
			// TODO: how to get toast height
			DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
			toast.setGravity(Gravity.LEFT | Gravity.TOP, location[0], location[1] + rect.top - view.getHeight()
					- (int) (48 * displayMetrics.density));
		} else if (alignType == RelativeLayout.ALIGN_BOTTOM) {
			DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
			toast.setGravity(Gravity.LEFT | Gravity.TOP, location[0], location[1] + rect.top
					- (int) (10 * displayMetrics.density));
		} else {
			toast.setGravity(Gravity.LEFT | Gravity.TOP, 0, 0);
		}
		toast.show();
	}

	public static final void toastCustomerView(Context ctx) {
		Toast toast = new Toast(ctx);
		toast.setDuration(Toast.LENGTH_SHORT);
		LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		View mLayout = layoutInflater.inflate(R.layout.dialog_layout, null);
		toast.setView(mLayout);
		toast.show();
	}
}
