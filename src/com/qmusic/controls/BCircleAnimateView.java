package com.qmusic.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.androidquery.util.AQUtility;

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
		AQUtility.post(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		AQUtility.removePost(this);
	}

	@Override
	public void run() {
		invalidate();
		AQUtility.postDelayed(this, 10);
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
		AQUtility.post(this);
	}

	public void stop() {
		AQUtility.removePost(this);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
