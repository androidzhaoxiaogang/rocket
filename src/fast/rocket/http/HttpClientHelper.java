package fast.rocket.http;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.params.HttpClientParams;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
/**
 * The Class HttpClientHelper.
 */
public class HttpClientHelper {
    
    /** The Constant DEFAULT_MAX_CONNECTIONS. */
    private static final int DEFAULT_MAX_CONNECTIONS = 30;
    
    /** The Constant DEFAULT_SOCKET_TIMEOUT. */
    private static final int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
    
    /** The Constant DEFAULT_SOCKET_BUFFER_SIZE. */
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    
    
    private static DefaultHttpClient sHttpClient = null;

    /**
     * Gets the http client which supporting for both http and https protocols.
     *
     * @return the http client
     */
    public  static HttpClient getHttpClient(String userAgent) {
    	final HttpParams httpParams = new BasicHttpParams();
        //ConnManagerParams.setTimeout(httpParams, 1000);
        //ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));
        //ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
        HttpClientParams.setRedirecting(httpParams, false);
        HttpProtocolParams.setUserAgent(httpParams, "Android client");
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80 , PlainSocketFactory.getSocketFactory()));
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");

            // set up a TrustManager that trusts everything
            try {
                sslContext.init(null,
                        new TrustManager[] { new X509TrustManager() {
                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            @Override
                            public void checkClientTrusted(
                                    X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(
                                    X509Certificate[] certs, String authType) {
                            }
                        } }, new SecureRandom());
            } catch (KeyManagementException e) {
            }
            SSLSocketFactory ssf = new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            schemeRegistry.register(new Scheme("https", 8443, ssf));
        } catch (Exception ex) {
            // do nothing, just keep not crash
        }

        PoolingClientConnectionManager manager = new PoolingClientConnectionManager(schemeRegistry);
        manager.setMaxTotal(DEFAULT_MAX_CONNECTIONS);
        manager.setDefaultMaxPerRoute(15);
        sHttpClient = new DefaultHttpClient(manager, httpParams);
        HttpProtocolParams.setUseExpectContinue(httpParams, false);
        return sHttpClient;
    }
}

