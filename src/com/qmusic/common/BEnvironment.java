package com.qmusic.common;

public final class BEnvironment {
	public static final int DEBUG = 0;
	public static final int PRODUCTION = 2;
	// =============CONFIG FOR THE BUILD===================
	public static final int ENV = DEBUG;
	public static final String SERVER_URL = "http://localhost";
	public static final boolean UI_TEST = false;// only for UI test.
	// ===================================================
	public static final String USERLOGIN = SERVER_URL + "/php/user.php";

}
