package com.qmusic.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.qmusic.R;

public final class BWidgetHelper {
	static final String TAG = BWidgetHelper.class.getSimpleName();

	/*
	 * set the special task to the widget
	 */
	public static void update(final Context ctx, final Object task) {
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx.getApplicationContext());
		ComponentName thisWidget = new ComponentName(ctx.getApplicationContext(), BWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		if (allWidgetIds == null || allWidgetIds.length == 0)
			return;
		for (final int widgetId : allWidgetIds) {
			final RemoteViews remoteViews = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
			remoteViews.setTextViewText(R.id.widget_task_title, ctx.getString(R.string.app_name));
			Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
			ctx.startActivity(intent);
			PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

}
