package com.qmusic.controls.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmusic.R;

/**
 * 
 * @author weicheng this is used for fragments, please don't call its
 *         show/dismiss
 * 
 */
public class BProgressDialog extends Dialog {
	static final String TAG = BProgressDialog.class.getSimpleName();
	AnimationDrawable animation;
	TextView txtLoading;

	public BProgressDialog(Context context) {
		this(context, null);
	}

	public BProgressDialog(Context context, String msg) {
		super(context, R.style.b_empty_dialog);
		this.setCanceledOnTouchOutside(false);
		this.setContentView(R.layout.dialog_layout);
		txtLoading = (TextView) this.findViewById(R.id.dialog_text);
		if (!TextUtils.isEmpty(msg)) {
			txtLoading.setText(msg);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		txtLoading.setText(title);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Log.e(TAG, "onWindowFocusChanged");
		if (hasFocus) {
			ImageView icon = (ImageView) this.findViewById(R.id.dialog_image);
			animation = (AnimationDrawable) icon.getDrawable();
			animation.start();
		}
	}

	@Override
	public void onDetachedFromWindow() {
		Log.e(TAG, "onDetachedFromWindow");
		if (animation != null)
			animation.stop();
		super.onDetachedFromWindow();
	}

}
