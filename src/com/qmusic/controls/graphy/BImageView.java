package com.qmusic.controls.graphy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.qmusic.R;
import com.qmusic.uitls.BLog;

/**
 * 圆角与黑白效果
 * 
 * @author weicheng
 * 
 */
public class BImageView extends ImageView {
	static final String TAG = BImageView.class.getSimpleName();
	ColorMatrixColorFilter filter;
	Paint mMaskPaint;
	Path mMaskPath;
	float mCornerRadius = 20f;
	boolean mBlackWhite = false;

	public BImageView(Context context) {
		super(context);
		init(null);
	}

	public BImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public BImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public void setBlackWhite(boolean blackWhite) {
		this.mBlackWhite = blackWhite;
		this.invalidate();
	}

	public boolean getBlackWhite() {
		return this.mBlackWhite;
	}

	public float getRadius() {
		return this.mCornerRadius;
	}

	public void setRadius(float radius) {
		if (radius > 0) {
			this.mCornerRadius = radius;
			generateMaskPath(this.getWidth(), this.getHeight());
		} else {
			BLog.e(TAG, "radius must greater than zero");
		}
	}

	@SuppressLint("NewApi")
	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImage);
			mCornerRadius = styledAttrs.getDimension(R.styleable.RoundImage_roundradius, mCornerRadius);
			mBlackWhite = styledAttrs.getBoolean(R.styleable.RoundImage_blackwhite, mBlackWhite);
			styledAttrs.recycle();

		}
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		filter = new ColorMatrixColorFilter(cm);
		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mMaskPaint = new Paint();
		mMaskPaint.setAntiAlias(true);
		mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	private void generateMaskPath(int width, int height) {
		this.mMaskPath = new Path();
		this.mMaskPath.addRoundRect(new RectF(0.0F, 0.0F, width, height), this.mCornerRadius, this.mCornerRadius,
				Path.Direction.CW);
		this.mMaskPath.setFillType(Path.FillType.INVERSE_WINDING);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if ((w != oldw) || (h != oldh))
			generateMaskPath(w, h);

	}

	protected void onDraw(Canvas canvas) {
		int saveCount = canvas.saveLayerAlpha(0.0F, 0.0F, canvas.getWidth(), canvas.getHeight(), 255,
				Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
		Drawable drawable = this.getDrawable();
		if (drawable != null) {
			drawable.setColorFilter(mBlackWhite ? filter : null);
		}
		super.onDraw(canvas);
		if (this.mMaskPath != null) {
			canvas.drawPath(this.mMaskPath, this.mMaskPaint);
		}
		canvas.restoreToCount(saveCount);
	}
}