package com.qmusic.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import com.qmusic.uitls.BLog;

public class BWidgetProvider extends AppWidgetProvider {
	static final String TAG = BWidgetProvider.class.getSimpleName();

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		BLog.i(TAG, "onEnabled");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		BLog.i(TAG, "onDisabled");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		BLog.i(TAG, "onDeleted");
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		BWidgetHelper.update(context, null);
	}
}
