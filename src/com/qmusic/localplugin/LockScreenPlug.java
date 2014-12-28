package com.qmusic.localplugin;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.qmusic.localplugin.lockscreen.LockScreenReceiver;
import com.qmusic.localplugin.lockscreen.LockScreenView;

public class LockScreenPlug extends BasePlug {
	public static final String TAG = LockScreenPlug.class.getSimpleName();
	Context ctx;
	IntentFilter intentFilter;
	LockScreenReceiver lockScreenReceiver;
	// ============
	private WindowManager mWindowManager;
	private View activityView, fullView;
	private LayoutParams fullScreenParams, activityParams;
	private boolean isLocked;

	@Override
	public void init(Context ctx) {
		this.ctx = ctx;
		lockScreenReceiver = new LockScreenReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		ctx.registerReceiver(lockScreenReceiver, intentFilter);
	}

	@Override
	public void destory() {
		ctx.unregisterReceiver(lockScreenReceiver);
	}

	private void init2() {
		mWindowManager = (WindowManager) ctx.getSystemService(Service.WINDOW_SERVICE);
		activityView = new LockScreenView(ctx);
		fullScreenParams = createFullParams(ctx);
		activityParams = createActivityParams(ctx);
	}

	private LayoutParams createFullParams(Context ctx) {
		LayoutParams fullParams = new LayoutParams();
		fullParams.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
		fullParams.format = PixelFormat.RGBA_8888;
		fullParams.flags = LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		fullParams.gravity = Gravity.LEFT | Gravity.TOP;
		return fullParams;
	}

	private LayoutParams createActivityParams(Context ctx) {
		LayoutParams mLockViewLayoutParams = new LayoutParams();
		mLockViewLayoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		// TYPE_SYSTEM_ERROR 这个可以全屏，覆盖statusbar
		// TYPE_SYSTEM_OVERLAY;不能获取事件
		// TYPE_SYSTEM_ALERT 虽然能保持最上面，但不能屏蔽按钮
		// TYPE_PRIORITY_PHONE 同上
		mLockViewLayoutParams.format = PixelFormat.RGBA_8888;// 控制透明度
		mLockViewLayoutParams.flags = LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE
		        | LayoutParams.FLAG_LAYOUT_IN_SCREEN | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED;
		// | LayoutParams.FLAG_LAYOUT_INSET_DECOR|
		mLockViewLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		// mLockViewLayoutParams.x = 0;
		// mLockViewLayoutParams.y = 0;
		// mLockViewLayoutParams.width = LayoutParams.MATCH_PARENT;
		// mLockViewLayoutParams.height = LayoutParams.MATCH_PARENT;
		// DisplayMetrics displayMetrics =
		// ctx.getResources().getDisplayMetrics();
		// mLockViewLayoutParams.width = displayMetrics.widthPixels;
		// mLockViewLayoutParams.height = displayMetrics.heightPixels;
		return mLockViewLayoutParams;
	}

	@SuppressWarnings("deprecation")
	public synchronized void lock() {
		if (!isLocked) {
			isLocked = true;
			if (mWindowManager == null) {
				init2();
			}
			mWindowManager.addView(activityView, activityParams);
			KeyguardManager keyguardManager = (KeyguardManager) ctx.getSystemService(Activity.KEYGUARD_SERVICE);
			KeyguardLock lock = keyguardManager.newKeyguardLock("lock");
			lock.disableKeyguard();
		}
	}

	public synchronized void unlock() {
		if (isLocked) {
			isLocked = false;
			mWindowManager.removeView(activityView);
		}
	}

	public synchronized void startAnimation() {
		mWindowManager.addView(fullView, fullScreenParams);
	}

	public synchronized void stopAnimation() {
		mWindowManager.removeView(fullView);
	}

	public static final void lockS() {
		LockScreenPlug lockScreenPlug = (LockScreenPlug) PluginManager.getPlugin(LockScreenPlug.class.getSimpleName());
		if (lockScreenPlug != null) {
			lockScreenPlug.lock();
		}
	}

	public static final void unlockS() {
		LockScreenPlug lockScreenPlug = (LockScreenPlug) PluginManager.getPlugin(LockScreenPlug.class.getSimpleName());
		if (lockScreenPlug != null) {
			lockScreenPlug.unlock();
		}
	}

	public static final void startAnimationS() {
		LockScreenPlug lockScreenPlug = (LockScreenPlug) PluginManager.getPlugin(LockScreenPlug.class.getSimpleName());
		if (lockScreenPlug != null) {
			lockScreenPlug.startAnimation();
		}
	}

	public static final void stopAnimationS() {
		LockScreenPlug lockScreenPlug = (LockScreenPlug) PluginManager.getPlugin(LockScreenPlug.class.getSimpleName());
		if (lockScreenPlug != null) {
			lockScreenPlug.stopAnimation();
		}
	}
}
