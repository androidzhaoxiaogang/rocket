package fast.rocket.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The Class RocketX509TrustManager.
 */
public class RocketX509TrustManager implements X509TrustManager {

	/** The trust managers. */
	private static TrustManager[] trustManagers;
	
	/** The Constant _AcceptedIssuers. */
	private static final X509Certificate[] acceptedIssuers = new X509Certificate[] {};

	/* (non-Javadoc)
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
	 */
	@Override
	public void checkClientTrusted(
			java.security.cert.X509Certificate[] x509Certificates, String s)
			throws java.security.cert.CertificateException {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	/* (non-Javadoc)
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
	 */
	@Override
	public void checkServerTrusted(
			java.security.cert.X509Certificate[] x509Certificates, String s)
			throws java.security.cert.CertificateException {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	/**
	 * Checks if is client trusted.
	 *
	 * @param chain the chain
	 * @return true, if is client trusted
	 */
	public boolean isClientTrusted(X509Certificate[] chain) {
		return true;
	}

	/**
	 * Checks if is server trusted.
	 *
	 * @param chain the chain
	 * @return true, if is server trusted
	 */
	public boolean isServerTrusted(X509Certificate[] chain) {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return acceptedIssuers;
	}

	/**
	 * Allow all ssl.
	 */
	public static void allowAllSSL() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});

		SSLContext context = null;
		if (trustManagers == null) {
			trustManagers = new TrustManager[] { new RocketX509TrustManager() };
		}

		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(context
				.getSocketFactory());
	}

}
