package com.qmusic.uitls;

import android.util.Log;

public class BLog {
	public static final int ALL = 6;
	public static final int VERBOSE = 5;
	public static final int DEBUG = 4;
	public static final int INFO = 3;
	public static final int WARN = 2;
	public static final int ERROR = 1;
	private static int DEBUG_LEVEL = 6;// set 0 to disable log

	public static void setLevel(int level) {
		DEBUG_LEVEL = level;
	}

	public static int v(String tag, String msg) {
		if (DEBUG_LEVEL > VERBOSE) {
			return Log.v(tag, msg);
		} else {
			return 0;
		}
	}

	public static int v(String tag, String msg, Throwable e) {
		if (DEBUG_LEVEL > VERBOSE) {
			return Log.v(tag, msg, e);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg) {
		if (DEBUG_LEVEL > DEBUG) {
			return Log.d(tag, msg);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg, Throwable e) {
		if (DEBUG_LEVEL > DEBUG) {
			return Log.d(tag, msg, e);
		} else {
			return 0;
		}
	}

	public static int i(String tag, String msg) {
		if (DEBUG_LEVEL > INFO) {
			return Log.i(tag, msg);
		} else {
			return 0;
		}
	}

	public static int i(String tag, String msg, Throwable e) {
		if (DEBUG_LEVEL > INFO) {
			return Log.i(tag, msg, e);
		} else {
			return 0;
		}
	}

	public static int w(String tag, String msg) {
		if (DEBUG_LEVEL > WARN) {
			return Log.w(tag, msg);
		} else {
			return 0;
		}
	}

	public static int w(String tag, String msg, Throwable e) {
		if (DEBUG_LEVEL > WARN) {
			return Log.w(tag, msg, e);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg) {
		if (DEBUG_LEVEL > ERROR) {
			return Log.e(tag, msg);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg, Throwable e) {
		if (DEBUG_LEVEL > ERROR) {
			return Log.e(tag, msg, e);
		} else {
			return 0;
		}
	}

	public static void v(String tag, String s, Object... args) {
		if (DEBUG_LEVEL > VERBOSE)
			Log.v(tag, String.format(s, args));
	}

	/**
	 * Debug log message with printf formatting.
	 * 
	 * @param tag
	 * @param s
	 * @param args
	 */
	public static void d(String tag, String s, Object... args) {
		if (DEBUG_LEVEL > DEBUG)
			Log.d(tag, String.format(s, args));
	}

	/**
	 * Info log message with printf formatting.
	 * 
	 * @param tag
	 * @param s
	 * @param args
	 */
	public static void i(String tag, String s, Object... args) {
		if (DEBUG_LEVEL > INFO)
			Log.i(tag, String.format(s, args));
	}

	/**
	 * Warning log message with printf formatting.
	 * 
	 * @param tag
	 * @param s
	 * @param args
	 */
	public static void w(String tag, String s, Object... args) {
		if (DEBUG_LEVEL > WARN)
			Log.w(tag, String.format(s, args));
	}

	/**
	 * Error log message with printf formatting.
	 * 
	 * @param tag
	 * @param s
	 * @param args
	 */
	public static void e(String tag, String s, Object... args) {
		if (DEBUG_LEVEL > ERROR)
			Log.e(tag, String.format(s, args));
	}
}
