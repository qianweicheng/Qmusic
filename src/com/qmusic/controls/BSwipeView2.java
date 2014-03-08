package com.qmusic.controls;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class BSwipeView2 extends RelativeLayout {
	static final String TAG = BSwipeView2.class.getSimpleName();
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
	int finalDiffX;

	public BSwipeView2(Context context) {
		super(context);
		init(null);
	}

	public BSwipeView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public BSwipeView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		scroller = new Scroller(getContext(), new DecelerateInterpolator());
	}

	public interface OnSwipeCallback {
		void onSwipe(BSwipeView2 view, boolean toRight);

		void onMove(BSwipeView2 view, boolean toRight, float percent);

		void onClick(BSwipeView2 view);
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
				Log.d(TAG, "onInterceptTouchEvent:down");
				if (backView == null || frontView == null) {
					backView = this.getChildAt(0);
					frontView = this.getChildAt(1);
				}
				touchState = TOUCH_STATE_REST;
				lastMotionX = x;
				lastMotionY = y;
				return false;
			case MotionEvent.ACTION_CANCEL:
				Log.d(TAG, "onInterceptTouchEvent:cancel");
				touchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "onInterceptTouchEvent:up");
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
		float currentMotionY = ev.getY();
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
			touchState = TOUCH_STATE_REST;
			lastMotionX = currentMotionX;
			lastMotionY = currentMotionY;
			break;
		}
		case MotionEvent.ACTION_UP: {
			Log.d(TAG, "onTouchEvent:up");
			if (swiping) {
				swiping = false;
				scrollBack();
			} else if (callback != null) {
				if (!checkInMoving(currentMotionX, currentMotionY)) {
					Log.d(TAG, "clicked");
					callback.onClick(this);
				} else {
					Log.d(TAG, "has moved. abort click");
				}
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			Log.d(TAG, "onTouchEvent:cancel");
			if (swiping) {
				swiping = false;
				scrollBack();
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

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			// Log.i(TAG, "computeScroll:111");
			scrollTo(scroller.getCurrX(), 0);
			invalidate();
		} else {
			// Log.i(TAG, "computeScroll:222");
			if (!swiping && swipe) {
				swipe = false;
				onClose();
			}
		}
	}

	private boolean checkInMoving(float x, float y) {
		final int xDiff = (int) Math.abs(x - lastMotionX);
		final int yDiff = (int) Math.abs(y - lastMotionY);
		final int touchSlop = this.touchSlop;
		boolean xMoved = xDiff > touchSlop;
		if (xDiff > yDiff && xMoved) {
			return true;
		}
		return false;
	}

	private void scrollOpen(float diffX) {
		finalDiffX = (int) diffX;
		this.scrollTo(-finalDiffX, 0);
		invalidate();
	}

	private void scrollBack() {
		scroller.startScroll(-finalDiffX, 0, finalDiffX, 0, 300);
		invalidate();
	}

	private void onClose() {
		if (callback != null) {
			callback.onSwipe(BSwipeView2.this, toRight);
		}
	}
}