package com.qmusic.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class BSwipeView extends FrameLayout {
	static final String TAG = BSwipeView.class.getSimpleName();
	public final static int SWIPE_MODE_NONE = 0;
	public final static int SWIPE_MODE_BOTH = 1;
	public final static int SWIPE_MODE_RIGHT = 2;
	public final static int SWIPE_MODE_LEFT = 3;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING_X = 1;
	int swipeMode = SWIPE_MODE_BOTH;
	int touchSlop;
	float lastMotionX, lastMotionY;
	boolean swipe;// need to swipe for this swipe
	boolean swiping;// swipe state;
	boolean toRight;
	int halfWidth;
	OnSwipeCallback callback;
	Scroller scroller;
	int touchState = TOUCH_STATE_REST;
	View frontView, backView;
	AnimatorListenerAdapter animatorListenerAdapterClose;
	float finalDiffX;

	public BSwipeView(Context context) {
		super(context);
		init(null);
	}

	public BSwipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public BSwipeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	@SuppressLint("NewApi")
	private void init(AttributeSet attrs) {
		animatorListenerAdapterClose = new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				onClose();
				invalidate();
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public interface OnSwipeCallback {
		void onSwipe(BSwipeView view, boolean toRight);

		void onMove(BSwipeView view, boolean toRight, float percent);
	}

	public void setCallback(OnSwipeCallback callback) {
		this.callback = callback;
	}

	public void setSwipeMode(int mode) {
		swipeMode = mode;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);
		final float x = ev.getX();
		final float y = ev.getY();

		if (isEnabled() && swipeMode != SWIPE_MODE_NONE) {
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				// Log.d(TAG, "onInterceptTouchEvent:move");
				swiping = checkInMoving(x, y);
				if (swiping) {
					touchState = TOUCH_STATE_SCROLLING_X;
					return true;
				} else {
					return false;
				}
			case MotionEvent.ACTION_DOWN:
				// Log.d(TAG, "onInterceptTouchEvent:down");
				if (backView == null || frontView == null) {
					backView = this.getChildAt(0);
					frontView = this.getChildAt(1);
				}
				backView.setVisibility(View.VISIBLE);
				touchState = TOUCH_STATE_REST;
				lastMotionX = x;
				lastMotionY = y;
				return false;
			case MotionEvent.ACTION_CANCEL:
				// Log.d(TAG, "onInterceptTouchEvent:cancel");
				touchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_UP:
				// Log.d(TAG, "onInterceptTouchEvent:up");
				return touchState == TOUCH_STATE_SCROLLING_X;
			default:
				// Log.d(TAG, "onInterceptTouchEvent:default");
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);
		float currentMotionX = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			// Log.d(TAG, "onTouchEvent:move");
			float diffX = currentMotionX - lastMotionX;
			toRight = diffX > 0;
			if ((toRight && (swipeMode == SWIPE_MODE_RIGHT || swipeMode == SWIPE_MODE_BOTH))
					|| (!toRight && (swipeMode == SWIPE_MODE_LEFT || swipeMode == SWIPE_MODE_BOTH))) {

				swipe = Math.abs(diffX) > halfWidth;
				float percent = Math.abs(diffX) / halfWidth;
				if (callback != null) {
					callback.onMove(this, toRight, percent);
				}
				swiping = true;
				scrollOpen(diffX * 0.5f);
			}
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			Log.d(TAG, "onTouchEvent:down");
			backView.setVisibility(View.VISIBLE);
			break;
		}
		case MotionEvent.ACTION_UP: {
			// Log.d(TAG, "onTouchEvent:up");
			if (swiping) {
				scrollBack();
				swiping = false;
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			// Log.d(TAG, "onTouchEvent:cancel");
			if (swiping) {
				scrollBack();
				swiping = false;
			}
			break;
		}
		default: {
			// Log.d(TAG, "onTouchEvent:default");
			break;
		}
		}
		invalidate();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		halfWidth = getMeasuredWidth() / 2;
		// int height = getMeasuredHeight();
		// Log.e(TAG, "onMeasure:w/2:" + halfWidth + ";h:" + height);
	}

	private boolean checkInMoving(float x, float y) {
		final int xDiff = (int) Math.abs(x - lastMotionX);
		final int yDiff = (int) Math.abs(y - lastMotionY);
		final int touchSlop = this.touchSlop;
		boolean xMoved = xDiff > touchSlop;
		if (xDiff > yDiff && xMoved) {
			lastMotionX = x;
			lastMotionY = y;
			return true;
		}
		return false;
	}

	private void scrollOpen(float diffX) {
		finalDiffX = diffX;
		ViewHelper.setTranslationX(frontView, diffX);
	}

	private void scrollBack() {
		ViewPropertyAnimator.animate(frontView).translationX(0).setDuration(300)
				.setListener(animatorListenerAdapterClose).start();
	}

	private void onClose() {
		if (swipe) {
			if (callback != null) {
				callback.onSwipe(BSwipeView.this, toRight);
			}
			swipe = false;
		}
		backView.setVisibility(View.GONE);
	}
}