package com.qmusic.controls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

/**
 * 
 * @author weicheng
 * 
 */
public class IconStateListDrawable extends StateListDrawable {
	public static final int ICON_TYPE_BUTTON = 0;
	public static final int ICON_TYPE_ENABLE = 1;
	public static final int ICON_TYPE_DISENABLE = 2;
	static final String TAG = IconStateListDrawable.class.getSimpleName();
	static int[] STATE_ENABLED = new int[] {};
	static int[] STATE_DIS_ABLED = new int[] {};
	String type;
	Drawable d1, d2;
	WeakReference<ImageView> imageViewCache;

	private IconStateListDrawable() {

	}

	public IconStateListDrawable(Drawable d1, Drawable d2) {
		this.d1 = d1;
		this.d2 = d2;
		addState(new int[] { android.R.attr.state_pressed }, d1);
		addState(new int[] { android.R.attr.state_focused }, d1);
		addState(new int[] {}, d2);
	}

	public static void create(ImageView imageView, String type) {
		if (TextUtils.isEmpty(type)) {
			type = "0";
		}
		imageView.setTag(type);
		IconStateListDrawable drawable = new IconStateListDrawable();
		drawable.type = type;
		drawable.imageViewCache = new WeakReference<ImageView>(imageView);
		drawable.setImage(imageView, type);
	}

	public static void create(ImageView imageView, String type, boolean enable) {
		if (TextUtils.isEmpty(type)) {
			type = "0";
		}
		imageView.setTag(type);
		getDrawable(imageView, type, enable);
	}

