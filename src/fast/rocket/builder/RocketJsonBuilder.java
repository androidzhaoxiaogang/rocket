package fast.rocket.builder;

<<<<<<< HEAD
import java.util.Map;

import android.text.TextUtils;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.cache.DiskCacheStrategy;
import fast.rocket.error.RocketError;
import fast.rocket.request.JsonRequest;
import fast.rocket.request.Request.Method;
import fast.rocket.response.JsonCallback;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

public class RocketJsonBuilder implements CacheBuilder{
	
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
	
	/** The rocket instance. */
	private Rocket rocket;
	
	/** The request tag. */
	private Object tag;

    /** The enable cookie tag. */
    private boolean isCookieEnabled;

    /** The Cache Strategy. */
    private DiskCacheStrategy cacheStrategy;
    
	/**
	 * Instantiates a new rocket request builder.
	 *
	 * @param rocket the rocket
	 */
	public RocketJsonBuilder(Rocket rocket) {
		this.rocket = rocket;
	}
	
	/**
	 * Sets the callback.
	 *
	 * @param callback the callback
	 * @return the rocket request builder
	 */
	@SuppressWarnings("rawtypes")
	public RocketJsonBuilder invoke(JsonCallback callback) {
		this.callback = callback;
		return this;
	}
	
	/**
	 * Sets the json parsed class type. The gson parser will put the result to the class object.
	 *
	 * @param clazz the clazz
	 * @return the rocket request builder
	 */
	public RocketJsonBuilder targetType(Class<?> clazz) {
		this.clazz = clazz;
		return this;
	}
	
	/**
	 * Sets the request tag. Request can be removed by the tag.
	 *
	 * @param tag the tag
	 * @return the rocket request builder
	 */
	public RocketJsonBuilder requestTag(Object tag) {
		this.tag = tag;
		return this;
	}

    /**
     * Sets the request cookie tag. Request can be removed by the tag.
     *
     * @param enableCookie the tag
     * @return the rocket request builder
     */
    public RocketJsonBuilder enableCookie(boolean enableCookie) {
        this.isCookieEnabled = enableCookie;
        return this;
    }

    /**
     * Sets the request api cache strategy. Request can be removed by the tag.
     *
     * @param cacheStrategy the cache strategy
     * @return the rocket request builder
     */
    public RocketJsonBuilder setCacheStrategy(DiskCacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
        return this;
    }
	
	/**
	 * Sets the request params for the http post.
	 *
	 * @param params the params
	 * @return the rocket request builder
	 */
	public RocketJsonBuilder requestParams(Map<String, String> params) {
		this.params = params;
		return this;
	}
	
	/**
	 * Sets the json request http headers.
	 *
	 * @param headers the headers
	 * @return the rocket request builder
	 */
	public RocketJsonBuilder requestHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
	}

	
	public void load(String uri) {
		load(Method.POST, uri);
	}
	

	public void load(int method, String uri) {
		if(TextUtils.isEmpty(uri)) return;
		
		if(clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}
		
//		if (Build.VERSION.SDK_INT >= 9 && uri.startsWith("https")) {
//			RocketX509TrustManager.allowAllSSL();  
//		}
//		
		addRequest(method, uri, clazz);
	}

	//***************************private apis***************************************//
	/**
	 * Add the json request to the request queue.
	 *
	 * @param <T> the generic type
	 * @param method the method
	 * @param uri the uri
	 * @param clazz the clazz
	 */
	private <T> void addRequest(int method, String uri, Class<T> clazz) {
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
        request.setCookieEnableOrDisable(isCookieEnabled);
        request.setCacheStrategy(cacheStrategy);
		rocket.getRequestQueue().add(request);
	}

	@Override
	public void cachePolicy(CachePolicy policy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skipMemoryCache(boolean skipMemoryCache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skipDiskCache(boolean skipDiskCache) {
		// TODO Auto-generated method stub
		
	}
}
