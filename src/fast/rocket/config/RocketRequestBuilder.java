package fast.rocket.config;

import java.io.File;

import fast.rocket.GsonRequest;
import fast.rocket.Response.ErrorListener;
import fast.rocket.Response.Listener;
import fast.rocket.Rocket;
import fast.rocket.error.RocketError;

public class RocketRequestBuilder implements LaunchBuilder {
	private FutureCallback callback;
	private Class<?> clazz;
	private Rocket rocket;
	private Object tag;
	

	public RocketRequestBuilder(Rocket rocket) {
		this.rocket = rocket;
	}
	
	public void setCallback(FutureCallback callback) {
		this.callback = callback;
	}
	
	public void setJsonClass(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	@Override
	public void launch(String uri) {
		if(clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}
		
		setRequest(uri, clazz);
	}
	
	private <T> void setRequest(String uri, Class<T> clazz) {
		GsonRequest<T> request = new GsonRequest<T>(uri, clazz,
				null, new Listener<T>() {

			@Override
			public void onResponse(T response) {
				if(callback != null) {
					callback.onCompleted(null, response);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(RocketError error) {
				if(callback != null) {
					callback.onCompleted(error, null);
				}
			}
		});
		
		if(tag != null) request.setTag(tag);
		rocket.getRequestQueue().add(request);
	}

	@Override
	public void launch(String method, String url) {
	}

	@Override
	public void launch(File file) {
	}
}
