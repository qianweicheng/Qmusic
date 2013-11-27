package com.qmusic.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat.Builder;

import com.qmusic.R;

public class BNotification {
	static final String TAG = BNotification.class.getSimpleName();
	public static int NOTIFICATION_ID = 0;
	private static final int NOFICATION_COUNT = 2;

	public static void notify(Context ctx, String title, String subTitle, Parcelable params) {
		NOTIFICATION_ID = NOTIFICATION_ID % NOFICATION_COUNT + 1;
		// Log.i(TAG, "NOTIFICATION_ID:" + NOTIFICATION_ID);
		NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, NOTIFICATION_ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Builder builder = new Builder(ctx);
		builder.setSmallIcon(R.drawable.icon).setTicker(title).setAutoCancel(true).setContentTitle(title)
				.setContentText(subTitle).setContentIntent(contentIntent);
		Notification notif = builder.build();
		notif.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		notif.number = NOTIFICATION_ID;
		manager.notify(NOTIFICATION_ID, notif);
	}

	public static void cancel(Context ctx) {
		NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
	}
}
