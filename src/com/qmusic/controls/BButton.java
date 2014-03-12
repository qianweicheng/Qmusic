package com.qmusic.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.androidquery.util.AQUtility;

public class BButton extends Button implements OnClickListener, Runnable {
	OnClickListener onClickListener;
	int coldTime = 1000;
	boolean cold;

	public BButton(Context context) {
		super(context);
		init();
	}

	public BButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		setOnClickListener(this);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.onClickListener = l;
	}

	public void setColdTime(int coldTime) {
		this.coldTime = coldTime;
	}

	@Override
	public void onClick(View v) {
		if (cold) {
			return;
		} else if (onClickListener != null) {
			cold = true;
			onClickListener.onClick(v);
			AQUtility.postDelayed(this, coldTime);
		}
	}

	@Override
	public void run() {
		cold = false;
	}
}
