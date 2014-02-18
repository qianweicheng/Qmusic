package com.qmusic.test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;

import com.qmusic.R;

public class BDrawable extends Drawable implements Drawable.Callback, Runnable {
	static final String TAG = BDrawable.class.getSimpleName();
	static final int[] ICONS = new int[] { R.drawable.tab1, R.drawable.tab2, R.drawable.tab3 };
	BitmapDrawable[] drawables;
	int currentIndex;

	public BDrawable(Context ctx) {
		Resources resource = ctx.getResources();
		drawables = new BitmapDrawable[ICONS.length];
		for (int i = 0; i < ICONS.length; i++) {
			drawables[i] = new BitmapDrawable(resource, BitmapFactory.decodeResource(resource, ICONS[i]));
		}
	}

	@Override
	public void draw(Canvas canvas) {
		Log.i(TAG, "draw");
		int saveCount = canvas.save();
		drawables[currentIndex % drawables.length].draw(canvas);
		canvas.restoreToCount(saveCount);
	}

	@Override
	public int getOpacity() {
		return 255;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public void invalidateDrawable(Drawable who) {
		Log.i(TAG, "invalidateDrawable");

	}

	@Override
	public void scheduleDrawable(Drawable who, Runnable what, long when) {
		Log.i(TAG, "scheduleDrawable");
		scheduleSelf(this, 1000);
	}

	@Override
	public void unscheduleDrawable(Drawable who, Runnable what) {
		unscheduleSelf(this);
	}

	@Override
	public void run() {
		Log.i(TAG, "run");
		currentIndex++;
		invalidateSelf();
		unscheduleSelf(this);
		scheduleSelf(this, SystemClock.uptimeMillis() + 1000);
	}
}
