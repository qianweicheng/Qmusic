package com.qmusic.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.qmusic.R;

public class BEditView extends EditText {
	Typeface typeface = Typeface.SANS_SERIF;
	int type = Typeface.NORMAL;

	public BEditView(Context context) {
		super(context);
		init();
	}

	public BEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		this.setTextColor(getResources().getColor(R.color.gray_dark_text));
		this.setHintTextColor(getResources().getColor(R.color.gray_light_text));
		this.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.margin_m));
	}

	public void setFontStyle(Typeface typeface, int type) {
		this.typeface = typeface;
		this.type = type;
		this.setTypeface(typeface, type);
	}

	public void setBackGroundColor(int color) {
		this.setBackgroundColor(color);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (text.length() == 0) {
			this.setTypeface(typeface, Typeface.ITALIC);
		} else {
			this.setTypeface(typeface, type);
		}
	}

}
