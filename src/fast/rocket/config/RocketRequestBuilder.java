package fast.rocket.config;

import java.io.File;
import java.util.Map;

import fast.rocket.GsonRequest;
import fast.rocket.Request.Method;
import fast.rocket.Response.ErrorListener;
import fast.rocket.Response.Listener;
import fast.rocket.Rocket;
import fast.rocket.error.RocketError;


/**
 * The Class RocketRequestBuilder.
 */
public class RocketRequestBuilder implements LaunchBuilder {
	
	/** The future callback to be invoked after
	 *  the json string being parsed. 
	 *  */
	@SuppressWarnings("rawtypes")
	private FutureCallback callback ;
	
	/** The class type to be parsed. */
	private Class<?> clazz;
	
	/** Http post or put params. */
	private Map<String, String> params;
	
	/** Http headers. */
	private Map<String, String> headers;
	
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
	
	/**
	 * Sets the request params.
	 *
	 * @param params the params
	 * @return the rocket request builder
	 */
	public RocketRequestBuilder setRequestParams(Map<String, String> params) {
		this.params = params;
		return this;
	}
	
	/**
	 * Sets the request headers.
	 *
	 * @param headers the headers
	 * @return the rocket request builder
	 */
	public RocketRequestBuilder setRequestHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.lang.String)
	 */
	@Override
	public void load(String uri) {
		load(Method.GET, uri);
	}
	
	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.lang.String, java.lang.String)
	 */
	@Override
	public void load(int method, String uri) {
		if(clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}
		
		addRequest(method, uri, clazz);
	}

	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.io.File)
	 */
	@Override
	public void load(File file) {
	}
	
	//***************************private apis***************************************//
	/**
	 * Add the request to the queue.
	 *
	 * @param <T> the generic type
	 * @param uri the uri
	 * @param clazz the clazz
	 */
	private <T> void addRequest(int method, String uri, Class<T> clazz) {
		if(params != null && method == Method.GET) {
			method = Method.POST;//reset the http method
		}
		
		GsonRequest<T> request = new GsonRequest<T>(method, uri, clazz,
				headers, params, new Listener<T>() {

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
}
