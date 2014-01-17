/*
 * 
 */
package fast.rocket;

import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.Build;

/**
 * The Class WrappedResponse to close the https connection after response.
 */
public class WrappedResponse {
	
	/** The http request. */
	public HttpUriRequest httpRequest;
	
	/** The http url connection. */
	public HttpURLConnection connection;
	
	/** The http resonpse. */
	public HttpResponse httpResonpse;
	
	/**
	 * Instantiates a new wrapped response.
	 *
	 * @param httpResponse the http response
	 * @param connection the connection
	 */
	public WrappedResponse (HttpResponse httpResponse,  HttpURLConnection connection) {
		this(httpResponse);
		this.connection = connection;
	}
	
	/**
	 * Instantiates a new wrapped response.
	 *
	 * @param httpResponse the http response
	 * @param uriRequest the uri request
	 */
	public WrappedResponse (HttpResponse httpResponse,  HttpUriRequest uriRequest) {
		this(httpResponse);
		this.httpRequest = uriRequest;
	}
	
	/**
	 * Instantiates a new wrapped response.
	 *
	 * @param httpResponse the http response
	 */
	public WrappedResponse (HttpResponse httpResponse) {
		this.httpResonpse = httpResponse;
	}
	
	/**
	 * Close (e.g., To prevent the SSL handshake issues, when post
	 * the https requests, the close method should be invoked).
	 */
	public void close() {
		if(Build.VERSION.SDK_INT >= 9) {
			if( connection != null) {
				connection.disconnect();
			}
		} else {
			if( httpRequest != null) {
				httpRequest.abort();
			}
		}
	}

}
