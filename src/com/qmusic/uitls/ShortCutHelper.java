package com.qmusic.uitls;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Parcelable;

import com.qmusic.R;
import com.qmusic.test.TestActivity;

public final class ShortCutHelper {
	public static final void create(Context ctx) {
		// 创建快捷方式的Intent
		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutintent.putExtra("duplicate", false);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "测试快捷方式");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(ctx.getApplicationContext(), R.drawable.icon);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(ctx.getResources(), R.drawable.tab1));
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(ctx.getApplicationContext(),
				TestActivity.class));
		ctx.sendBroadcast(shortcutintent);
	}
}
