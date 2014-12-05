package com.qmusic.common;

import java.util.HashMap;

import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmusic.dal.BDatabaseHelper;
import com.qmusic.uitls.BUtilities;

public final class BUser {
	public static final String FIELD_ID = "id";
	public static final String FIELD_USERNAME = "username";
	public static final String FIELD_PASSWORD = "password";
	public static final String FIELD_TOKEN = "token";
	HashMap<String, Object> userInfo;
	static BUser user;

	private BUser() {
	}

	public void setField(String key, Object value) {
		userInfo.put(key, value);
	}

	public Object getField(String key) {
		return userInfo.get(key);
	}

	public String getString(String key) {
		Object value = userInfo.get(key);
		if (value != null && value instanceof String) {
			return (String) value;
		}
		return null;
	}

	public int getInt(String key) {
		Object value = userInfo.get(key);
		if (value != null && value instanceof Integer) {
			return (Integer) value;
		}
		return 0;
	}

	public void save() {
		BUtilities.setPref(BConstants.PRE_KEY_USER_INFO, BUtilities.objToJsonString(user.userInfo));
	}

	public String getUsername() {
		String username = getString(FIELD_USERNAME);
		if (TextUtils.isEmpty(username)) {
			return "";
		} else {
			return username;
		}
	}

	public void logout() {
		user = null;
		// BUtilities.removePref(BConstants.PRE_KEY_SHOW_TUTORIAL);
		BUtilities.removePref(BConstants.PRE_KEY_USER_INFO);
		BUtilities.removePref(BConstants.PRE_KEY_RUN_COUNT);
		BDatabaseHelper.clearDB();
	}

	public static void init() {
		user = new BUser();
		try {
			ObjectMapper objectMapper = BUtilities.jsonMapper();
			String userInfoStr = BUtilities.getPref(BConstants.PRE_KEY_USER_INFO);
			if (!TextUtils.isEmpty(userInfoStr)) {
				JsonNode jnode = objectMapper.readTree(userInfoStr);
				user.userInfo = BUtilities.jsonMapper().convertValue(jnode, new TypeReference<HashMap<String, Object>>() {
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (user.userInfo == null) {
			user.userInfo = new HashMap<String, Object>();
		}
	}

	public static final BUser getUser() {
		if (user == null) {
			init();
		}
		return user;
	}

	/**
	 * need to call save
	 * 
	 * @param userInfo
	 */
	public static void update(String userInfo) {
		try {
			ObjectMapper objectMapper = BUtilities.jsonMapper();
			if (!TextUtils.isEmpty(userInfo)) {
				JsonNode jnode = objectMapper.readTree(userInfo);
				user.userInfo = BUtilities.jsonMapper().convertValue(jnode, new TypeReference<HashMap<String, Object>>() {
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isLogined() {
		if (user != null) {
			String token = (String) user.getString(FIELD_PASSWORD);
			;
			if (!TextUtils.isEmpty(token)) {
				return true;
			}
		}
		return false;
	}

}
