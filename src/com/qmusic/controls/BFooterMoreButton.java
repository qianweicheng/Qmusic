package com.qmusic.controls;

import com.qmusic.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

public class BFooterMoreButton extends Button {
	String enableText;
	String loadingText;

	public BFooterMoreButton(Context context) {
		super(context);
		init();
	}

	public BFooterMoreButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		setGravity(Gravity.CENTER);
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
		setBackgroundResource(R.drawable.b_button);
		enableText = "点击获取更多";
		loadingText = "正在加载中...";
		setText(enableText);
	}

	public void setText(String footer) {
		super.setText(footer);
	}

	public void startLoading() {
		setClickable(false);
		// setCompoundDrawables(getResources().getDrawable(R.drawable.b_loading),
		// null, null, null);
		setText(loadingText);
	}

	public void stopLoading() {
		setClickable(true);
		// setCompoundDrawables(null, null, null, null);
		setText(enableText);
	}
}
