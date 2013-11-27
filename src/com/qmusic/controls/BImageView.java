package com.qmusic.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BImageView extends ImageView {
	ColorMatrixColorFilter filter;
	boolean blackWhiteEnable;

	// private Matrix matrix = new Matrix();

	public BImageView(Context context) {
		super(context);
		init();
	}

	public BImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		filter = new ColorMatrixColorFilter(cm);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = this.getDrawable();
		if (drawable == null)
			return;
		if (blackWhiteEnable) {
			drawable.setColorFilter(filter);
		}
		// int saveCount = canvas.save();
		// int width = getWidth();
		// int height = getHeight();
		// int w = drawable.getIntrinsicWidth();
		// int h = drawable.getIntrinsicHeight();
		// float ratio = (float) height / (float) width;
		// float r = (float) h / (float) w;
		// float scale;
		// if (ratio > r) {
		// // 底下空白
		// scale = height / (float) h;
		// float offset = (w * scale - width) / 2;
		// matrix.setScale(scale, scale);
		// matrix.postTranslate(-offset, 0);
		// } else {
		// scale = width / (float) w;
		// matrix.setScale(scale, scale);
		// }
		// canvas.concat(matrix);
		// drawable.draw(canvas);
		// canvas.restoreToCount(saveCount);
		super.onDraw(canvas);
	}

	public void blackWhiteEnable(boolean blackWhiteEnable) {
		this.blackWhiteEnable = blackWhiteEnable;
	}
}
