package com.qmusic.volley;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.VolleyLog;
import com.qmusic.BuildConfig;
import com.qmusic.uitls.BLog;
import com.qmusic.volley.cache.disc.impl.ext.DiskLruCache;
import com.qmusic.volley.cache.disc.impl.ext.DiskLruCache.Snapshot;

public class VolleyL2DiskLruCache implements Cache {
	static boolean USE_HTTP_CACHE = true;
	private static int IO_BUFFER_SIZE = 8 * 1024;
	private static final int APP_VERSION = 1;
	private static final int VALUE_COUNT = 1;
	private DiskLruCache mDiskCache;
	private File rootDirectory;
	private int maxCacheSize;

	public VolleyL2DiskLruCache(File rootDirectory, int maxCacheSizeInBytes) {
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
				final InputStream in = snapshot.getInputStream(0);
				if (in != null) {
					Entry entry;
					if (USE_HTTP_CACHE) {
						CountingInputStream cis = new CountingInputStream(in, IO_BUFFER_SIZE);
						CacheHeader cacherHeader = CacheHeader.readHeader(cis);
						byte[] data = streamToBytes(cis, (int) (snapshot.getFile(0).length() - cis.bytesRead));
						entry = cacherHeader.toCacheEntry(data);
					} else {
						entry = new Entry();
						final BufferedInputStream buffIn = new BufferedInputStream(in, IO_BUFFER_SIZE);
						long size = snapshot.getFile(0).length();
						byte[] data = streamToBytes(buffIn, (int) size);
						entry.data = data;
						entry.serverDate = snapshot.getFile(0).lastModified();
						entry.ttl = Long.MAX_VALUE;
						entry.softTtl = Long.MAX_VALUE;
					}
					BLog.d("L2", "L2 Hit:" + key);
					return entry;
				}
			}
		} catch (Exception e) {
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
				if (USE_HTTP_CACHE) {
					CacheHeader cacheHeader = new CacheHeader(key, entry);
					boolean success = cacheHeader.writeHeader(out);
					if (!success) {
						out.close();
						VolleyLog.d("Failed to write header for %s", key);
						throw new IOException();
					}
				}
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

	private static class CountingInputStream extends BufferedInputStream {
		private int bytesRead = 0;

		private CountingInputStream(InputStream in, int bufferSize) {
			super(in, bufferSize);
		}

		@Override
		public int read() throws IOException {
			int result = super.read();
			if (result != -1) {
				bytesRead++;
			}
			return result;
		}

		@Override
		public int read(byte[] buffer, int offset, int count) throws IOException {
			int result = super.read(buffer, offset, count);
			if (result != -1) {
				bytesRead += result;
			}
			return result;
		}
	}

	/**
	 * Handles holding onto the cache headers for an entry.
	 */
	// Visible for testing.
	static class CacheHeader {
		/** Magic number for current version of cache file format. */
		private static final int CACHE_MAGIC = 0x20140623;
		/**
		 * The size of the data identified by this CacheHeader. (This is not
		 * serialized to disk.
		 */
		public long size;

		/** The key that identifies the cache entry. */
		public String key;

		/** ETag for cache coherence. */
		public String etag;

		/** Date of this response as reported by the server. */
		public long serverDate;

		/** TTL for this record. */
		public long ttl;

		/** Soft TTL for this record. */
		public long softTtl;

		/** Headers from the response resulting in this cache entry. */
		public Map<String, String> responseHeaders;

		private CacheHeader() {
		}

		/**
		 * Instantiates a new CacheHeader object
		 * 
		 * @param key
		 *            The key that identifies the cache entry
		 * @param entry
		 *            The cache entry.
		 */
		public CacheHeader(String key, Entry entry) {
			this.key = key;
			this.size = entry.data.length;
			this.etag = entry.etag;
			this.serverDate = entry.serverDate;
			this.ttl = entry.ttl;
			this.softTtl = entry.softTtl;
			this.responseHeaders = entry.responseHeaders;
		}

		/**
		 * Reads the header off of an InputStream and returns a CacheHeader
		 * object.
		 * 
		 * @param is
		 *            The InputStream to read from.
		 * @throws IOException
		 */
		public static CacheHeader readHeader(InputStream is) throws IOException {
			CacheHeader entry = new CacheHeader();
			int magic = readInt(is);
			if (magic != CACHE_MAGIC) {
				// don't bother deleting, it'll get pruned eventually
				throw new IOException();
			}
			entry.key = readString(is);
			entry.etag = readString(is);
			if (entry.etag.equals("")) {
				entry.etag = null;
			}
			entry.serverDate = readLong(is);
			entry.ttl = readLong(is);
			entry.softTtl = readLong(is);
			entry.responseHeaders = readStringStringMap(is);
			return entry;
		}

		/**
		 * Creates a cache entry for the specified data.
		 */
		public Entry toCacheEntry(byte[] data) {
			Entry e = new Entry();
			e.data = data;
			e.etag = etag;
			e.serverDate = serverDate;
			e.ttl = ttl;
			e.softTtl = softTtl;
			e.responseHeaders = responseHeaders;
			return e;
		}

		/**
		 * Writes the contents of this CacheHeader to the specified
		 * OutputStream.
		 */
		public boolean writeHeader(OutputStream os) {
			try {
				writeInt(os, CACHE_MAGIC);
				writeString(os, key);
				writeString(os, etag == null ? "" : etag);
				writeLong(os, serverDate);
				writeLong(os, ttl);
				writeLong(os, softTtl);
				writeStringStringMap(responseHeaders, os);
				os.flush();
				return true;
			} catch (IOException e) {
				VolleyLog.d("%s", e.toString());
				return false;
			}
		}

	}

	/**
	 * Reads the contents of an InputStream into a byte[].
	 * */
	private static byte[] streamToBytes(InputStream in, int length) throws IOException {
		byte[] bytes = new byte[length];
		int count;
		int pos = 0;
		while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
			pos += count;
		}
		if (pos != length) {
			throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
		}
		return bytes;
	}

	private static int read(InputStream is) throws IOException {
		int b = is.read();
		if (b == -1) {
			throw new EOFException();
		}
		return b;
	}

	static void writeInt(OutputStream os, int n) throws IOException {
		os.write((n >> 0) & 0xff);
		os.write((n >> 8) & 0xff);
		os.write((n >> 16) & 0xff);
		os.write((n >> 24) & 0xff);
	}

	static int readInt(InputStream is) throws IOException {
		int n = 0;
		n |= (read(is) << 0);
		n |= (read(is) << 8);
		n |= (read(is) << 16);
		n |= (read(is) << 24);
		return n;
	}

	static void writeLong(OutputStream os, long n) throws IOException {
		os.write((byte) (n >>> 0));
		os.write((byte) (n >>> 8));
		os.write((byte) (n >>> 16));
		os.write((byte) (n >>> 24));
		os.write((byte) (n >>> 32));
		os.write((byte) (n >>> 40));
		os.write((byte) (n >>> 48));
		os.write((byte) (n >>> 56));
	}

	static long readLong(InputStream is) throws IOException {
		long n = 0;
		n |= ((read(is) & 0xFFL) << 0);
		n |= ((read(is) & 0xFFL) << 8);
		n |= ((read(is) & 0xFFL) << 16);
		n |= ((read(is) & 0xFFL) << 24);
		n |= ((read(is) & 0xFFL) << 32);
		n |= ((read(is) & 0xFFL) << 40);
		n |= ((read(is) & 0xFFL) << 48);
		n |= ((read(is) & 0xFFL) << 56);
		return n;
	}

	static void writeString(OutputStream os, String s) throws IOException {
		byte[] b = s.getBytes("UTF-8");
		writeLong(os, b.length);
		os.write(b, 0, b.length);
	}

	static String readString(InputStream is) throws IOException {
		int n = (int) readLong(is);
		byte[] b = streamToBytes(is, n);
		return new String(b, "UTF-8");
	}

	static void writeStringStringMap(Map<String, String> map, OutputStream os) throws IOException {
		if (map != null) {
			writeInt(os, map.size());
			for (Map.Entry<String, String> entry : map.entrySet()) {
				writeString(os, entry.getKey());
				writeString(os, entry.getValue());
			}
		} else {
			writeInt(os, 0);
		}
	}

	static Map<String, String> readStringStringMap(InputStream is) throws IOException {
		int size = readInt(is);
		Map<String, String> result = (size == 0) ? Collections.<String, String> emptyMap() : new HashMap<String, String>(size);
		for (int i = 0; i < size; i++) {
			String key = readString(is).intern();
			String value = readString(is).intern();
			result.put(key, value);
		}
		return result;
	}
}