	static Drawable getDrawable(Context ctx, String type, boolean enable) {
		Drawable drawable = null;
		if (doTypeMap.containsKey(type)) {
			try {
				String adjustedType = doTypeMap.get(type);
				String icon = getIconPath(adjustedType, enable);
				InputStream in = ctx.getResources().getAssets().open("images/" + icon);
				drawable = new BitmapDrawable(ctx.getResources(), in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			String iconUrl = getIconPath(type, enable);
			File cacheFile = AQUtility.getCacheFile(AQUtility.getCacheDir(ctx), iconUrl);
			if (cacheFile.exists()) {
				drawable = new BitmapDrawable(ctx.getResources(), cacheFile.getAbsolutePath());
			}
		}
		return drawable;
	}

	static void getDrawable(ImageView imageView, String type, boolean enable) {
		Drawable drawable = null;
		Context ctx = AQUtility.getContext();
		if (doTypeMap.containsKey(type)) {
			try {
				String adjustedType = doTypeMap.get(type);
				String icon = getIconPath(adjustedType, enable);
				InputStream in = ctx.getResources().getAssets().open("images/" + icon);
				drawable = new BitmapDrawable(ctx.getResources(), in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final String iconUrl = getIconPath(type, enable);
		if (drawable == null) {
			File cacheFile = AQUtility.getCacheFile(AQUtility.getCacheDir(ctx), iconUrl);
			if (cacheFile.exists()) {
				try {
					drawable = new BitmapDrawable(ctx.getResources(), cacheFile.getAbsolutePath());
				} catch (Exception ex) {
					ex.printStackTrace();
				} catch (OutOfMemoryError ex) {
					ex.printStackTrace();
				}
			}
		}
		if (drawable == null) {
			BitmapAjaxCallback callback = new BitmapAjaxCallback();
			callback.imageView(imageView).url(iconUrl).type(Bitmap.class).fileCache(true).memCache(true).round(0);
			callback.async(ctx);
		} else {
			imageView.setImageDrawable(drawable);
		}
	}

	void setImage(ImageView imageView, String type) {
		Context ctx = AQUtility.getContext();
		d1 = getDrawable(ctx, type, true);
		d2 = getDrawable(ctx, type, false);
		if (d1 != null && d2 != null) {
			addState(new int[] { android.R.attr.state_pressed }, d1);
			addState(new int[] { android.R.attr.state_focused }, d1);
			addState(new int[] {}, d2);
		} else {
			if (d1 == null) {
				final String iconUrl = getIconPath(type, true);
				getIconFromServer(imageView, iconUrl);
			}
			if (d2 == null) {
				final String iconUrl = getIconPath(type, false);
				getIconFromServer(imageView, iconUrl);
			}
		}
		imageView.setImageDrawable(this);
	}

	static final String getIconPath(String iconType, boolean enabled) {
		StringBuilder sb = new StringBuilder();
		sb.append("dotype_").append(iconType);
		if (enabled) {
			sb.append("_enabled");
		}
		sb.append(".png");
		return sb.toString();
	}

	final void getIconFromServer(final ImageView imageView, String url) {
		final Context ctx = AQUtility.getContext();
		AjaxCallback<Bitmap> callback = new AjaxCallback<Bitmap>() {
			@Override
			public void callback(String url, Bitmap bm, AjaxStatus status) {
				Log.e(TAG, "url:" + url);
				if (bm != null) {
					if (url.endsWith("_enabled.png")) {
						d1 = new BitmapDrawable(ctx.getResources(), bm);
					} else {
						d2 = new BitmapDrawable(ctx.getResources(), bm);
					}
					if (d1 != null && d2 != null) {
						ImageView iv = imageViewCache.get();
						if (iv != null && iv.getTag() != null && type.equals(iv.getTag().toString())) {
							IconStateListDrawable d3 = new IconStateListDrawable(d1, d2);
							iv.setImageDrawable(d3);
						}
					}
				}
			}
		};
		callback.url(url).type(Bitmap.class).fileCache(true);
		callback.async(ctx);
	}

	static HashMap<String, String> doTypeMap;
	static {
		doTypeMap = new HashMap<String, String>();
		// ====this is for performance====
		doTypeMap.put("0", "0");
		doTypeMap.put("1", "1");
		doTypeMap.put("2", "2");
		doTypeMap.put("3", "3");
		doTypeMap.put("4", "4");
		doTypeMap.put("5", "5");
		doTypeMap.put("6", "6");
		doTypeMap.put("7", "7");
		doTypeMap.put("8", "8");
		doTypeMap.put("9", "9");
		doTypeMap.put("10", "10");
		doTypeMap.put("11", "11");
		doTypeMap.put("12", "12");
		doTypeMap.put("13", "13");
		doTypeMap.put("14", "14");
		doTypeMap.put("15", "15");
		doTypeMap.put("50", "50");
		doTypeMap.put("51", "51");
		doTypeMap.put("52", "52");
		doTypeMap.put("53", "53");
		doTypeMap.put("100", "100");
		doTypeMap.put("101", "101");
		doTypeMap.put("102", "102");
		doTypeMap.put("103", "103");
		doTypeMap.put("104", "104");
		doTypeMap.put("105", "105");
		doTypeMap.put("106", "106");
		doTypeMap.put("200", "200");
		doTypeMap.put("250", "250");
		doTypeMap.put("500", "500");
		doTypeMap.put("501", "501");
		doTypeMap.put("1001", "1001");
		doTypeMap.put("1002", "1002");
		doTypeMap.put("1003", "1003");
		doTypeMap.put("1004", "1004");
		doTypeMap.put("1005", "1005");
		doTypeMap.put("2001", "2001");
		doTypeMap.put("2002", "2002");
		doTypeMap.put("2003", "2003");
		doTypeMap.put("2006", "2006");
		doTypeMap.put("2011", "2011");
		doTypeMap.put("2012", "2012");
		doTypeMap.put("2013", "2013");
		doTypeMap.put("2016", "2016");
		doTypeMap.put("2017", "2016");
		doTypeMap.put("2020", "2016");
		doTypeMap.put("2030", "101");
		doTypeMap.put("2035", "104");
		doTypeMap.put("2045", "500");
		doTypeMap.put("2052", "104");
		doTypeMap.put("2062", "500");
		doTypeMap.put("2069", "2069");
		doTypeMap.put("2070", "104");
		doTypeMap.put("2071", "2071");
		doTypeMap.put("2073", "2071");

		doTypeMap.put("99", "99");
		doTypeMap.put("9999", "9999");
		doTypeMap.put("add", "0");
		doTypeMap.put("default", "0");
		doTypeMap.put("gift", "0");
		doTypeMap.put("message", "0");
		doTypeMap.put("post", "0");
		doTypeMap.put("surface", "0");
		doTypeMap.put("track", "0");
	}
}
