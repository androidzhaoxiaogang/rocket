
package fast.rocket.http;



import android.text.TextUtils;

import fast.rocket.Request;
import fast.rocket.Request.Method;
import fast.rocket.WrappedResponse;
import fast.rocket.error.AuthFailureError;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

/**
 * An {@link HttpStack} based on {@link HttpURLConnection}.
 */
public class HurlStack implements HttpStack {
    
    /** The Constant HEADER_CONTENT_TYPE. */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    
    /** The Constant HEADER_SET_COOKIE. */
    private static final String HEADER_SET_COOKIE = "Set-Cookie";
    
    /** The Constant HEADER_COOKIE. */
    private static final String HEADER_COOKIE = "Cookie";
    
    /** The Constant NOT_VERIFY. */
    final static HostnameVerifier NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    
    /** The Constant trustAllCerts. */
    final static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                	return new java.security.cert.X509Certificate[] {};
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    /**
     * An interface for transforming URLs before use.
     */
    public interface UrlRewriter {
        
        /**
         * Returns a URL to use instead of the provided one, or null to indicate
         * this URL should not be used at all.
         *
         * @param originalUrl the original url
         * @return the string
         */
        public String rewriteUrl(String originalUrl);
    }

    /** The m url rewriter. */
    private final UrlRewriter mUrlRewriter;
    
    /** The cookie. */
    private String cookie;

    /**
     * Instantiates a new hurl stack.
     */
    public HurlStack() {
        this(null);
    }

    /**
     * Instantiates a new hurl stack.
     *
     * @param urlRewriter Rewriter to use for request URLs
     */
    public HurlStack(UrlRewriter urlRewriter) {
    	mUrlRewriter = urlRewriter;
    }

    /* (non-Javadoc)
     * @see fast.rocket.http.HttpStack#performRequest(fast.rocket.Request, java.util.Map)
     */
    @Override
    public WrappedResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        String url = request.getUrl();
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(request.getHeaders());
        map.putAll(additionalHeaders);
        if (mUrlRewriter != null) {
            String rewritten = mUrlRewriter.rewriteUrl(url);
            if (rewritten == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
            url = rewritten;
        }
        
        if (null != url && url.startsWith("https")) {
        	trustAllHosts();
        	request.setSSLRequest(true);
        }
        
        URL parsedUrl = new URL(url);
        HttpURLConnection connection = openConnection(parsedUrl, request);
        for (String headerName : map.keySet()) {
            connection.addRequestProperty(headerName, map.get(headerName));
        }
        if (request.isCookieEnabled()) {
            setConnectionCookie(connection, this.cookie);
        }
        setConnectionParametersForRequest(connection, request);
        // Initialize HttpResponse with data from the HttpURLConnection.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            // -1 is returned by getResponseCode() if the response code could not be retrieved.
            // Signal to the caller that something was wrong with the connection.
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromConnection(connection));
        if (request.isCookieEnabled()) {
            storeConnectionCookie(connection, request);
        }
        for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }
        
        //return response;
        return new WrappedResponse(response, connection);
    }

    /**
     * Initializes an {@link HttpEntity} from the given {@link HttpURLConnection}.
     *
     * @param connection the connection
     * @return an HttpEntity populated with data from <code>connection</code>.
     */
    private static HttpEntity entityFromConnection(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return entity;
    }

    /**
     * Create an {@link HttpURLConnection} for the specified {@code url}.
     *
     * @param url the url
     * @return the http url connection
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected HttpURLConnection createConnection(URL url) throws IOException {
    	Proxy proxy = getProxy();
    	if (proxy != null)
            return (HttpURLConnection) url.openConnection(proxy);
        else
        	return (HttpURLConnection) url.openConnection();
    }

    /**
     * Opens an {@link HttpURLConnection} with parameters.
     *
     * @param url the url
     * @param request the request
     * @return an open connection
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private HttpURLConnection openConnection(URL url, Request<?> request) throws IOException {
        HttpURLConnection connection = createConnection(url);

        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        return connection;
    }
    
    /**
     * Trust all hosts.
     */
    private static void trustAllHosts() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(NOT_VERIFY);
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
    }
    
    /**
     * Gets the proxy.
     *
     * @return the proxy
     */
    private static Proxy getProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort))
            return new Proxy(java.net.Proxy.Type.HTTP, 
            		new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
        else
            return null;
    }

    /**
     * Sets the connection cookie.
     *
     * @param connection the connection
     * @param cookie the cookie
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws AuthFailureError the auth failure error
     */
    private void setConnectionCookie(HttpURLConnection connection, String cookie) 
    		throws IOException, AuthFailureError {
        if (!TextUtils.isEmpty(cookie)) {
            connection.setRequestProperty(HEADER_COOKIE, cookie);
        }
    }

    /**
     * Store connection cookie.
     *
     * @param connection the connection
     * @param request the request
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws AuthFailureError the auth failure error
     */
    public void storeConnectionCookie(HttpURLConnection connection, Request<?> request) 
    		throws IOException, AuthFailureError {
        String cookieHeader = connection.getHeaderField(HEADER_SET_COOKIE);
        if (!TextUtils.isEmpty(cookieHeader)) {
        	this.cookie = cookieHeader.substring(0, cookieHeader.indexOf(";"));
        }
    }

    /**
     * Sets the connection parameters for request.
     *
     * @param connection the connection
     * @param request the request
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws AuthFailureError the auth failure error
     */
    @SuppressWarnings("deprecation")
    /* package */ static void setConnectionParametersForRequest(HttpURLConnection connection,
            Request<?> request) throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case Method.DEPRECATED_GET_OR_POST:
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    // Prepare output. There is no need to set Content-Length explicitly,
                    // since this is handled by HttpURLConnection using the size of the prepared
                    // output stream.
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty(HEADER_CONTENT_TYPE,
                            request.getPostBodyContentType());
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.close();
                }
                break;
            case Method.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    /**
     * Adds the body if exists.
     *
     * @param connection the connection
     * @param request the request
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws AuthFailureError the auth failure error
     */
    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request)
            throws IOException, AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}
