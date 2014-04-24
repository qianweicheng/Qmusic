package com.qmusic.uitls;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.qmusic.R;
import com.qmusic.activities.MainActivity;
import com.qmusic.activities.SplashActivity;

public final class ShortCutHelper {
	static final String TAG = ShortCutHelper.class.getSimpleName();
	static final String INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	static final String UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
	static final String READ_SETTINGS = "com.android.launcher.permission.READ_SETTINGS";
	static final String WRITE_SETTINGS = "com.android.launcher.permission.WRITE_SETTINGS";

	/**
	 * 
	 * @param ctx
	 * @param title
	 * @param icon
	 */
	public static final void create(Context ctx, String title, Bitmap icon) {
		if (icon == null) {
			return;
		}
		// 创建快捷方式的Intent
		Intent shortcutintent = new Intent(INSTALL_SHORTCUT);
		shortcutintent.putExtra("duplicate", false);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		// shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
		// BitmapFactory.decodeResource(ctx.getResources(), R.drawable.tab1));
		Intent target = new Intent();
		// target.setComponent(new ComponentName(ctx.getPackageName(),
		// TestActivity.class.getName()));
		target.setClass(ctx.getApplicationContext(), SplashActivity.class);
		/* 以下两句是为了在卸载应用的时候同时删除桌面快捷方式 */
		target.setAction(Intent.ACTION_MAIN);
		target.addCategory(Intent.CATEGORY_LAUNCHER);
		target.putExtra(SplashActivity.ROUTE, true);
		target.putExtra(SplashActivity.ORIGININTENT, new Intent(ctx.getApplicationContext(), MainActivity.class));
		target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		ctx.sendBroadcast(shortcutintent);
	}

	public static final void create(Context ctx, String title) {
		// 创建快捷方式的Intent
		Intent shortcutintent = new Intent(INSTALL_SHORTCUT);
		shortcutintent.putExtra("duplicate", false);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		Parcelable icon = Intent.ShortcutIconResource.fromContext(ctx.getApplicationContext(), R.drawable.icon);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
		// BitmapFactory.decodeResource(ctx.getResources(), R.drawable.tab1));
		Intent target = new Intent();
		// target.setComponent(new ComponentName(ctx.getPackageName(),
		// TestActivity.class.getName()));
		target.setClass(ctx.getApplicationContext(), SplashActivity.class);
		/* 以下两句是为了在卸载应用的时候同时删除桌面快捷方式 */
		target.setAction(Intent.ACTION_MAIN);
		target.addCategory(Intent.CATEGORY_LAUNCHER);
		target.putExtra(SplashActivity.ROUTE, true);
		target.putExtra(SplashActivity.ORIGININTENT, new Intent(ctx.getApplicationContext(), MainActivity.class));
		target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		ctx.sendBroadcast(shortcutintent);
	}

	public static final void remove(Context ctx, String title) {
		Intent shortcut = new Intent(UNINSTALL_SHORTCUT);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		Intent target = new Intent();
		target.setClass(ctx.getApplicationContext(), SplashActivity.class);
		target.setAction(Intent.ACTION_MAIN);
		target.addCategory(Intent.CATEGORY_LAUNCHER);
		target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		shortcut.putExtra("duplicate", false);
		ctx.sendBroadcast(shortcut);
	}

	public static final void remove2(Context ctx, String title, String packagename) {
		final String AUTHORITY = BUtilities.getAuthorityFromPermission(ctx.getApplicationContext(), WRITE_SETTINGS);
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		try {
			StringBuilder where = new StringBuilder();
			if (TextUtils.isEmpty(title)) {
				where.append(" 1=1 ");
			} else {
				where.append(String.format("title='%s'", title));
			}
			if (!TextUtils.isEmpty(packagename)) {
				where.append(" and ");
				where.append(String.format(" intent like '%%component=%s/%%' ", packagename.replace("'", "''")));
			}
			int result = ctx.getContentResolver().delete(CONTENT_URI, where.toString(), null);
			BLog.i(TAG, result + " item(s) deleted.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static final void update(Context ctx, long id, String title, String iconPackage, String iconResource, int x,
			int y) {
		final String AUTHORITY = BUtilities.getAuthorityFromPermission(ctx.getApplicationContext(), WRITE_SETTINGS);
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		try {
			ContentValues cv = new ContentValues();
			if (title != null) {
				cv.put("title", title);
			}
			if (iconPackage != null) {
				cv.put("iconPackage", iconPackage);
			}
			if (iconResource != null) {
				cv.put("iconResource", iconResource);
			}
			if (x > 0) {
				cv.put("cellX", x);
			}
			if (y > 0) {
				cv.put("cellY", y);
			}
			// 0:应用程序(不能改变),1:快捷方式,2:文件夹,3:活动文件夹,4:widget
			cv.put("itemType", "1");
			int result = ctx.getContentResolver().update(CONTENT_URI, cv, "_id=?", new String[] { String.valueOf(id) });
			BLog.i(TAG, result + " item(s) updated.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static final long hasShortcut(Context ctx, String title, String packagename) {
		final String AUTHORITY = BUtilities.getAuthorityFromPermission(ctx.getApplicationContext(), READ_SETTINGS);
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		long result = 0;
		Cursor c = null;
		try {
			StringBuilder where = new StringBuilder();
			if (TextUtils.isEmpty(title)) {
				where.append(" 1=1 ");
			} else {
				where.append(String.format("title='%s'", title));
			}
			if (!TextUtils.isEmpty(packagename)) {
				where.append(" and ");
				where.append(String.format(" intent like '%%component=%s/%%' ", packagename.replace("'", "''")));
			}
			c = ctx.getContentResolver().query(CONTENT_URI, new String[] { "_id" }, where.toString(), null, null);
			while (c.moveToNext()) {
				result = c.getLong(0);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	public static final void listShortcut(Context ctx) {
		final String AUTHORITY = BUtilities.getAuthorityFromPermission(ctx.getApplicationContext(), READ_SETTINGS);
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		Cursor c = null;
		try {
			c = ctx.getContentResolver().query(CONTENT_URI, null, null, null, null);

			while (c.moveToNext()) {
				int count = c.getColumnCount();
				for (int i = 0; i < count; i++) {
					if ("icon".equals(c.getColumnName(i))) {
					} else {
						Log.i("ShortCutHelper", c.getColumnName(i) + ":" + c.getString(i));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}
}
