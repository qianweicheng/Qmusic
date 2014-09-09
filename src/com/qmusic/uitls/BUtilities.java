package com.qmusic.uitls;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.androidquery.util.AQUtility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.qmusic.common.BConstants;
import com.qmusic.controls.dialogs.BToast;

public class BUtilities {
	static final String TAG = BUtilities.class.getSimpleName();
	private static ObjectMapper mapper;
	protected volatile static UUID uuid;

	public final static ObjectMapper jsonMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
			// skip null values
			mapper.setSerializationInclusion(Include.NON_NULL);
			// skip null values in map
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			// skip unkown properties in JSON when converting to POJO
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}

		return mapper;
	}

	public final static String objToJsonString(Object o) {
		String str;
		try {
			if (mapper == null) {
				jsonMapper();
			}
			str = mapper.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
			str = "null";
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			str = "null";
		}
		return str;
	}

	public final static String getPref(String key) {
		SharedPreferences myPrefs = AQUtility.getContext().getSharedPreferences("BPref", Context.MODE_PRIVATE);
		return myPrefs.getString(key, "");
	}

	public final static void setPref(String key, String value) {
		SharedPreferences myPrefs = AQUtility.getContext().getSharedPreferences("BPref", Context.MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = myPrefs.edit();
		preferencesEditor.putString(key, value);
		preferencesEditor.commit();
	}

	public final static void removePref(String key) {
		SharedPreferences myPrefs = AQUtility.getContext().getSharedPreferences("BPref", Context.MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = myPrefs.edit();
		preferencesEditor.remove(key);
		preferencesEditor.commit();
	}

	public final static boolean isEmail(String email) {
		Pattern pattern = Pattern.compile("^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public static final void sendEmail(FragmentActivity ctx, String to, String subject, String body) {
		try {
			Intent data = new Intent(Intent.ACTION_SEND);
			data.setType("message/rfc822");
			// Intent data = new Intent(Intent.ACTION_SENDTO);
			// data.setData(Uri.parse("mailto:"));
			if (!TextUtils.isEmpty(to)) {
				data.putExtra(Intent.EXTRA_EMAIL, to);
			}
			data.putExtra(Intent.EXTRA_SUBJECT, subject);
			data.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
			ctx.startActivity(data);
		} catch (ActivityNotFoundException ex) {
			BToast.toast("本设备未安装邮箱客户端");
		}
	}

	public final static String getAppVersion() {
		try {
			Context ctx = AQUtility.getContext();
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			return String.format("%s (%s)", pInfo.versionName, pInfo.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
			return "0.0.0";
		}
	}

	public final static String getChannelNo(Context context) {
		String appKey = "";
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai && null != ai.metaData) {
				appKey = ai.metaData.getString("channel");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appKey;
	}

	public final static int getResId(String variableName, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(variableName);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public final static String dateLongToString(long mills) {
		SimpleDateFormat sdfLong = new SimpleDateFormat(BConstants.DEFAULT_DATE_FORMAT, Locale.CHINA);
		Date date = new Date(mills);
		return sdfLong.format(date);
	}

	public final static String dateShortString(long mills) {
		SimpleDateFormat sdfShort = new SimpleDateFormat(BConstants.SHORT_DATE_FORMAT, Locale.CHINA);
		Date date = new Date(mills);
		return sdfShort.format(date);
	}

	public final static String toUpperCaseFirstOne(String s) {
		if (TextUtils.isEmpty(s) || Character.isUpperCase(s.charAt(0))) {
			return s;
		} else {
			char[] chars = s.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new String(chars);
		}
	}

	public static final String getAuthorityFromPermission(Context context, String permission) {
		if (TextUtils.isEmpty(permission)) {
			return null;
		}
		List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packs == null) {
			return null;
		}
		for (PackageInfo pack : packs) {
			ProviderInfo[] providers = pack.providers;
			if (providers != null) {
				for (ProviderInfo provider : providers) {
					// if (!TextUtils.isEmpty(provider.readPermission)) {
					// Log.i("ShortCutHelper_R:", provider.readPermission);
					// }
					// if (!TextUtils.isEmpty(provider.writePermission)) {
					// Log.i("ShortCutHelper_W:", provider.writePermission);
					// }
					if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
						return provider.authority;
					}
				}
			}
		}
		return null;
	}

	public final static String getCookieFromHeader(List<Header> headers) {
		String token = "";
		for (Header header : headers) {
			if ("Set-Cookie".equals(header.getName())) {
				String value = header.getValue();
				token = value.split(";")[0];
				break;
			}
			// BLog.i(header.getName(), header.getValue());
		}
		return token;
	}

	// Refer to http://blog.csdn.net/billpig/article/details/6728573
	public final static String getDeviceId() {
		Context ctx = AQUtility.getContext();
		if (uuid == null) {
			final String id = getPref(BConstants.PREFS_DEVICE_ID);
			if (!TextUtils.isEmpty(id)) {
				// Use the ids previously computed and stored in the prefs file
				uuid = UUID.fromString(id);
			} else {
				final String androidId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);

				// Use the Android ID unless it's broken, in which case fallback
				// on deviceId,
				// unless it's not available, then fallback on a random number
				// which we store
				// to a prefs file
				try {
					if (!"9774d56d682e549c".equals(androidId)) {
						uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
					} else {
						final String deviceId = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
						uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
					}
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				// Write the value out to the prefs file
				setPref(BConstants.PREFS_DEVICE_ID, uuid.toString());
			}
		}
		return uuid.toString();
	}

	public final static int[] getScreenSize(Context ctx) {
		int[] screenSize = new int[2];
		try {
			DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
			screenSize[0] = displayMetrics.widthPixels < displayMetrics.heightPixels ? displayMetrics.widthPixels : displayMetrics.heightPixels;
			screenSize[1] = displayMetrics.widthPixels > displayMetrics.heightPixels ? displayMetrics.widthPixels : displayMetrics.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenSize;
	}

	public final static int[] getScreenSize(Activity ctx) {
		int[] screenSize = new int[2];
		DisplayMetrics displayMetrics = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenSize[0] = displayMetrics.widthPixels;
		screenSize[1] = displayMetrics.heightPixels;
		return screenSize;
	}

	public static final RunningAppProcessInfo getCurProcess(Context context) {
		final int pid = android.os.Process.myPid();
		final ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();
		RunningAppProcessInfo result = null;
		for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
			if (appProcess.pid == pid) {
				result = appProcess;
				break;
			}
		}
		return result;
	}
}
