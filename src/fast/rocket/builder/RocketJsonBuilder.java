package fast.rocket.builder;

import java.util.Map;

import android.content.Context;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.error.RocketError;
import fast.rocket.request.JsonRequest;
import fast.rocket.request.Request.Method;
import fast.rocket.response.JsonCallback;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

/**
 * The Class RocketJsonBuilder.
 */
public class RocketJsonBuilder implements JsonBuilder {

	/**
	 * The future callback to be invoked after the json string being parsed.
	 **/

	@SuppressWarnings("rawtypes")
	private JsonCallback callback;

	/** The class type to be parsed. */
	private Class<?> clazz;

	/** Http post or put params. */
	private Map<String, String> params;

	/** Http headers. */
	private Map<String, String> headers;

	/** The rocket instance. */
	private Rocket rocket;

	/** The request tag. */
	private Object tag;

	/** The enable cookie tag. */
	private boolean cookieEnable;

	/** The cache policy. */
	private CachePolicy cachePolicy;

	private String uri;

	// private File file;

	private int method;

	public RocketJsonBuilder(Context context, Rocket rocket, String uri) {
		this.rocket = rocket;
		this.uri = uri;
	}

	/**
	 * Instantiates a new rocket request builder.
	 * 
	 * @param rocket
	 *            the rocket
	 * @param clazz
	 *            the clazz
	 */
	public RocketJsonBuilder(Rocket rocket, Class<?> clazz, String uri, int method) {
		this.rocket = rocket;
		this.clazz = clazz;
		this.uri = uri;
		this.method = method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fast.rocket.builder.JsonBuilder#invoke(fast.rocket.response.JsonCallback)
	 */
	@Override
	public RocketJsonBuilder invoke(JsonCallback<?> callback) {
		this.callback = callback;
		addRequest(method, uri, clazz);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fast.rocket.builder.CacheBuilder#cachePolicy(fast.rocket.cache.CachePolicy
	 * )
	 */
	@Override
	public RocketJsonBuilder cachePolicy(CachePolicy cachePolicy) {
		this.cachePolicy = cachePolicy;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fast.rocket.builder.JsonBuilder#requestTag(java.lang.Object)
	 */
	@Override
	public RocketJsonBuilder requestTag(Object tag) {
		this.tag = tag;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fast.rocket.builder.JsonBuilder#enableCookie(boolean)
	 */
	@Override
	public RocketJsonBuilder enableCookie(boolean enable) {
		this.cookieEnable = enable;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fast.rocket.builder.JsonBuilder#requestParams(java.util.Map)
	 */
	@Override
	public RocketJsonBuilder requestParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fast.rocket.builder.JsonBuilder#requestHeaders(java.util.Map)
	 */
	@Override
	public RocketJsonBuilder requestHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	/**
	 * Adds the json request.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param method
	 *            the method
	 * @param uri
	 *            the uri
	 * @param clazz
	 *            the clazz
	 */
	private <T> void addRequest(int method, String uri, Class<T> clazz) {
		if (clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}

		if (params != null && method == Method.GET) {
			method = Method.POST;// reset the http method
		}

		JsonRequest<T> request = new JsonRequest<T>(method, uri, clazz,
				headers, params, new Listener<T>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onResponse(T response) {
						if (callback != null) {
							callback.onCompleted(null, response);
						}
					}
				}, new ErrorListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onErrorResponse(RocketError error) {
						if (callback != null) {
							callback.onCompleted(error, null);
						}
					}
				});

		if (tag != null)
			request.setTag(tag);
		request.setCookieEnable(cookieEnable);
		request.setCachePolicy(cachePolicy);
		rocket.getRequestQueue().add(request);
	}

}
