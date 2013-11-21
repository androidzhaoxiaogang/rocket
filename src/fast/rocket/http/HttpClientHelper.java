package fast.rocket.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

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

    /**
     * Gets the http client which supporting for both http and https protocols.
     *
     * @param userAgent the user agent
     * @return the http client
     */
    public  static HttpClient getHttpClient(String userAgent) {
        final HttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, 1000);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
        HttpClientParams.setRedirecting(httpParams, false);
        HttpProtocolParams.setUserAgent(httpParams, "Android client");
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpProtocolParams.setUserAgent(httpParams, userAgent);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            schemeRegistry.register(new Scheme("https", sf, 443));
        } catch (Exception ex) {
            // do nothing, just keep not crash
        }

        ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        DefaultHttpClient httpClient = new DefaultHttpClient(manager, httpParams);
        return httpClient;
    }

    /**
     * A factory for creating MySSLSocket objects.
     */
    private static class MySSLSocketFactory extends SSLSocketFactory {
        
        /** The ssl context. */
        SSLContext sslContext = SSLContext.getInstance("TLS");

        /**
         * Instantiates a new my ssl socket factory.
         *
         * @param truststore the truststore
         * @throws NoSuchAlgorithmException the no such algorithm exception
         * @throws KeyManagementException the key management exception
         * @throws KeyStoreException the key store exception
         * @throws UnrecoverableKeyException the unrecoverable key exception
         */
        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
        KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] {
                    tm
            }, null);
        }

        /* (non-Javadoc)
         * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
         */
        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        /* (non-Javadoc)
         * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket()
         */
        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}

