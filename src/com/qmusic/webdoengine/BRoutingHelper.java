package com.qmusic.webdoengine;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;

import com.qmusic.R;
import com.qmusic.activities.BWebActivity;
import com.qmusic.activities.SplashActivity;
import com.qmusic.common.BLocationManager;
import com.qmusic.controls.dialogs.BToast;
import com.qmusic.uitls.BLog;

public final class BRoutingHelper {
	static final String TAG = "BRoutingHelper";
	static final String SCHEME_CALL = "easilydo://call";
	static final String SCHEME_DIRECTION_TO = "easilydo://directionTo";
	static final String SCHEME_MAP = "easilydo://map";
	static final String SCHEME_SMS = "easilydo://sms";
	static final String SCHEME_HTTP = "http://";
	static final String SCHEME_HTTPS = "https://";
	static final String SCHEME_MAILTO = "mailto:";
	static final String SCHEME_MARKET = "market://";
	static final String SCHEME_EASILYDO = "easilydo://easilydo.com";

	public static final boolean process(final FragmentActivity ctx, final String url) {
		if (url == null) {
			return false;
		}
		if (url.startsWith(SCHEME_CALL)) {
			try {
				String telNo = url.substring(SCHEME_CALL.length() + 1);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNo));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				BToast.toast(R.string.has_no_phone_call_client);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_MAP)) {
			String geo = "";
			try {
				geo = url.substring(SCHEME_MAP.length() + 1);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + geo));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				// EdoDialogHelper.toast(R.string.has_no_map_client);
				try {
					String MAPURL_FORMAT = "http://maps.google.com/maps?saddr=%s&z=12&view=map";
					String mapURL = String.format(MAPURL_FORMAT, geo);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapURL));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ctx.startActivity(intent);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_DIRECTION_TO)) {
			try {
				// the geo must be lat,lon. not the name of the address
				String geo = url.substring(SCHEME_DIRECTION_TO.length() + 1);
				Location currentLocation = BLocationManager.getInstance().getLastKnowLocation();
				String geoURL;
				if (currentLocation != null) {
					String URL_FORMAT = "http://maps.google.com/maps?saddr=%s,%s&daddr=%s&z=12&view=map";
					geoURL = String.format(URL_FORMAT, currentLocation.getLatitude(), currentLocation.getLongitude(), geo);
				} else {
					geoURL = "geo:0,0?q=" + geo;
				}
				Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse(geoURL));
				navigation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ctx.startActivity(navigation);
			} catch (ActivityNotFoundException ex) {
				BToast.toast(R.string.has_no_map_client);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_SMS)) {
			try {
				String urlDecoded = URLDecoder.decode(url, "UTF-8");
				String[] sms = urlDecoded.substring(SCHEME_SMS.length() + 1).split(",");
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sms[0]));
				intent.putExtra("sms_body", sms[1]);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				BToast.toast(R.string.has_no_sms_client);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_HTTP) || url.startsWith(SCHEME_HTTPS)) {
			try {
				if (url.endsWith(".mp4") || url.endsWith(".3gp")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(url), "video/*");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					ctx.startActivity(intent);
				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ctx.startActivity(intent);
				}
			} catch (ActivityNotFoundException ex) {
				BToast.toast(R.string.has_no_browser_client);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_MAILTO)) {
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setData(Uri.parse(url));
				intent.putExtra(Intent.EXTRA_SUBJECT, "");
				intent.putExtra(Intent.EXTRA_TEXT, "");
				ctx.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				BToast.toast(R.string.has_no_email_client);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_MARKET)) {
			try {
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ctx.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				BToast.toast("Couldn't launch the market");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (url.startsWith(SCHEME_EASILYDO)) {
			final Uri data = Uri.parse(url);
			return processEasilyDoPage(ctx, data);
		}
		return false;
	}

	public static final boolean processEasilyDoPage(final FragmentActivity ctx, final Uri data) {
		String page = null;
		final List<String> segments = data.getPathSegments();
		final String scheme = data.getScheme();
		if (scheme.startsWith("easilydo")) {
			if (segments.size() > 0) {
				page = segments.get(0);
			}
		} else {
			if (segments.size() > 1) {
				page = segments.get(1);
			}
		}
		Intent intent = null;
		if ("web".equalsIgnoreCase(page)) {
			intent = new Intent(ctx, BWebActivity.class);
			intent.putExtra("url", data.getQueryParameter("url"));
		} else {
			intent = new Intent(ctx, SplashActivity.class);
			intent.putExtra(SplashActivity.RE_LOGIN, true);
		}
		String p = data.getQueryParameter("p");
		if ("true".equals(p)) {
			intent.putExtra("p", p);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ctx.startActivity(intent);
		return true;
	}

	public static void putExtra(final Intent intent, final JSONObject params) {
		Iterator<String> keys = params.keys();
		while (keys.hasNext()) {
			final String key = keys.next();
			if ("callback".equals(key) || "page".equals(key)) {
				continue;
			} else {
				Object value = params.opt(key);
				if (value instanceof String) {
					intent.putExtra(key, (String) value);
				} else if (value instanceof Integer) {
					intent.putExtra(key, (Integer) value);
				} else if (value instanceof Double) {
					intent.putExtra(key, (Double) value);
				} else if (value instanceof Long) {
					intent.putExtra(key, (Long) value);
				} else if (value instanceof Boolean) {
					intent.putExtra(key, (Boolean) value);
				} else if (value instanceof Parcelable) {
					intent.putExtra(key, (Parcelable) value);
				} else {
					BLog.e(TAG, "Unsupport type:" + value.getClass().getSimpleName() + ";value:" + value);
				}
			}
		}
	}
}
