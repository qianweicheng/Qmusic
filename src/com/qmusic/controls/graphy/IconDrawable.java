package com.qmusic.controls.graphy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;

public class IconDrawable extends BitmapDrawable {
	static final String TAG = IconDrawable.class.getSimpleName();
	Paint paint;
	int rgb;
	PorterDuffXfermode xfermode;

	public IconDrawable(Resources resource, int resId, int rgb) {
		super(resource, BitmapFactory.decodeResource(resource, resId));
		init();
		this.rgb = rgb;
	}

	public IconDrawable(Resources resource, String filePath) {
		super(resource, filePath);
		init();
	}

	public IconDrawable(Resources resources, Bitmap bitmap) {
		super(resources, bitmap);
		init();
	}

	private void init() {
		xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		paint = new Paint();
	}

	public void setColor(int rgb) {
		this.rgb = rgb;
		invalidateSelf();
	}

	@Override
	public void draw(Canvas canvas) {
		int sc = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.MATRIX_SAVE_FLAG
				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
				| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		canvas.drawColor(rgb);
		paint.setXfermode(xfermode);
		canvas.drawBitmap(getBitmap(), 0, 0, paint);
		paint.setXfermode(null);
		canvas.restoreToCount(sc);
	}
}
