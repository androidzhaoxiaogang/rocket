package fast.rocket;

import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.Build;

public class WrappedResponse {
	public HttpUriRequest httpRequest;
	public HttpURLConnection connection;
	public HttpResponse httResonpse;
	
	public WrappedResponse (HttpResponse httpResponse,  HttpURLConnection connection) {
		this(httpResponse);
		this.connection = connection;
	}
	
	public WrappedResponse (HttpResponse httpResponse,  HttpUriRequest uriRequest) {
		this(httpResponse);
		this.httpRequest = uriRequest;
	}
	
	public WrappedResponse (HttpResponse httpResponse) {
		this.httResonpse = httpResponse;
	}
	
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
