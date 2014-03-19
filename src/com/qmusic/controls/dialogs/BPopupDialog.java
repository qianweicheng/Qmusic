package com.qmusic.controls.dialogs;

import android.app.Service;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.qmusic.R;

public class BPopupDialog extends PopupWindow implements OnTouchListener {
	public BPopupDialog(Context context) {
		super(context);
		init(context);
	}

	public BPopupDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BPopupDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public BPopupDialog(View contentView, int width, int height) {

	}

	public BPopupDialog(View contentView, int width, int height, boolean focusable) {

	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		setWindowLayoutMode(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setContentView(inflater.inflate(R.layout.activity_login, null));
		setAnimationStyle(R.style.b_dialog_menu_animation_style);
		setFocusable(false);
		setOutsideTouchable(false);
		// false,false, 外部可以点击，必须强制消除，没有外部点击事件
		// false,true 外部可以点击，有action =4
		// true,false 外部可以点击，点击外部会先消失，再响应外部 有action=0
		// true,true 外部可以点击，点击外部会先消失，再响应外部 有action=0
		setTouchInterceptor(this);
		ColorDrawable dw = new ColorDrawable(0x66000000);
		setBackgroundDrawable(dw);
		setTouchable(false);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i("BPopupDialog", "ACTION:" + event.getActionMasked());
		return false;
	}
}
