package fast.rocket.builder;

import java.io.File;
import java.util.Map;

import android.text.TextUtils;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.error.RocketError;
import fast.rocket.request.JsonRequest;
import fast.rocket.request.Request.Method;
import fast.rocket.response.JsonCallback;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

public class RocketJsonBuilder implements JsonBuilder<RocketJsonBuilder>, 
	CacheBuilder<RocketJsonBuilder>, LoadBuilder<RocketJsonBuilder>{
	
	/** The future callback to be invoked after
	 *  the json string being parsed. 
	 **/
	@SuppressWarnings("rawtypes")
	private JsonCallback callback ;
	
	/** The class type to be parsed. */
	private Class<?> clazz;
	
	/** Http post or put params. */
	private Map<String, String> params;
	
	/** Http headers. */
	private Map<String, String> headers;
	
	private String url;
	
	private int method;
	
	/** The rocket instance. */
	private Rocket rocket;
	
	/** The request tag. */
	private Object tag;

    /** The enable cookie tag. */
    private boolean cookieEnable;

    private CachePolicy cachePolicy;
    
    
	/**
	 * Instantiates a new rocket request builder.
	 *
	 * @param rocket the rocket
	 */
	public RocketJsonBuilder(Rocket rocket, Class<?> clazz) {
		this.rocket = rocket;
		this.clazz = clazz;
	}
	
	@Override
	public RocketJsonBuilder skipMemoryCache(boolean skipMemoryCache) {
		return this;
	}

	@Override
	public RocketJsonBuilder skipDiskCache(boolean skipDiskCache) {
		return this;
	}

	@Override
	public RocketJsonBuilder load(File file) {
		return this;
	}

	@Override
	public RocketJsonBuilder load(String uri) {
		load(Method.POST, uri);
		return this;
	}

	@Override
	public RocketJsonBuilder load(int method, String url) {
		if(TextUtils.isEmpty(url)) {
			throw new IllegalArgumentException("Request url is null");
		}
		
		this.url = url;
		this.method = method;
		return this;
	}

	@Override
	public RocketJsonBuilder invoke(JsonCallback<?> callback) {
		this.callback = callback;
		addRequest(method, url, clazz);
		return this;
	}

	@Override
	public RocketJsonBuilder cachePolicy(CachePolicy cachePolicy) {
		this.cachePolicy = cachePolicy;
		return this;
	}

	@Override
	public RocketJsonBuilder requestTag(Object tag) {
		this.tag = tag;
		return this;
	}

	@Override
	public RocketJsonBuilder enableCookie(boolean enable) {
		this.cookieEnable = enable;
		return this;
	}

	@Override
	public RocketJsonBuilder requestParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	@Override
	public RocketJsonBuilder requestHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	private <T> void addRequest(int method, String uri, Class<T> clazz) {
		if(clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}
		
		if(params != null && method == Method.GET) {
			method = Method.POST;//reset the http method
		}
		
		JsonRequest<T> request = new JsonRequest<T>(method, uri, clazz,
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
        request.setCookieEnable(cookieEnable);
        request.setCacheStrategy(cachePolicy);
		rocket.getRequestQueue().add(request);
	}
	
}
