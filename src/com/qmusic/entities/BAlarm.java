package com.qmusic.entities;

public class BAlarm extends BaseEntity {
	public static int STATUS_SCHEDUE = 0;
	public static int STATUS_SCHEDUING = 1;
	public static int STATUS_DONE = 2;
	public static int STATUS_CANCEL = 3;
	public long id;
	public String title;
	public String subTitle;
	public String options;
	public long time;
	public int status;

}
