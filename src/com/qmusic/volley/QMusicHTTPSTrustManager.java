package com.qmusic.volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.qmusic.uitls.BLog;

public class QMusicHTTPSTrustManager implements X509TrustManager {
	private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	public boolean isClientTrusted(X509Certificate[] chain) {
		return true;
	}

	public boolean isServerTrusted(X509Certificate[] chain) {
		return true;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return _AcceptedIssuers;
	}

	public static SSLContext getSSLContext() {
		SSLContext context = null;
		try {
			context = SSLContext.getInstance("TLS");// or SSL
			TrustManager[] trustManagers = null;
			// Option 1:
			// KeyStore trustStore =
			// KeyStore.getInstance(KeyStore.getDefaultType());
			// // load the CA here
			// trustStore.load(null, null);
			// String algorithm = TrustManagerFactory.getDefaultAlgorithm();
			// TrustManagerFactory tmf =
			// TrustManagerFactory.getInstance(algorithm);
			// tmf.init(trustStore);
			// trustManagers = tmf.getTrustManagers();
			// Option 2:
			trustManagers = new TrustManager[] { new QMusicHTTPSTrustManager() };
			context.init(null, trustManagers, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context;
	}

	public static HostnameVerifier getHostnameVerifier() {
		HostnameVerifier verifier = new HostnameVerifier() {

			@Override
			public boolean verify(String urlHostName, SSLSession session) {
				BLog.d("RequestImageManager", "" + urlHostName + " vs. " + session.getPeerHost());
				return true;
			}

		};
		return verifier;
	}
}