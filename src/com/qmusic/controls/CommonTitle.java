package com.qmusic.controls;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.qmusic.R;

/**
 * 
 * @author weicheng
 * 
 */
public class CommonTitle extends RelativeLayout implements OnClickListener {
	static final String TAG = CommonTitle.class.getSimpleName();
	ImageButton imgLeft;
	ImageButton imgRight;
	Button btnLeft;
	Button btnRight;
	TextView txtTitle;
	CharSequence title, rightText, leftText;
	Drawable leftDrawable, rightDrawable;

	public CommonTitle(Context context) {
		super(context);
		init(context);
	}

	public CommonTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CommonTitle);
		title = styledAttrs.getString(R.styleable.CommonTitle_common_title);
		rightText = styledAttrs.getString(R.styleable.CommonTitle_right_text);
		leftText = styledAttrs.getString(R.styleable.CommonTitle_left_text);
		leftDrawable = styledAttrs.getDrawable(R.styleable.CommonTitle_left_img);
		rightDrawable = styledAttrs.getDrawable(R.styleable.CommonTitle_right_img);
		styledAttrs.recycle();
		init(context);
	}

	private void init(Context context) {
		int padding = AQUtility.dip2pixel(getContext(), 12);
		int padding3 = AQUtility.dip2pixel(getContext(), 3);
		int width = AQUtility.dip2pixel(context, 60);
		LayoutParams leftLayoutParams = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		imgLeft = new ImageButton(context);
		imgLeft.setId(R.id.common_title_left_img);
		imgLeft.setPadding(padding, padding, padding, padding);
		imgLeft.setLayoutParams(leftLayoutParams);
		imgLeft.setScaleType(ScaleType.CENTER_INSIDE);
		imgLeft.setBackgroundResource(R.drawable.b_button);
		imgLeft.setOnClickListener(this);
		if (leftDrawable == null && TextUtils.isEmpty(leftText)) {
			imgLeft.setImageResource(R.drawable.backarrow);
		} else {
			imgLeft.setImageDrawable(leftDrawable);
		}

		LayoutParams leftLayoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		btnLeft = new Button(context);
		btnLeft.setId(R.id.common_title_left_txt);
		btnLeft.setPadding(padding3, 0, padding3, 0);
		btnLeft.setTextColor(0xffffffff);
		btnLeft.setLayoutParams(leftLayoutParams2);
		btnLeft.setMinimumWidth(width);
		btnLeft.setBackgroundResource(R.drawable.b_button);
		btnLeft.setOnClickListener(this);
		if (!TextUtils.isEmpty(leftText)) {
			btnLeft.setText(leftText);
		} else {
			btnLeft.setVisibility(View.GONE);
		}

		LayoutParams rightLayoutParams = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		imgRight = new ImageButton(context);
		imgRight.setId(R.id.common_title_right_img);
		imgRight.setPadding(padding, padding, padding, padding);
		imgRight.setLayoutParams(rightLayoutParams);
		imgRight.setScaleType(ScaleType.CENTER_INSIDE);
		imgRight.setBackgroundResource(R.drawable.b_button);
		if (rightDrawable != null) {
			imgRight.setImageDrawable(rightDrawable);
		} else {
			imgRight.setVisibility(View.GONE);
		}

		LayoutParams rightLayoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		rightLayoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		btnRight = new Button(context);
		btnRight.setId(R.id.common_title_right_txt);
		btnRight.setPadding(padding3, 0, padding3, 0);
		btnRight.setTextColor(0xffffffff);
		btnRight.setLayoutParams(rightLayoutParams2);
		btnRight.setMinimumWidth(width);
		btnRight.setBackgroundResource(R.drawable.b_button);
		if (!TextUtils.isEmpty(rightText)) {
			btnRight.setText(rightText);
		} else {
			btnRight.setVisibility(View.GONE);
		}

		LayoutParams titleLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		txtTitle = new TextView(context);
		txtTitle.setId(R.id.common_title_title_txt);
		try {
			txtTitle.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
		} catch (Exception ex) {
			ex.printStackTrace();
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		}
		txtTitle.setLayoutParams(titleLayoutParams);
		txtTitle.setTextColor(0xffffffff);
		txtTitle.setBackgroundColor(0x00000000);
		if (TextUtils.isEmpty(title)) {
			txtTitle.setText("");
		} else {
			txtTitle.setText(title);
		}

		LayoutParams divisionLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, AQUtility.dip2pixel(context, 1));
		divisionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		View division = new View(context);
		division.setLayoutParams(divisionLayoutParams);
		division.setBackgroundColor(getResources().getColor(R.color.gray_division));
		this.addView(imgLeft);
		this.addView(btnLeft);
		this.addView(txtTitle);
		this.addView(imgRight);
		this.addView(btnRight);
		this.addView(division);
		setBackgroundColor(getResources().getColor(R.color.red));
	}

	public void setTitle(CharSequence title) {
		txtTitle.setText(title);
	}

	public void setLeftImg(int resId) {
		if (resId > 0) {
			imgLeft.setImageResource(resId);
			imgLeft.setVisibility(View.VISIBLE);
		} else {
			imgLeft.setVisibility(View.GONE);
		}
		btnLeft.setVisibility(View.GONE);
	}

	public void setLeftText(CharSequence left) {
		if (TextUtils.isEmpty(left)) {
			btnLeft.setVisibility(View.GONE);
		} else {
			btnLeft.setVisibility(View.VISIBLE);
			btnLeft.setText(left);
		}
		imgLeft.setVisibility(View.GONE);
	}

	public void setRightImg(int resId) {
		if (resId > 0) {
			imgRight.setImageResource(resId);
			imgRight.setVisibility(View.VISIBLE);
		} else {
			imgRight.setVisibility(View.GONE);
		}
		btnRight.setVisibility(View.GONE);
	}

	public void setRightText(CharSequence right) {
		if (TextUtils.isEmpty(right)) {
			btnRight.setVisibility(View.GONE);
		} else {
			btnRight.setVisibility(View.VISIBLE);
			btnRight.setText(right);
		}
		imgRight.setVisibility(View.GONE);
	}

	public void setLeftImgCallback(OnClickListener callback) {
		imgLeft.setOnClickListener(callback);
	}

	public void setLeftTextCallback(OnClickListener callback) {
		btnLeft.setOnClickListener(callback);
	}

	public void setRightImgCallback(OnClickListener callback) {
		imgRight.setOnClickListener(callback);
	}

	public void setRightTextCallback(OnClickListener callback) {
		btnRight.setOnClickListener(callback);
	}

	@Override
	public void onClick(View v) {
		if (v == imgLeft || v == btnLeft) {
			Context ctx = getContext();
			if (ctx instanceof Activity) {
				((Activity) ctx).finish();
			}
		}
	}
}
