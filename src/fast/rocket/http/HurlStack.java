
package fast.rocket.http;



import android.text.TextUtils;

import fast.rocket.error.AuthFailureError;
import fast.rocket.request.Request;
import fast.rocket.request.Request.Method;
import fast.rocket.request.filecore.MultiPartRequest;
import fast.rocket.request.filecore.MultiPartRequest.MultiPartParam;


import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
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
import org.apache.http.HttpResponse;
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
    
    private static final String    HEADER_CONTENT_DISPOSITION       = "Content-Disposition";
    private static final String    HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    private static final String    CONTENT_TYPE_MULTIPART           = "multipart/form-data; charset=%s; boundary=%s";
    private static final String    BINARY                           = "binary";
    private static final String    CRLF                             = "\r\n";
    private static final String    FORM_DATA                        = "form-data; name=\"%s\"";
    private static final String    BOUNDARY_PREFIX                  = "--";
    private static final String    CONTENT_TYPE_OCTET_STREAM        = "application/octet-stream";
    private static final String    FILENAME                         = "filename=%s";
    private static final String    COLON_SPACE                      = ": ";
    private static final String    SEMICOLON_SPACE                  = "; ";
    
    /** The Constant HEADER_SET_COOKIE. */
    private static final String HEADER_SET_COOKIE = "Set-Cookie";
    
    /** The Constant HEADER_COOKIE. */
    private static final String HEADER_COOKIE = "Cookie";
    
    /** Not verify the host. */
    final static HostnameVerifier notVerify = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    
    /** Trust all certifications. */
	final static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}
	} };

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
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
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
        
        if (request instanceof MultiPartRequest) {
            setConnectionParametersForMultipartRequest(connection, request, map);
        } else {
        	setConnectionParametersForRequest(connection, request);
        }
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
        
        return response;
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
     * Set trust all https hosts.
     */
    private static void trustAllHosts() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(notVerify);
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
     * Sets the connection cookie if cookie is's empty.
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
     * Store connection cookie if cookie is't empty.
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
     * Perform a multipart request on a connection
     * 
     * @param connection
     *            The Connection to perform the multi part request
     * @param request
     * @param additionalHeaders
     * @param multipartParams
     *            The params to add to the Multi Part request
     * @param filesToUpload
     *            The files to upload
     * @throws ProtocolException
     */
    private static void setConnectionParametersForMultipartRequest(HttpURLConnection connection, Request<?> request, 
    		HashMap<String, String> additionalHeaders) throws IOException, ProtocolException {

        final String charset = ((MultiPartRequest<?>) request)
                .getProtocolCharset();
        final int curTime = (int) (System.currentTimeMillis() / 1000);
        final String boundary = BOUNDARY_PREFIX + curTime;
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(HEADER_CONTENT_TYPE,
                String.format(CONTENT_TYPE_MULTIPART, charset, curTime));
        connection.setChunkedStreamingMode(0);

        Map<String, MultiPartParam> multipartParams = ((MultiPartRequest<?>) request)
                .getMultipartParams();
        Map<String, String> filesToUpload = ((MultiPartRequest<?>) request)
                .getFilesToUpload();
        PrintWriter writer = null;
        OutputStream out = null;
        try {
            addHeadersToConnection(connection, additionalHeaders);
            out = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(out, charset), true);

            for (String key : multipartParams.keySet()) {
                MultiPartParam param = multipartParams.get(key);
                
                writer.append(boundary)
                        .append(CRLF)
                        .append(String.format(HEADER_CONTENT_DISPOSITION
                                + COLON_SPACE + FORM_DATA, key))
                        .append(CRLF)
                        .append(HEADER_CONTENT_TYPE + COLON_SPACE
                                + param.contentType)
                        .append(CRLF)
                        .append(CRLF)
                        .append(param.value)
                        .append(CRLF)
                        .flush();
            }

            for (String key : filesToUpload.keySet()) {

                File file = new File(filesToUpload.get(key));
                
                if(!file.exists()) {
                    throw new IOException(String.format("File not found: %s", file.getAbsolutePath()));
                }
                
                if(file.isDirectory()) {
                    throw new IOException(String.format("File is a directory: %s", file.getAbsolutePath()));
                }

                writer.append(boundary)
                        .append(CRLF)
                        .append(String.format(HEADER_CONTENT_DISPOSITION
                                + COLON_SPACE + FORM_DATA + SEMICOLON_SPACE
                                + FILENAME, key, file.getName()))
                        .append(CRLF)
                        .append(HEADER_CONTENT_TYPE + COLON_SPACE
                                + CONTENT_TYPE_OCTET_STREAM)
                        .append(CRLF)
                        .append(HEADER_CONTENT_TRANSFER_ENCODING + COLON_SPACE
                                + BINARY)
                        .append(CRLF)
                        .append(CRLF)
                        .flush();

                BufferedInputStream input = null;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    input = new BufferedInputStream(fis);
                    int bufferLength = 0;

                    byte[] buffer = new byte[1024];
                    while ((bufferLength = input.read(buffer)) > 0) {
                        out.write(buffer, 0, bufferLength);
                    }
                    out.flush(); // Important! Output cannot be closed. Close of
                                 // writer will close
                                 // output as well.
                } finally {
                    if (input != null)
                        try {
                            input.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                }
                writer.append(CRLF).flush(); // CRLF is important! It indicates
                                             // end of binary
                                             // boundary.
            }

            // End of multipart/form-data.
            writer.append(boundary + BOUNDARY_PREFIX).append(CRLF).flush();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                writer.close();
            }
            
            if(out != null) {
        		out.close();
        	}
        }
    }
    
    /**
     * Add headers and user agent to an {@code }
     * 
     * @param connection
     *            The {@linkplain HttpURLConnection} to add request headers to
     * @param userAgent
     *            The User Agent to identify on server
     * @param additionalHeaders
     *            The headers to add to the request
     */
    private static void addHeadersToConnection(HttpURLConnection connection,
    		Map<String, String> additionalHeaders) {

        for (String headerName : additionalHeaders.keySet()) {
            connection.addRequestProperty(headerName,
                    additionalHeaders.get(headerName));
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
