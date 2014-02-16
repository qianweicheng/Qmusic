package com.qmusic.controls;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class BScrollView extends LinearLayout implements OnClickListener {
	static final String TAG = BScrollView.class.getSimpleName();
	Scroller mScroller;
	boolean state;

	public BScrollView(Context context) {
		super(context);
		init();
	}

	public BScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext(), new CycleInterpolator(3));
		setOnClickListener(this);
		setGravity(Gravity.CENTER);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			Log.e(TAG, "computeScroll1+" + mScroller.getCurrX());
			scrollTo(mScroller.getCurrX(), 0);
			postInvalidate();
		} else {
			Log.e(TAG, "computeScroll2");
			// scrollTo(0, 0);
			// state = true;
		}
	}

	@Override
	public void onClick(View v) {
		Log.e(TAG, "onClick");
		// mScroller.startScroll(0, 0, 150, 100, 1250);
		// scrollTo(150, 0);
		startAnimation(new MyAnicamtion());
	}

	public void startScroll() {
		Log.e(TAG, "startScroll");
		mScroller.startScroll(0, 0, 550, 100, 1250);
		state = true;
		invalidate();
	}

	public void reset() {
		Log.e(TAG, "reset");
		scrollTo(0, 0);
		state = false;
	}

	public boolean isOpen() {
		return state;
	}

	static class MyInterpolator implements Interpolator {
		private float mTension;

		public MyInterpolator(float tension) {
			mTension = tension;
		}

		public MyInterpolator(Context context, AttributeSet attrs) {

		}

		@Override
		public float getInterpolation(float input) {
			return input * mTension;
		}

	}

	static class MyAnicamtion extends Animation {
		Camera camera;
		int mCenterX, mCenterY;

		public MyAnicamtion() {
			setInterpolator(new MyInterpolator(1));
			setDuration(1000);
			camera = new Camera();
		}

		@Override
		public boolean getTransformation(long currentTime, Transformation outTransformation) {
			return super.getTransformation(currentTime, outTransformation);
		}

		@Override
		public void initialize(int width, int height, int parentWidth, int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			mCenterX = width / 2;

			mCenterY = height / 2;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			Log.d(TAG, "interpolatedTime:" + interpolatedTime);
			final Matrix matrix = t.getMatrix();
			camera.save();
			camera.translate(0.0f, 0.0f, (1300 - 1300.0f * interpolatedTime));
			camera.rotateY(360 * interpolatedTime);
			camera.getMatrix(matrix);
			matrix.preTranslate(-mCenterX, -mCenterY);
			matrix.postTranslate(mCenterX, mCenterY);
			camera.restore();
		}
	}
}