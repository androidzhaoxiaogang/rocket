package fast.rocket.http;

import java.net.HttpURLConnection;


import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;

public class WrappedResponse {
	
	public  HttpResponse httpResponse;
	public  HttpURLConnection connection;
	public  HttpUriRequest uriRequest;
	
	
	public WrappedResponse (HttpResponse httpResponse,  HttpURLConnection connection) {
		this(httpResponse);
		this.connection = connection;
	}
	
	public WrappedResponse (HttpResponse httpResponse,  HttpUriRequest uriRequest) {
		this(httpResponse);
		this.uriRequest = uriRequest;
	}
	
	public WrappedResponse (HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	
	public void abort() {
		if(uriRequest != null) {
			if(uriRequest instanceof HttpPost || uriRequest instanceof HttpGet) {
				((HttpPost) uriRequest).abort();
			}
		} 
	}
	
	public void close() {
		if(uriRequest != null) {
			if(uriRequest instanceof HttpPost || uriRequest instanceof HttpGet) {
				System.out.println("==========close()  close()  close()=======");
				((HttpPost) uriRequest).releaseConnection();
			}
		} else if(connection != null){
			System.out.println("==========close()  connection()  connection()=======");
			connection.disconnect();
		}
	}
}
