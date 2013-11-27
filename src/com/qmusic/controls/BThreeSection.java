package com.qmusic.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmusic.R;

public class BThreeSection extends ViewGroup implements OnClickListener {
	static final String TAG = BThreeSection.class.getSimpleName();
	TextView txtSection0;
	TextView txtSection1;
	TextView txtSection2;
	String tab0, tab1, tab2;
	int selected;

	public BThreeSection(Context context) {
		super(context);
		init();
	}

	public BThreeSection(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.SectionTab);
		selected = styledAttrs.getInt(R.styleable.SectionTab_selectedTab, 0);
		styledAttrs.recycle();
		init();
	}

	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putString("tab0", tab0);
		bundle.putString("tab1", tab1);
		bundle.putString("tab2", tab2);
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			tab0 = bundle.getString("tab0");
			tab1 = bundle.getString("tab1");
			tab2 = bundle.getString("tab2");
			super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

		} else {
			super.onRestoreInstanceState(state);
		}
		updateState();
	}

	void init() {
		tab0 = "Tab0";
		tab1 = "Tab1";
		tab2 = "Tab2";
		Context context = getContext();
		setBackgroundResource(R.drawable.three_section);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		txtSection0 = new TextView(context);
		txtSection0.setLayoutParams(params);
		txtSection0.setGravity(Gravity.CENTER);
		txtSection0.setText(tab0);
		txtSection0.setOnClickListener(this);
		txtSection0.setBackgroundResource(R.drawable.three_section_button_tab1);
		txtSection1 = new TextView(context);
		txtSection1.setLayoutParams(params);
		txtSection1.setGravity(Gravity.CENTER);
		txtSection1.setText(tab1);
		txtSection1.setOnClickListener(this);
		txtSection1.setBackgroundResource(R.drawable.three_section_button_tab2);
		txtSection2 = new TextView(context);
		txtSection2.setLayoutParams(params);
		txtSection2.setGravity(Gravity.CENTER);
		txtSection2.setText(tab2);
		txtSection2.setOnClickListener(this);
		txtSection2.setBackgroundResource(R.drawable.three_section_button_tab3);
		addView(txtSection0);
		addView(txtSection1);
		addView(txtSection2);
		updateState();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthPadding = getWidthPadding();
		final int heightPadding = getHeightPadding();
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		final int size = getChildCount();
		int childWidth = measuredWidth / size;
		int childHeight = measuredHeight;
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt(i);
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
		setMeasuredDimension(measuredWidth + widthPadding, measuredHeight + heightPadding);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = getPaddingLeft() + getHorizontalFadingEdgeLength();

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, getPaddingTop(), childLeft + childWidth,
						getPaddingTop() + child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	private int getWidthPadding() {
		return getPaddingLeft() + getPaddingRight() + getHorizontalFadingEdgeLength() * 2;
	}

	private int getHeightPadding() {
		return getPaddingTop() + getPaddingBottom();
	}

	public void setTitle(int section, String title) {
		if (section == 0) {
			this.tab0 = title;
			txtSection0.setText(tab0);
		} else if (section == 1) {
			this.tab1 = title;
			txtSection1.setText(tab1);
		} else if (section == 2) {
			this.tab2 = title;
			txtSection2.setText(tab2);
		}
	}

	public void setState(int selected) {
		this.selected = selected;
		updateState();
	}

	public int getSelectedSection() {
		return selected;
	}

	private void updateState() {
		txtSection0.setSelected(selected == 0);
		txtSection1.setSelected(selected == 1);
		txtSection2.setSelected(selected == 2);
	}

	@Override
	public void onClick(View v) {
		if (v == txtSection0) {
			selected = 0;
		} else if (v == txtSection1) {
			selected = 1;
		} else if (v == txtSection2) {
			selected = 2;
		}
		updateState();
	}

}
