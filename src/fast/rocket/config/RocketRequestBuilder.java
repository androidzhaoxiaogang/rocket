package fast.rocket.config;

import java.io.File;

import fast.rocket.GsonRequest;
import fast.rocket.Response.ErrorListener;
import fast.rocket.Response.Listener;
import fast.rocket.Rocket;
import fast.rocket.error.RocketError;


// TODO: Auto-generated Javadoc
/**
 * The Class RocketRequestBuilder.
 */
public class RocketRequestBuilder implements LaunchBuilder {
	
	/** The future callback after json string being parsed. */
	@SuppressWarnings("rawtypes")
	private FutureCallback callback ;
	
	/** The class type to be parsed. */
	private Class<?> clazz;
	
	/** The rocket. */
	private Rocket rocket;
	
	/** The request tag. */
	private Object tag;
	

	/**
	 * Instantiates a new rocket request builder.
	 *
	 * @param rocket the rocket
	 */
	public RocketRequestBuilder(Rocket rocket) {
		this.rocket = rocket;
	}
	
	/**
	 * Sets the callback.
	 *
	 * @param callback the callback
	 * @return the rocket request builder
	 */
	@SuppressWarnings("rawtypes")
	public RocketRequestBuilder setCallback(FutureCallback callback) {
		this.callback = callback;
		return this;
	}
	
	/**
	 * Sets the json class type.
	 *
	 * @param clazz the clazz
	 * @return the rocket request builder
	 */
	public RocketRequestBuilder setJsonClass(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	/**
	 * Sets the request tag.
	 *
	 * @param tag the tag
	 * @return the rocket request builder
	 */
	public RocketRequestBuilder setRequestTag(Object tag) {
		this.tag = tag;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.lang.String)
	 */
	@Override
	public void load(String uri) {
		if(clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}
		
		addRequest(uri, clazz);
	}
	
	/**
	 * Sets the request.
	 *
	 * @param <T> the generic type
	 * @param uri the uri
	 * @param clazz the clazz
	 */
	private <T> void addRequest(String uri, Class<T> clazz) {
		GsonRequest<T> request = new GsonRequest<T>(uri, clazz,
				null, new Listener<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(T response) {
				if(callback != null) {
					callback.onCompleted(null, response);
				}
			}
		}, new ErrorListener() {

			@SuppressWarnings("unchecked")
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

	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.lang.String, java.lang.String)
	 */
	@Override
	public void load(String method, String url) {
	}

	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.io.File)
	 */
	@Override
	public void load(File file) {
	}
}
