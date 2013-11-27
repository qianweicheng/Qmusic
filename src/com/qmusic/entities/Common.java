package com.qmusic.entities;

import java.util.Date;

public final class Common extends BaseEntity {
	public long id;
	public int serverId;
	public String category;
	public String name;
	public String option;
	public int state;
	public Date time;
	public Object cacheForOption;
}
