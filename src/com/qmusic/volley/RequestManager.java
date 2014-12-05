package com.qmusic.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;

public class RequestManager {
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	private static ImageCache mImageCacheL1;
	private static ImageCache mImageCacheL2;

	/**
	 * Volley recommends in-memory L1 cache but both a disk and memory cache are
	 * provided. Volley includes a L2 disk cache out of the box but you can
	 * technically use a disk cache as an L1 cache provided you can live with
	 * potential i/o blocking.
	 *
	 */
	public enum CacheType {
		DISK, MEMORY, L1L2
	}

	private RequestManager() {
		// no instances
	}

	public static final void init(Context context, String uniqueName, int cacheSize, CompressFormat compressFormat, int quality, CacheType type) {
		switch (type) {
		case DISK:
			mImageCacheL1 = new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
			break;
		case MEMORY:
			mImageCacheL1 = new BitmapLruImageCache(cacheSize);
			break;
		case L1L2: {
			// TODO
			mImageCacheL1 = new BitmapLruImageCache(cacheSize);
			mImageCacheL2 = new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
			break;
		}
		default:
			mImageCacheL1 = new BitmapLruImageCache(cacheSize);
			break;
		}
		mRequestQueue = Volley.newRequestQueue(context);
		mImageLoader = new ImageLoader(mRequestQueue, mImageCacheL1);
	}

	/**
	 * @return instance of the queue
	 * @throws IllegalStatException
	 *             if init has not yet been called
	 */
	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("Not initialized");
		}
	}

	/**
	 * @return instance of the image loader
	 */
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public Bitmap getBitmap(String url) {
		try {
			return mImageCacheL1.getBitmap(createKey(url));
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	public void putBitmap(String url, Bitmap bitmap) {
		try {
			mImageCacheL1.putBitmap(createKey(url), bitmap);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	/**
	 * Executes and image load
	 * 
	 * @param url
	 *            location of image
	 * @param listener
	 *            Listener for completion
	 */
	public void getImage(String url, ImageListener listener) {
		mImageLoader.get(url, listener);
	}

	/**
	 * Creates a unique cache key based on a url value
	 * 
	 * @param url
	 *            url to be used in key creation
	 * @return cache key value
	 */
	private String createKey(String url) {
		return String.valueOf(url.hashCode());
	}
}
