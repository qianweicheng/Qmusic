package com.qmusic.controls;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

@SuppressLint("NewApi")
public class BClipDrawable extends Drawable implements Drawable.Callback {
	private ClipState mClipState;
	private final Rect mTmpRect = new Rect();

	public static final int HORIZONTAL = 1;
	public static final int VERTICAL = 2;

	BClipDrawable() {
		this(null, null);
	}

	/**
	 * @param orientation
	 *            Bitwise-or of {@link #HORIZONTAL} and/or {@link #VERTICAL}
	 */
	public BClipDrawable(Drawable drawable, int gravity, int orientation) {
		this(null, null);

		mClipState.mDrawable = drawable;
		mClipState.mGravity = gravity;
		mClipState.mOrientation = orientation;

		if (drawable != null) {
			drawable.setCallback(this);
		}
	}

	// overrides from Drawable.Callback

	public void invalidateDrawable(Drawable who) {
		final Callback callback = getCallback();
		if (callback != null) {
			callback.invalidateDrawable(this);
		}
	}

	public void scheduleDrawable(Drawable who, Runnable what, long when) {
		final Callback callback = getCallback();
		if (callback != null) {
			callback.scheduleDrawable(this, what, when);
		}
	}

	public void unscheduleDrawable(Drawable who, Runnable what) {
		final Callback callback = getCallback();
		if (callback != null) {
			callback.unscheduleDrawable(this, what);
		}
	}

	// overrides from Drawable

	@Override
	public int getChangingConfigurations() {
		return super.getChangingConfigurations() | mClipState.mChangingConfigurations
				| mClipState.mDrawable.getChangingConfigurations();
	}

	@Override
	public boolean getPadding(Rect padding) {
		// XXX need to adjust padding!
		return mClipState.mDrawable.getPadding(padding);
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart) {
		mClipState.mDrawable.setVisible(visible, restart);
		return super.setVisible(visible, restart);
	}

	@Override
	public void setAlpha(int alpha) {
		mClipState.mDrawable.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mClipState.mDrawable.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return mClipState.mDrawable.getOpacity();
	}

	@Override
	public boolean isStateful() {
		return mClipState.mDrawable.isStateful();
	}

	@Override
	protected boolean onStateChange(int[] state) {
		return mClipState.mDrawable.setState(state);
	}

	@Override
	protected boolean onLevelChange(int level) {
		mClipState.mDrawable.setLevel(level);
		invalidateSelf();
		return true;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		mClipState.mDrawable.setBounds(bounds);
	}

	@Override
	public void draw(Canvas canvas) {

		if (mClipState.mDrawable.getLevel() == 0) {
			return;
		}

		final Rect r = mTmpRect;
		final Rect bounds = getBounds();
		int level = getLevel();
		int w = bounds.width();
		final int iw = 0; // mClipState.mDrawable.getIntrinsicWidth();
		if ((mClipState.mOrientation & HORIZONTAL) != 0) {
			w -= (w - iw) * (10000 - level) / 10000;
		}
		int h = bounds.height();
		final int ih = 0; // mClipState.mDrawable.getIntrinsicHeight();
		if ((mClipState.mOrientation & VERTICAL) != 0) {
			h -= (h - ih) * (10000 - level) / 10000;
		}
		Gravity.apply(mClipState.mGravity, w, h, bounds, r, mClipState.mOrientation);
		if (w > 0 && h > 0) {
			canvas.save();
			canvas.clipRect(r);
			mClipState.mDrawable.draw(canvas);
			canvas.restore();
		}
	}

	@Override
	public int getIntrinsicWidth() {
		return mClipState.mDrawable.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return mClipState.mDrawable.getIntrinsicHeight();
	}

	@Override
	public ConstantState getConstantState() {
		if (mClipState.canConstantState()) {
			mClipState.mChangingConfigurations = getChangingConfigurations();
			return mClipState;
		}
		return null;
	}

	final static class ClipState extends ConstantState {
		Drawable mDrawable;
		int mChangingConfigurations;
		int mOrientation;
		int mGravity;

		private boolean mCheckedConstantState;
		private boolean mCanConstantState;

		ClipState(ClipState orig, BClipDrawable owner, Resources res) {
			if (orig != null) {
				if (res != null) {
					mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
				} else {
					mDrawable = orig.mDrawable.getConstantState().newDrawable();
				}
				mDrawable.setCallback(owner);
				mOrientation = orig.mOrientation;
				mGravity = orig.mGravity;
				mCheckedConstantState = mCanConstantState = true;
			}
		}

		@Override
		public Drawable newDrawable() {
			return new BClipDrawable(this, null);
		}

		@Override
		public Drawable newDrawable(Resources res) {
			return new BClipDrawable(this, res);
		}

		@Override
		public int getChangingConfigurations() {
			return mChangingConfigurations;
		}

		boolean canConstantState() {
			if (!mCheckedConstantState) {
				mCanConstantState = mDrawable.getConstantState() != null;
				mCheckedConstantState = true;
			}

			return mCanConstantState;
		}
	}

	private BClipDrawable(ClipState state, Resources res) {
		mClipState = new ClipState(state, this, res);
	}
}
