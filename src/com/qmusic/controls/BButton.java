package com.qmusic.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.qmusic.MyApplication;
import com.qmusic.R;

public class BButton extends Button implements OnClickListener, Runnable {
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
		setBackgroundResource(R.drawable.b_button);
	}

	public void setColdTime(int coldTime) {
		this.coldTime = coldTime;
	}

	@Override
	public void onClick(View v) {
		if (cold) {
			return;
		}
		cold = true;
		MyApplication.postDelayed(this, coldTime);
		performClick();
	}

	@Override
	public void run() {
		cold = false;
	}
}
