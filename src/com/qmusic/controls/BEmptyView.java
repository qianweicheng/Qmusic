package com.qmusic.controls;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class BEmptyView extends TextView {

	public BEmptyView(Context context) {
		super(context);
		init();
	}

	public BEmptyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		setGravity(Gravity.CENTER);
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Medium);
		if (TextUtils.isEmpty(getText())) {
			setText("暂无数据");
		}
	}
}
