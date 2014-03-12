package com.qmusic.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;

import com.qmusic.R;

@SuppressLint("NewApi")
public class BDrawable extends Drawable implements Drawable.Callback, Runnable, Animatable {
	static final String TAG = BDrawable.class.getSimpleName();
	static final int[] ICONS = new int[] { R.drawable.tab1, R.drawable.tab2, R.drawable.tab3 };
	Drawable[] drawables;
	int currentIndex;
	boolean isRunning;

	public BDrawable(Context ctx) {
		Resources resource = ctx.getResources();
		drawables = new Drawable[ICONS.length];
		for (int i = 0; i < ICONS.length; i++) {
			drawables[i] = new BitmapDrawable(resource, BitmapFactory.decodeResource(resource, ICONS[i]));
		}
	}

	@Override
	public void draw(Canvas canvas) {
		Log.i(TAG, "draw");
		canvas.save();
		Drawable d = drawables[currentIndex % drawables.length];
		d.draw(canvas);
		canvas.restore();
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		for (int i = 0; i < drawables.length; i++) {
			drawables[i].setBounds(bounds);
		}
	}

	@Override
	public void invalidateDrawable(Drawable who) {
		Log.i(TAG, "invalidateDrawable");
		Callback c = getCallback();
		if (c != null) {
			c.invalidateDrawable(who);
		}
	}

	@Override
	public void scheduleDrawable(Drawable who, Runnable what, long when) {
		Log.i(TAG, "scheduleDrawable");
		Callback c = getCallback();
		if (c != null) {
			c.scheduleDrawable(who, what, when);
		}
	}

	@Override
	public void unscheduleDrawable(Drawable who, Runnable what) {
		Log.i(TAG, "scheduleDrawable");
		Callback c = getCallback();
		if (c != null) {
			c.unscheduleDrawable(who, what);
		}
	}

	@Override
	public void run() {
		currentIndex++;
		scheduleSelf(this, SystemClock.uptimeMillis() + 1000);
		invalidateSelf();
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void start() {
		if (!isRunning()) {
			isRunning = true;
			run();
		}
	}

	@Override
	public void stop() {
		if (isRunning()) {
			isRunning = false;
			unscheduleSelf(this);
		}
	}
}
