package com.qmusic.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.androidquery.util.AQUtility;
import com.qmusic.R;
import com.qmusic.uitls.BLog;

/**
 * Perform as an text, editable when clicked
 * 
 * @author weicheng
 * 
 */
public class BEditView2 extends FrameLayout implements OnClickListener, OnEditorActionListener, TextWatcher, OnFocusChangeListener, Runnable {
	static final String TAG = "BEditView2";
	Typeface typeface = Typeface.SANS_SERIF;
	TextView txtTextView;
	EditText editText;
	LayoutParams layoutParams;
	int mode = 0;// text:0,edit:1
	CharSequence text, textHint;
	int color, colorHint;
	float textSize;
	int padding;
	boolean editable;

	public BEditView2(Context context) {
		this(context, null, R.attr.myBEditTextStyle);
	}

	public BEditView2(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.myBEditTextStyle);
	}

	public BEditView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	void init(Context context, AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.BEditText, defStyle, 0);
			text = styledAttrs.getString(R.styleable.BEditText_bText);
			textHint = styledAttrs.getString(R.styleable.BEditText_bTextHint);
			mode = styledAttrs.getInt(R.styleable.BEditText_bTextMode, 0);
			color = styledAttrs.getColor(R.styleable.BEditText_bTextColor, 0xff000000);
			colorHint = styledAttrs.getColor(R.styleable.BEditText_bTextColorHint, 0xff666666);
			textSize = styledAttrs.getDimensionPixelOffset(R.styleable.BEditText_bTextSize, 14);
			editable = styledAttrs.getBoolean(R.styleable.BEditText_bEditable, true);
			if (mode == 1 && !editable) {
				mode = 0;
				BLog.e("BEditView2", "editable=false and mode=1 is not allowed");
			}
			styledAttrs.recycle();
		} else {
			mode = 0;
			color = 0xff000000;
			colorHint = 0xff666666;
			textSize = AQUtility.dip2pixel(context, 14);
			editable = true;
		}
		padding = AQUtility.dip2pixel(context, 3);
		updateMode();
	}

	public void setText(CharSequence text) {
		this.text = text;
		updateMode();
	}

	public CharSequence getText() {
		return text;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (editable) {
			txtTextView.setOnClickListener(this);
		} else {
			txtTextView.setOnClickListener(null);
			if (mode == 1) {
				mode = 0;
				updateMode();
			}
		}
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
		if (mode == 0) {
			if (txtTextView != null) {
				txtTextView.setTypeface(typeface);
			}
		} else {
			if (editText != null) {
				int style = TextUtils.isEmpty(text) ? Typeface.ITALIC : Typeface.NORMAL;
				editText.setTypeface(typeface, style);
			}
		}
	}

	void initText() {
		final Context ctx = getContext();
		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		txtTextView = new TextView(ctx);
		txtTextView.setLayoutParams(layoutParams);
		txtTextView.setSingleLine();
		txtTextView.setGravity(Gravity.CENTER_VERTICAL);
		txtTextView.setTextColor(color);
		txtTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		txtTextView.setEllipsize(TruncateAt.END);
		txtTextView.setPadding(padding, padding, padding, padding);
		txtTextView.setTypeface(typeface);
		if (editable) {
			txtTextView.setOnClickListener(this);
		}
		addView(txtTextView);
	}

	void initEdit() {
		final Context ctx = getContext();
		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		editText = new EditText(ctx);
		editText.setLayoutParams(layoutParams);
		editText.setSingleLine();
		editText.setBackgroundResource(R.drawable.sharp_green_stroke);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		editText.setTextColor(color);
		editText.setHint(textHint);
		editText.setHintTextColor(colorHint);
		editText.setPadding(padding, padding, padding, padding);
		editText.setTypeface(typeface);
		editText.setOnEditorActionListener(this);
		editText.addTextChangedListener(this);
		editText.setOnFocusChangeListener(this);
		addView(editText);
	}

	void updateMode() {
		if (mode == 0) {
			if (txtTextView == null) {
				initText();
			}
			txtTextView.setVisibility(View.VISIBLE);
			txtTextView.setText(text);
			if (editText != null) {
				editText.setVisibility(View.GONE);
			}
		} else {
			if (editText == null) {
				initEdit();
			}
			editText.setVisibility(View.VISIBLE);
			editText.setText(text);
			if (text != null) {
				editText.setSelection(text.length());
			} else {
				editText.setSelection(0);
			}
			editText.requestFocus();
			if (txtTextView != null) {
				txtTextView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (editText == null) {
			initEdit();
		}
		if (mode == 0) {
			mode = 1;
			updateMode();
			AQUtility.postDelayed(this, 100);
		}
	}

	@Override
	public void run() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(getWindowToken(), 0);
		// imm.showSoftInputFromInputMethod(getWindowToken(),
		// InputMethodManager.SHOW_FORCED);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		BLog.d(TAG, "keyCode:" + keyCode + ";KeyEvent:" + event);
		return super.onKeyPreIme(keyCode, event);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			mode = 0;
			text = editText.getText().toString();
			updateMode();
		}
		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() == 0) {
			editText.setTypeface(typeface, Typeface.ITALIC);
		} else {
			editText.setTypeface(typeface, Typeface.NORMAL);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			mode = 0;
			text = editText.getText().toString();
			updateMode();
		}
	}
}
