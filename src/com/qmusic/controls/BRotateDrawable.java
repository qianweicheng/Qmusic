/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qmusic.controls;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class BRotateDrawable extends Drawable implements Drawable.Callback, Animatable, Runnable {
	private static final float MAX_LEVEL = 10000.0f;

	private RotateState mState;
	private boolean mMutated;

	/**
	 * <p>
	 * Create a new rotating drawable with an empty state.
	 * </p>
	 */
	public BRotateDrawable() {
		this(null, null);
	}

	/**
	 * <p>
	 * Create a new rotating drawable with the specified state. A copy of this
	 * state is used as the internal state for the newly created drawable.
	 * </p>
	 * 
	 * @param rotateState
	 *            the state for this drawable
	 */
	private BRotateDrawable(RotateState rotateState, Resources res) {
		mState = new RotateState(rotateState, this, res);
		init();
	}

	public void draw(Canvas canvas) {
		int saveCount = canvas.save();

		Rect bounds = mState.mDrawable.getBounds();

		int w = bounds.right - bounds.left;
		int h = bounds.bottom - bounds.top;

		final RotateState st = mState;

		float px = st.mPivotXRel ? (w * st.mPivotX) : st.mPivotX;
		float py = st.mPivotYRel ? (h * st.mPivotY) : st.mPivotY;

		canvas.rotate(st.mCurrentDegrees, px + bounds.left, py + bounds.top);

		st.mDrawable.draw(canvas);

		canvas.restoreToCount(saveCount);
	}

	/**
	 * Returns the drawable rotated by this RotateDrawable.
	 */
	public Drawable getDrawable() {
		return mState.mDrawable;
	}

	@Override
	public int getChangingConfigurations() {
		return super.getChangingConfigurations() | mState.mChangingConfigurations
				| mState.mDrawable.getChangingConfigurations();
	}

	public void setAlpha(int alpha) {
		mState.mDrawable.setAlpha(alpha);
	}

	public void setColorFilter(ColorFilter cf) {
		mState.mDrawable.setColorFilter(cf);
	}

	public int getOpacity() {
		return mState.mDrawable.getOpacity();
	}

	@SuppressLint("NewApi")
	public void invalidateDrawable(Drawable who) {
		final Callback callback = getCallback();
		if (callback != null) {
			callback.invalidateDrawable(this);
		}
	}

	@SuppressLint("NewApi")
	public void scheduleDrawable(Drawable who, Runnable what, long when) {
		final Callback callback = getCallback();
		if (callback != null) {
			callback.scheduleDrawable(this, what, when);
		}
	}

	@SuppressLint("NewApi")
	public void unscheduleDrawable(Drawable who, Runnable what) {
		final Callback callback = getCallback();
		if (callback != null) {
			callback.unscheduleDrawable(this, what);
		}
	}

	@Override
	public boolean getPadding(Rect padding) {
		return mState.mDrawable.getPadding(padding);
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart) {
		mState.mDrawable.setVisible(visible, restart);
		return super.setVisible(visible, restart);
	}

	@Override
	public boolean isStateful() {
		return mState.mDrawable.isStateful();
	}

	@Override
	protected boolean onStateChange(int[] state) {
		boolean changed = mState.mDrawable.setState(state);
		onBoundsChange(getBounds());
		return changed;
	}

	@Override
	protected boolean onLevelChange(int level) {
		mState.mDrawable.setLevel(level);
		onBoundsChange(getBounds());

		mState.mCurrentDegrees = mState.mFromDegrees + (mState.mToDegrees - mState.mFromDegrees)
				* ((float) level / MAX_LEVEL);

		invalidateSelf();
		return true;
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		mState.mDrawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}

	@Override
	public int getIntrinsicWidth() {
		return mState.mDrawable.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return mState.mDrawable.getIntrinsicHeight();
	}

	@Override
	public ConstantState getConstantState() {
		if (mState.canConstantState()) {
			mState.mChangingConfigurations = getChangingConfigurations();
			return mState;
		}
		return null;
	}

	private void init() {
		// mState.mDrawable = drawable;
		mState.mPivotXRel = true;
		mState.mPivotX = 0.5f;
		mState.mPivotYRel = true;
		mState.mPivotY = 0.5f;
		mState.mFromDegrees = mState.mCurrentDegrees = 0;
		mState.mToDegrees = 360;

	}

	public void setDrawable(Drawable drawable) {
		mState.mDrawable = drawable;
		if (drawable != null) {
			drawable.setCallback(this);
		}
	}

	@Override
	public Drawable mutate() {
		if (!mMutated && super.mutate() == this) {
			mState.mDrawable.mutate();
			mMutated = true;
		}
		return this;
	}

	/**
	 * <p>
	 * Represents the state of a rotation for a given drawable. The same rotate
	 * drawable can be invoked with different states to drive several rotations
	 * at the same time.
	 * </p>
	 */
	final static class RotateState extends Drawable.ConstantState {
		Drawable mDrawable;

		int mChangingConfigurations;

		boolean mPivotXRel;
		float mPivotX;
		boolean mPivotYRel;
		float mPivotY;

		float mFromDegrees;
		float mToDegrees;

		float mCurrentDegrees;

		private boolean mCanConstantState;
		private boolean mCheckedConstantState;

		public RotateState(RotateState source, BRotateDrawable owner, Resources res) {
			if (source != null) {
				if (res != null) {
					mDrawable = source.mDrawable.getConstantState().newDrawable(res);
				} else {
					mDrawable = source.mDrawable.getConstantState().newDrawable();
				}
				mDrawable.setCallback(owner);
				mPivotXRel = source.mPivotXRel;
				mPivotX = source.mPivotX;
				mPivotYRel = source.mPivotYRel;
				mPivotY = source.mPivotY;
				mFromDegrees = mCurrentDegrees = source.mFromDegrees;
				mToDegrees = source.mToDegrees;
				mCanConstantState = mCheckedConstantState = true;
			}
		}

		@Override
		public Drawable newDrawable() {
			return new BRotateDrawable(this, null);
		}

		@Override
		public Drawable newDrawable(Resources res) {
			return new BRotateDrawable(this, res);
		}

		@Override
		public int getChangingConfigurations() {
			return mChangingConfigurations;
		}

		public boolean canConstantState() {
			if (!mCheckedConstantState) {
				mCanConstantState = mDrawable.getConstantState() != null;
				mCheckedConstantState = true;
			}

			return mCanConstantState;
		}
	}

	boolean isRunning;

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void start() {
		if (!isRunning()) {
			run();
		}
	}

	@Override
	public void stop() {
		if (isRunning()) {
			unscheduleSelf(this);
		}
	}

	int level = 0;

	@Override
	public void run() {
		level += 10;
		setLevel(level);
		scheduleSelf(this, SystemClock.uptimeMillis() + 100);
	}
}
