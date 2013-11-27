package com.qmusic.entities;

import com.qmusic.uitls.BUtilities;

public class BaseEntity {
	public String toString() {
		return BUtilities.objToJsonString(this);
	};
}
