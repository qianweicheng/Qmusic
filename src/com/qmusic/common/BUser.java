package com.qmusic.common;

import android.text.TextUtils;

import com.androidquery.util.AQUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmusic.R;
import com.qmusic.dal.BDatabaseHelper;
import com.qmusic.uitls.BUtilities;

public final class BUser {
	private String id = "";
	private String username = "";
	private String password = "";
	private String token = "";
	static BUser user;

	public static void init() {
		try {
			ObjectMapper objectMapper = BUtilities.jsonMapper();
			String userInfoStr = BUtilities.getPref(BConstants.PRE_KEY_USER_INFO);
			if (!TextUtils.isEmpty(userInfoStr)) {
				JsonNode jnode = objectMapper.readTree(userInfoStr);
				user = BUtilities.jsonMapper().convertValue(jnode, new TypeReference<BUser>() {
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (user == null) {
			user = new BUser();
		}
	}

	public static void init(String username, String password, String token) {
		user = new BUser();
		user.setUsername(username);
		user.setPassword(password);
		user.setToken(token);
		BUtilities.setPref(BConstants.PRE_KEY_USER_INFO, user.toString());
	}

	public static final BUser getUser() {
		if (user == null) {
			init();
		}
		return user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		if (TextUtils.isEmpty(username)) {
			return AQUtility.getContext().getString(R.string.default_username);
		} else {
			return username;
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public static void setUser(BUser user) {
		BUser.user = user;
	}

	public static boolean isLogined() {
		if (user != null && !TextUtils.isEmpty(user.token)) {
			return true;
		} else {
			return false;
		}
	}

	public void saveUser() {
		BUtilities.setPref(BConstants.PRE_KEY_USER_INFO, toString());
	}

	public void logout() {
		user = null;
		BUtilities.removePref(BConstants.PRE_KEY_SHOW_TUTORIAL);
		BUtilities.removePref(BConstants.PRE_KEY_USER_INFO);
		BUtilities.removePref(BConstants.PRE_KEY_RUN_COUNT);
		BDatabaseHelper.clearDB();
	}

	@Override
	public String toString() {
		return BUtilities.objToJsonString(this);
	}
}
