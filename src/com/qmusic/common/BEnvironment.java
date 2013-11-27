package com.qmusic.common;

public final class BEnvironment {
	public static final int DEBUG = 0;
	public static final int PRODUCTION = 2;
	public static final int BUILD_TYPE_PHONE = 0;
	public static final int BUILD_TYPE_TABLET = 2;
	// =============CONFIG FOR THE BUILD===================
	public static final int BUILD_TYPE = BUILD_TYPE_PHONE;
	public static final int ENV = DEBUG;
	public static final String SERVER_URL = "http://115.28.49.18";
	// ===================================================
	public static final String USER = SERVER_URL + "/php/user.php";
	public static final String BLOOD = SERVER_URL + "/php/blood.php";
	public static final String DATALIB = SERVER_URL + "/php/datalibservice.php";
	public static final String ORDER = SERVER_URL + "/php/orderservice.php";
	public static final String PAPER = SERVER_URL + "/php/paperservice.php";
	public static final String TASK = SERVER_URL + "/php/taskservice.php";
	public static final String DRUG = SERVER_URL + "/php/medicineservice.php";
	public static final String USERMEDICINE = SERVER_URL + "/php/usermedicineservice.php";
	public static final String USER_TASK = SERVER_URL + "/php/usertaskservice.php";
	public static final String USER_MSG = SERVER_URL + "/php/usermsgservice.php";
	public static final String FAMLIYNUMBER = SERVER_URL + "/php/famliynumberservice.php";
	public static final String HOW_TO_GET_CUT = "http://www.baidu.com";

}
