package com.qmusic.controls;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmusic.R;

/**
 * 
 * @author weicheng
 * 
 */
public class BCommonTitle extends RelativeLayout implements OnClickListener {
	static final String TAG = BCommonTitle.class.getSimpleName();
	ImageButton imgLeft;
	ImageButton imgRight;
	Button btnLeft;
	Button btnRight;
	TextView txtTitle;
	CharSequence title, rightText, leftText;
	Drawable leftDrawable, rightDrawable;
	int padding;

	public BCommonTitle(Context context) {
		this(context, null, R.attr.myCommonTitleStyle);
	}

	public BCommonTitle(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.myCommonTitleStyle);
	}

	public BCommonTitle(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		if (attrs != null) {
			TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.BCommonTitle);
			title = styledAttrs.getString(R.styleable.BCommonTitle_common_title);
			rightText = styledAttrs.getString(R.styleable.BCommonTitle_right_text);
			leftText = styledAttrs.getString(R.styleable.BCommonTitle_left_text);
			leftDrawable = styledAttrs.getDrawable(R.styleable.BCommonTitle_left_img);
			rightDrawable = styledAttrs.getDrawable(R.styleable.BCommonTitle_right_img);
			styledAttrs.recycle();
		}
		init(context);
	}

	private void init(Context context) {
		int width = (int) context.getResources().getDimension(R.dimen.title_button_width);
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
			imgLeft.setContentDescription("Back");
		} else {
			imgLeft.setImageDrawable(leftDrawable);
		}

		LayoutParams leftLayoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		btnLeft = new Button(context);
		btnLeft.setId(R.id.common_title_left_txt);
		btnLeft.setTextColor(getResources().getColor(R.color.blue));
		btnLeft.setLayoutParams(leftLayoutParams2);
		btnLeft.setMinimumWidth(width);
		btnLeft.setBackgroundResource(R.drawable.b_button);
		btnLeft.setOnClickListener(this);
		if (!TextUtils.isEmpty(leftText)) {
			btnLeft.setText(leftText);
		} else {
			btnLeft.setVisibility(View.INVISIBLE);
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
			imgRight.setVisibility(View.INVISIBLE);
		}

		LayoutParams rightLayoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		rightLayoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		btnRight = new Button(context);
		btnRight.setId(R.id.common_title_right_txt);
		btnRight.setTextColor(getResources().getColor(R.color.blue));
		btnRight.setLayoutParams(rightLayoutParams2);
		btnRight.setMinimumWidth(width);
		btnRight.setBackgroundResource(R.drawable.b_button);
		if (!TextUtils.isEmpty(rightText)) {
			btnRight.setText(rightText);
		} else {
			btnRight.setVisibility(View.INVISIBLE);
		}

		LayoutParams titleLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.common_title_left_img);
		titleLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.common_title_right_img);
		txtTitle = new TextView(context);
		txtTitle.setId(R.id.common_title_title_txt);
		txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_large_x));
		txtTitle.setLayoutParams(titleLayoutParams);
		txtTitle.setTextColor(getResources().getColor(R.color.blue));
		txtTitle.setBackgroundColor(0x00000000);
		txtTitle.setSingleLine();
		txtTitle.setEllipsize(TruncateAt.MARQUEE);
		txtTitle.setGravity(Gravity.CENTER);
		txtTitle.setSelected(true);
		if (title == null) {
			txtTitle.setText("");
		} else {
			txtTitle.setText(title);
		}

		LayoutParams divisionLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
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
		setBackgroundColor(0xffffffff);
	}

	public void setTitle(CharSequence title) {
		txtTitle.setText(title);
	}

	/**
	 * 
	 * @param resId
	 *            if <=0 then hide the button
	 * @param hintRes
	 */
	public void setLeftImg(int resId, int hintRes) {
		if (resId > 0) {
			imgLeft.setImageResource(resId);
			imgLeft.setVisibility(View.VISIBLE);
			imgLeft.setContentDescription(getContext().getString(hintRes));
		} else {
			imgLeft.setVisibility(View.INVISIBLE);
		}
		btnLeft.setVisibility(View.INVISIBLE);
	}

	public void setLeftText(CharSequence left) {
		if (TextUtils.isEmpty(left)) {
			btnLeft.setVisibility(View.INVISIBLE);
		} else {
			btnLeft.setVisibility(View.VISIBLE);
			btnLeft.setText(left);
		}
		imgLeft.setVisibility(View.INVISIBLE);
	}

	/**
	 * 
	 * @param resId
	 *            if <=0 then hide the button
	 * @param hintRes
	 */
	public void setRightImg(int resId, int hintRes) {
		if (resId > 0) {
			imgRight.setImageResource(resId);
			imgRight.setVisibility(View.VISIBLE);
			imgRight.setContentDescription(getContext().getString(hintRes));
		} else {
			imgRight.setVisibility(View.INVISIBLE);
		}
		btnRight.setVisibility(View.INVISIBLE);
	}

	public void setRightText(CharSequence right) {
		if (TextUtils.isEmpty(right)) {
			btnRight.setVisibility(View.INVISIBLE);
		} else {
			btnRight.setVisibility(View.VISIBLE);
			btnRight.setText(right);
		}
		imgRight.setVisibility(View.INVISIBLE);
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
