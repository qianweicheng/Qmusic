package com.qmusic.volley;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class QMusicSSLSocketFactory extends SSLSocketFactory {
	public static final String TAG = "QMusicSSLSocketFactory";
	SSLContext sslContext;

	public static final SSLSocketFactory createInstance() {
		SSLSocketFactory sf = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);// load private CA here
			sf = new QMusicSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			e.printStackTrace();
			sf = SSLSocketFactory.getSocketFactory();
		}
		return sf;
	}

	public QMusicSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		TrustManager tm = new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				// BLog.e(TAG, "getAcceptedIssuers");
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				// BLog.e(TAG, "checkClientTrusted");
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
				// BLog.e(TAG, "checkServerTrusted");
			}
		};
		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}

}
