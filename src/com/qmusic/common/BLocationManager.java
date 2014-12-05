package com.qmusic.common;

import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.qmusic.MyApplication;
import com.qmusic.uitls.BLog;

public class BLocationManager implements LocationListener {
	static final String TAG = BLocationManager.class.getSimpleName();
	public static final int STATUS_NOT_DEF = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_DOING = 2;
	public static final int STATUS_DONE = 3;
	public static final int STATUS_FAILED = 4;
	private LocationManager locationManager;
	private String bestProvider = "";
	private Location location;
	private int mState = STATUS_NOT_DEF;
	static private BLocationManager instance;
	private Context mContext;
	WeakHashMap<IAsyncDataCallback, String> callbackList;
	long lastupdateTime;

	private BLocationManager(Context ctx) {
		mContext = ctx;
		locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		callbackList = new WeakHashMap<IAsyncDataCallback, String>();
		List<String> providers = locationManager.getAllProviders();
		if (providers != null) {
			BLog.i(TAG, "AllProviders:" + providers.toString());
		}
		bestProvider = getBestProvider();
		if (!TextUtils.isEmpty(bestProvider)) {
			BLog.i(TAG, "BEST Provider:" + bestProvider);
			location = locationManager.getLastKnownLocation(bestProvider);
			if (location != null) {
				BLog.i(TAG, "LastKnownLocation : " + location.toString());
			}
		} else {
			BLog.e(TAG, "LocationManager init failed. bestProvider = " + bestProvider);
		}
	}

	public static void init(Context c) {
		if (instance == null) {
			instance = new BLocationManager(c);
		}
	}

	public static BLocationManager getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param callback
	 * @param millis
	 *            use cache if lastKnowLocation is in millis
	 */
	public void updateLocation(final IAsyncDataCallback callback, final long millis) {
		if (System.currentTimeMillis() - lastupdateTime <= millis && location != null) {
			MyApplication.post(new Runnable() {

				@Override
				public void run() {
					callback.callback(BConstants.MSG_RESULT_OK, location);
				}
			});
		} else {
			bestProvider = getBestProvider();
			if (TextUtils.isEmpty(bestProvider)) {
				checkCallback(BConstants.MSG_RESULT_FAILED);
				return;
			}
			callbackList.put(callback, "");
			if (mState == STATUS_DOING) {
				return;
			}
			BLog.i(TAG, "updateLocation BEST Provider : " + bestProvider);
			mState = STATUS_DOING;
			MyApplication.postDelayed(timeCheck, 10000);
			BLog.i(TAG, "requestLocationUpdates. bestProvider = " + bestProvider);
			locationManager.requestLocationUpdates(bestProvider, 2000, 500, this);
		}
	}

	public Location getLastKnowLocation() {
		return location;
	}

	/**
	 * format(lat,lng)
	 * 
	 * @return
	 */
	public String getLatLng() {
		String laglng = null;
		if (location != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(location.getLatitude());
			sb.append(",");
			sb.append(location.getLongitude());
			laglng = sb.toString();
		}
		return laglng;
	}

	public void getLocationName(final IAsyncDataCallback callback) {
		final AsyncTask<Void, Void, String[]> asyncTask = new AsyncTask<Void, Void, String[]>() {

			@Override
			protected String[] doInBackground(Void... params) {
				String[] names = getLocationName(location);
				return names;
			}

			@Override
			protected void onPostExecute(String result[]) {
				if (result != null && result.length > 0) {
					callback.callback(BConstants.MSG_RESULT_OK, result);
				} else {
					callback.callback(BConstants.MSG_RESULT_FAILED, null);
				}
			}
		};
		if (location == null) {
			updateLocation(new IAsyncDataCallback() {
				@Override
				public void callback(int result, Object data) {
					if (result == BConstants.MSG_RESULT_OK) {
						asyncTask.execute();
					} else {
						callback.callback(BConstants.MSG_RESULT_FAILED, null);
					}
				}
			}, 0);
		} else {
			asyncTask.execute();
		}
	}

	private String getBestProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String prov = locationManager.getBestProvider(criteria, true);
		return prov;
	}

	Runnable timeCheck = new Runnable() {
		@Override
		public void run() {
			BLog.i(TAG, "UpdateLocation timeout. mState = " + mState);
			if (mState == STATUS_DOING) {
				mState = STATUS_FAILED;
				BLog.i(TAG, "onLocationChanged: location failed");
				checkCallback(BConstants.MSG_RESULT_FAILED);
			}
		}
	};

	private void checkCallback(final int result) {
		Set<IAsyncDataCallback> cs = callbackList.keySet();
		for (IAsyncDataCallback c : cs) {
			try {
				c.callback(result, location);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		callbackList.clear();
	}

	private void stop() {
		mState = STATUS_READY;
		locationManager.removeUpdates(instance);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		if (arg0 == null) {
			mState = STATUS_FAILED;
			BLog.i(TAG, "onLocationChanged: location failed");
			checkCallback(BConstants.MSG_RESULT_FAILED);
		} else {
			mState = STATUS_DONE;
			location = arg0;
			lastupdateTime = System.currentTimeMillis();
			BLog.i(TAG, "onLocationChanged:" + arg0.toString());
			checkCallback(BConstants.MSG_RESULT_OK);
		}
		stop();
	}

	@Override
	public void onProviderDisabled(String provider) {
		BLog.d(TAG, "onProviderDisabled : " + provider);
		if (provider.equals(bestProvider)) {
			// location = null;
			mState = STATUS_FAILED;
			checkCallback(BConstants.MSG_RESULT_FAILED);
			stop();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		BLog.d(TAG, "onProviderEnabled : " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		BLog.d(TAG, "onStatusChanged : " + provider + " : " + status);
		if (provider.equals(bestProvider) && (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE)) {
			BLog.v(TAG, "Provider: " + bestProvider + " become unavailable.");
			// location = null;
			mState = STATUS_FAILED;
			checkCallback(BConstants.MSG_RESULT_FAILED);
			stop();
		}
	}

	private String[] getLocationName(Location location) {
		try {
			Geocoder geocoder = new Geocoder(mContext);
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (addresses != null && addresses.size() > 0) {
				String[] addressStrs = new String[addresses.size()];
				for (int j = 0; j < addresses.size(); j++) {
					Address address = addresses.get(j);
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
						sb.append(address.getAddressLine(i) + " ");
					}
					addressStrs[j] = sb.toString();
				}
				return addressStrs;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String[0];
	}

}
