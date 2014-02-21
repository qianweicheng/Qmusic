package com.qmusic.common;

public final class BEnvironment {
	public static final int DEBUG = 0;
	public static final int PRODUCTION = 2;
	public static final int BUILD_TYPE_PHONE = 0;
	public static final int BUILD_TYPE_TABLET = 2;
	// =============CONFIG FOR THE BUILD===================
	public static final int BUILD_TYPE = BUILD_TYPE_PHONE;
	public static final int ENV = DEBUG;
	public static final String SERVER_URL = "http://baidu.com";
	// ===================================================
	public static final String USERLOGIN = SERVER_URL + "/php/user.php";

}
