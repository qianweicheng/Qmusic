package com.qmusic.volley;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.cache.disc.impl.ext.DiskLruCache;
import com.android.volley.cache.disc.impl.ext.DiskLruCache.Snapshot;
import com.qmusic.BuildConfig;
import com.qmusic.uitls.BLog;

public class VolleyDiskLruImageCache implements Cache {
	private static int IO_BUFFER_SIZE = 8 * 1024;
	private static final int APP_VERSION = 1;
	private static final int VALUE_COUNT = 1;
	private DiskLruCache mDiskCache;
	private File rootDirectory;
	private int maxCacheSize;

	public VolleyDiskLruImageCache(File rootDirectory, int maxCacheSizeInBytes) {
		this.rootDirectory = rootDirectory;
		this.maxCacheSize = maxCacheSizeInBytes;
	}

	@Override
	public void initialize() {
		try {
			mDiskCache = DiskLruCache.open(rootDirectory, APP_VERSION, VALUE_COUNT, maxCacheSize, Integer.MAX_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Entry get(String key) {
		Snapshot snapshot = null;
		try {
			String k = getFilenameForKey(key);
			snapshot = mDiskCache.get(k);
			if (snapshot != null) {
				Entry entry = new Entry();
				final InputStream in = snapshot.getInputStream(0);
				if (in != null) {
					final BufferedInputStream buffIn = new BufferedInputStream(in, IO_BUFFER_SIZE);
					long size = snapshot.getFile(0).length();
					byte[] data = new byte[(int) size];
					buffIn.read(data);
					entry.data = data;
					entry.ttl = Long.MAX_VALUE;
					entry.softTtl = Long.MAX_VALUE;
					BLog.d("L2", "L2 Hit:" + key);
					return entry;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}
		BLog.d("L2", "L2 Missed:" + key);
		return null;
	}

	@Override
	public void put(String key, Entry entry) {
		DiskLruCache.Editor editor = null;
		try {
			String k = getFilenameForKey(key);
			editor = mDiskCache.edit(k);
			if (editor == null) {
				return;
			}

			OutputStream out = null;
			try {
				out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
				IOUtils.write(entry.data, out);
				mDiskCache.flush();
				editor.commit();
				if (BuildConfig.DEBUG) {
					Log.d("L2", "image put on disk cache " + key);
				}
			} catch (Exception ex) {
				editor.abort();
				if (BuildConfig.DEBUG) {
					Log.d("L2", "ERROR on: image put on disk cache " + key);
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}

		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.d("L2", "ERROR on: image put on disk cache " + key);
			}
			try {
				if (editor != null) {
					editor.abort();
				}
			} catch (IOException ignored) {
			}
		}

	}

	@Override
	public void invalidate(String key, boolean fullExpire) {

	}

	@Override
	public void remove(String key) {
		if (BuildConfig.DEBUG) {
			Log.d("L2", "disk cache REMOVE " + key);
		}
		try {
			mDiskCache.remove(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() {
		if (BuildConfig.DEBUG) {
			Log.d("L2", "disk cache CLEARED");
		}
		try {
			mDiskCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a pseudo-unique filename for the specified cache key.
	 * 
	 * @param key
	 *            The key to generate a file name for.
	 * @return A pseudo-unique filename.
	 */
	private String getFilenameForKey(String key) {
		int firstHalfLength = key.length() / 2;
		String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
		localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
		return localFilename;
	}
}