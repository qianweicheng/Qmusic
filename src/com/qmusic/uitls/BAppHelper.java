package com.qmusic.uitls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.qmusic.R;
import com.qmusic.activities.SplashActivity;
import com.qmusic.common.BConstants;

public class BAppHelper {
	static final String TAG = BAppHelper.class.getSimpleName();
	static int exiting;

	public final static boolean exit(Activity activity, boolean force) {
		if (exiting == 1 || force) {
			Intent intent = new Intent(activity, SplashActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra(SplashActivity.SHUTDOWN, true);
			activity.startActivity(intent);
			activity.overridePendingTransition(0, 0);
			return true;
		} else if (exiting == 0) {
			exiting++;
			Toast.makeText(activity, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();
			Handler handle = new Handler();
			handle.postDelayed(new Runnable() {
				@Override
				public void run() {
					exiting = 0;
				}
			}, 3000);
			return false;
		}
		return false;
	}

	public static final void setTitle(SherlockFragmentActivity activity, int layoutResID, String title, boolean showBack) {
		activity.setContentView(layoutResID);
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(showBack);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setBackgroundDrawable(AQUtility.getContext().getResources().getDrawable(R.drawable.edo_navbar));
		if (TextUtils.isEmpty(title)) {
			actionBar.setTitle("  ");
		} else {
			// add padding between back button and title
			actionBar.setTitle(" " + title);
		}
	}

	public static final void setTitle(final Activity activity, int layoutResID, String title) {
		setTitle(activity, layoutResID, title, R.drawable.icon, new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		}, 0, null);
	}

	public static final void setTitle(final Activity activity, int layoutResID, String title, int leftResId,
			OnClickListener leftButtonCallback, int rightResId, OnClickListener rightButtonCallback) {
		activity.setContentView(layoutResID);
		ImageButton btnLeft = (ImageButton) activity.findViewById(R.id.common_title_left_icon);
		ImageButton btnRight = (ImageButton) activity.findViewById(R.id.common_title_right_icon);
		TextView txtTitle = (TextView) activity.findViewById(R.id.common_title_title);
		txtTitle.setText(title);
		if (leftResId == 0 || leftButtonCallback == null) {
			btnLeft.setVisibility(View.INVISIBLE);
		} else {
			btnLeft.setImageResource(leftResId);
			btnLeft.setOnClickListener(leftButtonCallback);
		}
		if (rightResId == 0 || rightButtonCallback == null) {
			btnRight.setVisibility(View.INVISIBLE);
		} else {
			btnRight.setImageResource(rightResId);
			btnRight.setOnClickListener(rightButtonCallback);
		}
	}

	public final static int init(Context ctx) {
		String countStr = BUtilities.getPref(BConstants.PRE_KEY_RUN_COUNT);
		int count;
		if (TextUtils.isEmpty(countStr)) {
			count = 0;
		} else {
			count = Integer.parseInt(countStr);
		}
		BUtilities.setPref(BConstants.PRE_KEY_RUN_COUNT, "" + (++count));
		String log = String.format("App Opened:%s time(s)", count);
		BLog.i(TAG, log);

		DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		// Note:w and h may be exchanged for each other
		String result = String.format("w=%s,h=%s,dip=%s", displayMetrics.widthPixels, displayMetrics.heightPixels,
				displayMetrics.density);
		BLog.i(TAG, result);
		return count;
	}
}
