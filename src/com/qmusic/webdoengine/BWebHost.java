package com.qmusic.webdoengine;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.qmusic.common.BConstants;
import com.qmusic.common.IAsyncDataCallback;
import com.qmusic.uitls.BLog;

public class BWebHost {
	static final String TAG = "BWebHost";
	private FragmentActivity activity;
	private Object jsInterface;
	private BWebView webView;
	private SparseArray<IAsyncDataCallback<Intent>> activityResultCallbacks;
	private int activityRequestCode = 1000;// increase by one every time
	private boolean animateWebView = true;
	private boolean onDrawReady, animated;

	public BWebHost(FragmentActivity activity) {
		this.activity = activity;
		activityResultCallbacks = new SparseArray<IAsyncDataCallback<Intent>>();
	}

	public FragmentActivity getHost() {
		return activity;
	}

	public BWebView getWebView() {
		return webView;
	}

	public void setWebView(BWebView webView) {
		this.webView = webView;
	}

	public Object getJSInterface() {
		return jsInterface;
	}

	public void setJSInterface(Object jsInterface) {
		this.jsInterface = jsInterface;
	}

	public void setAnimateWebView(boolean animateWebView) {
		this.animateWebView = animateWebView;
	}

	public void onCreate() {
		if (animateWebView) {
			final View decorView = activity.getWindow().getDecorView();
			decorView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					onDrawReady = true;
					if (webView != null) {
						webView.postDelayed(new Runnable() {
							@Override
							public void run() {
								showAnimate();
							}
						}, 50);
					}
					decorView.getViewTreeObserver().removeOnPreDrawListener(this);
					return false;
				}
			});
		}
	}

	public void onStart() {
		if (webView != null) {
			webView.sendJavascript("Qm.onStart();");
		}
	}

	public void onResume() {
		if (webView != null) {
			webView.sendJavascript("Qm.onResume();");
			webView.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (webView != null) {
						webView.dispatchWindowVisibilityChanged(View.VISIBLE);
						webView.invalidate();
					}
				}
			}, 1000);
		}
	}

	public void onPause() {
		if (webView != null) {
			webView.sendJavascript("Qm.onPause();");
		}
	}

	public void onStop() {
		if (webView != null) {
			webView.sendJavascript("Qm.onStop();");
		}
	}

	public void onDestory() {
		// =========
		if (webView != null) {
			webView.sendJavascript("Qm.onDestory();");
			// webView.clearView();
			// webView.reload();
		}
		// =========
		activity = null;
		webView = null;
	}

	public void startActivityForResult(final Intent intent, final IAsyncDataCallback<Intent> callback) {
		if (activity != null) {
			activityResultCallbacks.put(activityRequestCode, callback);
			activity.startActivityForResult(intent, activityRequestCode);
			activityRequestCode++;
		}
	}

	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		final IAsyncDataCallback<Intent> callback = activityResultCallbacks.get(requestCode);
		if (callback != null) {
			activityResultCallbacks.remove(requestCode);
			callback.callback(resultCode, data);
		}
	}

	public void showAnimate() {
		if (webView == null || animated || !webView.getState() || !onDrawReady) {
			return;
		}
		animated = true;
		webView.setVisibility(View.VISIBLE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
		scaleAnimation.setDuration(500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		webView.startAnimation(animationSet);
	}

	/**
	 * call directly
	 * 
	 * @param what
	 * @param arg1
	 * @param obj
	 * @return
	 */
	public final Object exec(int what, int arg1, Object obj) {
		BLog.e(TAG, "should override exec");
		return null;
	}

	/**
	 * use message queue
	 * 
	 * @param what
	 * @param arg1
	 * @param obj
	 */
	public final void postMessage(final int what, final int arg1, final Object obj) {
		webView.post(new Runnable() {
			@Override
			public void run() {
				onMessage(what, arg1, obj);
			}
		});
	}

	/**
	 * only be called by sub-class
	 * 
	 * @param what
	 * @param arg1
	 * @param obj
	 * @return
	 */
	protected Object onMessage(int what, int arg1, Object obj) {
		switch (what) {
		case BConstants.MSG_PAGE_START_LOADING: {
			if (animateWebView) {
				webView.setVisibility(View.INVISIBLE);
			}
			break;
		}
		case BConstants.MSG_PAGE_FINISH_LOADING: {
			if (animateWebView) {
				showAnimate();
			}
			break;
		}
		}
		return null;
	}
}
