package com.qmusic.controls.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qmusic.R;
import com.qmusic.uitls.BLog;

/**
 * 
 * @author weicheng
 * 
 */
public class BProgressDialog extends Dialog {
	static final String TAG = BProgressDialog.class.getSimpleName();
	String msg;
	private OnBackPressedListener onBackPressedListener = null;

	public BProgressDialog(Context context) {
		super(context, R.style.b_empty_dialog);
		init(context);
	}

	public BProgressDialog(Context context, String msg) {
		super(context, R.style.b_empty_dialog);
		this.msg = msg;
		init(context);
	}

	private void init(Context context) {
		// TODO:set style here
		// this.setIndeterminateDrawable(this.getContext().getResources().getDrawable(R.drawable.edo_loading));
		// this.setMessage(this.getContext().getString(R.string.loading));
		this.setCanceledOnTouchOutside(false);
		this.setContentView(R.layout.dialog_layout);
		TextView txtLoading = (TextView) this.findViewById(R.id.dialog_text);
		if (!TextUtils.isEmpty(msg)) {
			txtLoading.setText(msg);
			BLog.w(TAG, "Hiding message:" + msg);
		}
		txtLoading.setVisibility(View.GONE);
	}

	public interface OnBackPressedListener {
		public void onBackPressed(DialogInterface dialog);
	}

	public void setOnBackPressedListener(OnBackPressedListener listener) {
		onBackPressedListener = listener;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		BLog.d("BProgressDialog", "onBackPressed");
		if (onBackPressedListener != null) {
			onBackPressedListener.onBackPressed(this);
		}
	}

	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void dismiss() {
		try {
			super.dismiss();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
