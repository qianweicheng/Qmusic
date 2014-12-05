package com.qmusic.localplugin.lockscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.qmusic.MyApplication;
import com.qmusic.R;
import com.qmusic.localplugin.LockScreenPlug;

public class AnimateScreenView extends LinearLayout implements Runnable {
	static final int DURATION = 4000;
	TextView txtTitle;
	ObjectAnimator animator;

	public AnimateScreenView(Context context) {
		super(context);
		init(context);
	}

	public AnimateScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		LayoutInflater.from(context).inflate(R.layout.activity_animation, this);
		txtTitle = (TextView) findViewById(R.id.activity_animation_title_txt);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 5f);
		PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0.0f);
		PropertyValuesHolder pvhY2 = PropertyValuesHolder.ofFloat("y", dm.heightPixels);
		animator = ObjectAnimator.ofPropertyValuesHolder(txtTitle, pvhY, pvhA, pvhY2);
		animator.setDuration(DURATION);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		MyApplication.postDelayed(this, DURATION);
		animator.start();
	}

	@Override
	public void run() {
		LockScreenPlug.stopAnimationS();
		animator.cancel();
	}

}