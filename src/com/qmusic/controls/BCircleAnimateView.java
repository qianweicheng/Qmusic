package com.qmusic.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.qmusic.MyApplication;

public class BCircleAnimateView extends ImageView implements Runnable {
	int width, height;
	int speed = 2;
	long angle;

	public BCircleAnimateView(Context context) {
		super(context);
	}

	public BCircleAnimateView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		MyApplication.post(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		MyApplication.removePost(this);
	}

	@Override
	public void run() {
		invalidate();
		MyApplication.postDelayed(this, 10);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}

	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		int saveCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.rotate(angle, width / 2, width / 2);
		angle += speed;
		super.onDraw(canvas);
		canvas.restoreToCount(saveCount);
	}

	public void start() {
		MyApplication.post(this);
	}

	public void stop() {
		MyApplication.removePost(this);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
