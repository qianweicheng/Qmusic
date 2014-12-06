package com.qmusic.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.qmusic.uitls.BLog;

public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache {

	public BitmapLruImageCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap result = get(url);
		if (result != null) {
			BLog.d("L1", "L1 Hit:" + url);
		} else {
			BLog.d("L1", "L1 Missed:" + url);
		}
		return result;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		BLog.d("L1", "L1 add:" + url);
		put(url, bitmap);
	}
}