package com.qmusic.common;

import org.json.JSONObject;

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
	private String account = "";
	private String username = "";
	private String password = "";
	private String token = "";
	private String age = "";
	private String sex = "";
	private double discount;
	private double alldiscount;
	private int taskcompletecount;
	private int taskcount;
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

	public static void init(JSONObject object, String password, String token) {
		JSONObject data = object.optJSONObject("data");
		user = new BUser();
		user.setId(data.optString("userid"));
		user.setAccount(data.optString("account"));
		// TODO: encode password
		// Base64.encodeToString(password.getBytes(), 0);
		user.setPassword(password);
		user.setUsername(data.optString("guestname"));
		user.setSex(data.optString("sex"));
		user.setAge(data.optString("age"));
		user.setToken(token);
		user.setDiscount(object.optDouble("discount"));
		user.setAlldiscount(object.optDouble("alldiscount"));
		user.setTaskcompletecount(object.optInt("taskcompletecount"));
		user.setTaskcount(object.optInt("taskcount"));
		BUtilities.setPref(BConstants.PRE_KEY_USER_INFO, user.toString());
	}

	public static void update(JSONObject object) {
		JSONObject data = object.optJSONObject("data");
		user.setId(data.optString("userid"));
		user.setAccount(data.optString("account"));
		user.setUsername(data.optString("guestname"));
		user.setSex(data.optString("sex"));
		user.setAge(data.optString("age"));
		user.setDiscount(object.optDouble("discount"));
		user.setAlldiscount(object.optDouble("alldiscount"));
		user.setTaskcompletecount(object.optInt("taskcompletecount"));
		user.setTaskcount(object.optInt("taskcount"));
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getAlldiscount() {
		return alldiscount;
	}

	public void setAlldiscount(double alldiscount) {
		this.alldiscount = alldiscount;
	}

	public int getTaskcompletecount() {
		return taskcompletecount;
	}

	public void setTaskcompletecount(int taskcompletecount) {
		this.taskcompletecount = taskcompletecount;
	}

	public int getTaskcount() {
		return taskcount;
	}

	public void setTaskcount(int taskcount) {
		this.taskcount = taskcount;
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

	public void logout() {
		user = null;
		// BUtilities.removePref(BConstants.PRE_KEY_SHOW_TUTORIAL);
		BUtilities.removePref(BConstants.PRE_KEY_USER_INFO);
		BUtilities.removePref(BConstants.PRE_KEY_RUN_COUNT);
		BUtilities.removePref(BConstants.PRE_KEY_ORDER_USERNAME);
		BUtilities.removePref(BConstants.PRE_KEY_ORDER_ADDRESS);
		BUtilities.removePref(BConstants.PRE_KEY_ORDER_PHONE);
		BUtilities.removePref(BConstants.PRE_KEY_ORDER_ZIP);
		BDatabaseHelper.clearDB();
	}

	@Override
	public String toString() {
		return BUtilities.objToJsonString(this);
	}
}
