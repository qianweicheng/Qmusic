package com.qmusic.controls.dialogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.qmusic.R;
import com.qmusic.common.BConstants;

public final class BDateTimePickerFragment extends BaseDialogFragment implements DialogInterface.OnClickListener {
	static final String TAG = BDateTimePickerFragment.class.getSimpleName();
	public static final int MODE_TIME = 0;
	public static final int MODE_DATE = 1;
	public static final int MODE_BOTH = 2;

	Calendar calendar;
	TimePicker timePicker;
	DatePicker datePicker;
	OnDateTimePickerSelectedCallback callback;
	long minimumDate = Long.MIN_VALUE;
	SimpleDateFormat sdf;
	int mode;
	String defaultDate, dateFormat;

	public static final BDateTimePickerFragment getInstance(int mode, String defaultDate, long minimumDate,
			String dateFormat) {
		BDateTimePickerFragment fragment = new BDateTimePickerFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("mode", mode);
		bundle.putString("defaultDate", defaultDate);
		bundle.putLong("minimumDate", minimumDate);
		bundle.putString("dateFormat", dateFormat);
		fragment.setArguments(bundle);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		fragment.setCancelable(false);
		return fragment;
	}

	/**
	 * During creation, if arguments have been supplied to the fragment then
	 * parse those out.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calendar = Calendar.getInstance();
		Bundle args;
		if (savedInstanceState != null) {
			args = savedInstanceState;
		} else {
			args = getArguments();
		}
		mode = args.getInt("mode");
		defaultDate = args.getString("defaultDate");
		minimumDate = args.getLong("minimumDate");
		dateFormat = args.getString("dateFormat");
		if (!TextUtils.isEmpty(dateFormat) && !TextUtils.isEmpty(defaultDate)) {
			try {
				sdf = new SimpleDateFormat(dateFormat, Locale.US);
				Date date = null;
				try {
					if (TextUtils.isDigitsOnly(defaultDate)) {
						long epoch = Long.parseLong(defaultDate);
						date = new Date(epoch);
					} else {
						date = sdf.parse(defaultDate);
					}
				} catch (Exception ex1) {
					try {
						String tmpDateStr = defaultDate.replace("UTC", "GMT");
						date = sdf.parse(tmpDateStr);
					} catch (Exception ex2) {
						ex2.printStackTrace();
					}
				}
				if (date != null) {
					calendar.setTime(date);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (callback == null && activity instanceof OnDateTimePickerSelectedCallback) {
			callback = (OnDateTimePickerSelectedCallback) activity;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putInt("mode", mode);
		bundle.putString("defaultDate", defaultDate);
		bundle.putLong("minimumDate", minimumDate);
		bundle.putString("dateFormat", dateFormat);
	}

	@SuppressLint("NewApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater vi = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = vi.inflate(R.layout.fragment_date_time_picker, null);
		timePicker = (TimePicker) layout.findViewById(R.id.fragment_time_picker1);
		datePicker = (DatePicker) layout.findViewById(R.id.fragment_date_picker1);
		if (Build.VERSION.SDK_INT >= 11 && minimumDate > 0) {
			datePicker.setMinDate(minimumDate);
			datePicker.setCalendarViewShown(false);
		}
		timePicker.setVisibility(View.GONE);
		datePicker.setVisibility(View.GONE);
		if (mode == MODE_TIME || mode == MODE_BOTH) {
			timePicker.setVisibility(View.VISIBLE);
			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
		if (mode == MODE_DATE || mode == MODE_BOTH) {
			datePicker.setVisibility(View.VISIBLE);
			datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
					null);
		}
		Builder builder = new Builder(this.getActivity());
		builder.setView(layout);
		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);
		Dialog dialog = builder.create();
		return dialog;
	}

	public static interface OnDateTimePickerSelectedCallback {
		void callback(int result, Date date);
	}

	public void setCallbackDate(OnDateTimePickerSelectedCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (callback == null)
			return;
		if (which == DialogInterface.BUTTON_POSITIVE) {
			datePicker.clearFocus();
			timePicker.clearFocus();
			calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
					timePicker.getCurrentHour(), timePicker.getCurrentMinute());
			if (calendar.getTimeInMillis() >= minimumDate) {
				Date dateReturn = calendar.getTime();
				callback.callback(BConstants.OP_RESULT_OK, dateReturn);
			} else {
				callback.callback(BConstants.OP_RESULT_CANCELED, null);
				if (sdf != null) {
					BToast.toast("Please select a date later than " + sdf.format(new Date(minimumDate)));
				}
			}
		} else if (which == DialogInterface.BUTTON_NEGATIVE) {
			callback.callback(BConstants.OP_RESULT_CANCELED, null);
		}
	}

}
