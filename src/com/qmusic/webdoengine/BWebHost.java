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

import com.androidquery.util.AQUtility;
import com.qmusic.common.BConstants;
import com.qmusic.common.IAsyncDataCallback;
import com.qmusic.uitls.BLog;

public abstract class BWebHost {
	static final String TAG = "BWebHost";
	private FragmentActivity activity;
	private Object jsInterface;
	private BWebView webView;
	private SparseArray<IAsyncDataCallback<Intent>> activityResultCallbacks;
	private int activityRequestCode = 1000;// increase by one every time
	private boolean animateWebView = true;

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
					if (webView != null) {
						webView.postDelayed(new Runnable() {
							@Override
							public void run() {
								webView.setVisibility(View.VISIBLE);
								AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
								alphaAnimation.setDuration(500);
								ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_PARENT, 0.5f,
										Animation.RELATIVE_TO_PARENT, 0.5f);
								scaleAnimation.setDuration(500);
								AnimationSet animationSet = new AnimationSet(true);
								animationSet.addAnimation(alphaAnimation);
								animationSet.addAnimation(scaleAnimation);
								webView.startAnimation(animationSet);
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
		webView.sendJavascript("onStart();");
	}

	public void onResume() {
		webView.sendJavascript("onResume();");
	}

	public void onPause() {
		webView.sendJavascript("onPause();");
	}

	public void onStop() {
		webView.sendJavascript("onStop();");
	}

	public void onDestory() {
		// =========
		webView.sendJavascript("onDestory();");
		// webView.clearView();
		// webView.reload();
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
		AQUtility.post(new Runnable() {
			@Override
			public void run() {
				if (animateWebView) {
					if (what == BConstants.MSG_PAGE_START_LOADING) {
						webView.setVisibility(View.INVISIBLE);
					}
				}
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
		BLog.w(TAG, String.format("arg0:%d,arg1:%d,obj:%s", what, arg1, obj));
		return null;
	}
}
