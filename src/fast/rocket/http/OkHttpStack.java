package fast.rocket.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.squareup.okhttp.OkHttpClient;

public class OkHttpStack extends HurlStack {
	private final OkHttpClient client;

	public OkHttpStack() {
		this(new OkHttpClient());
	}

	public OkHttpStack(OkHttpClient client) {
		if (client == null) {
			throw new NullPointerException("Client must not be null.");
		}
		client.setSslSocketFactory(getSslSocketFactory());
		this.client = client;
	}

	@Override
	protected HttpURLConnection createConnection(URL url) throws IOException {
		return client.open(url);
	}
	
	private static SSLSocketFactory getSslSocketFactory() {
		try {
			// Construct SSLSocketFactory that accepts any cert.
			SSLContext context = SSLContext.getInstance("TLS");
			TrustManager permissive = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			context.init(null, new TrustManager[] { permissive }, null);
			return context.getSocketFactory();
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
}
