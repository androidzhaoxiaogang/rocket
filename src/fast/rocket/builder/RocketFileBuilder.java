package fast.rocket.builder;

import java.util.Map;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.error.RocketError;
import fast.rocket.request.FileRequest;
import fast.rocket.request.Request.Method;
import fast.rocket.response.JsonCallback;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

public class RocketFileBuilder implements FileMultipartBuilder {
	
	@SuppressWarnings("rawtypes")
	private JsonCallback callback;
	
	/** The class type to be parsed. */
	private Class<?> clazz;
	
	/** The request tag. */
	private Object tag;
	
	/** The rocket instance. */
	private Rocket rocket;
	
	private Map<String, String> params;
	
	private String contentType = "text/plain;";
	
	/** The cache policy. */
	private CachePolicy cachePolicy;
	
	private String uri;
	
	private String fileName;
	
	private String filePath;
	
	public RocketFileBuilder(Rocket rocket, Class<?> clazz, String uri) {
		this.rocket = rocket;
		this.clazz = clazz;
		this.uri = uri;
	}
	
	@Override
	public FileMultipartBuilder invoke(JsonCallback<?> callback) {
		this.callback = callback;
		addRequest(Method.POST, uri, clazz);
		return this;
	}
	
	private <T> void addRequest(int method, String uri, Class<T> clazz) {
		if (clazz == null || callback == null) {
			throw new IllegalArgumentException("Initialization params is null");
		}

		FileRequest<T> request = new FileRequest<T>(method, uri, new Listener<T>() {

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
				}, clazz);
		
		if(params != null) {
			for(String name : params.keySet()) {
				request.addMultipartParam(name, contentType, params.get(name));
			}
		}
		
		request.addFile(fileName, filePath);

		if (tag != null)
			request.setTag(tag);
		request.setCachePolicy(cachePolicy);
		rocket.getRequestQueue().add(request);
	}

	@Override
	public FileMultipartBuilder requestTag(Object tag) {
		this.tag = tag;
		return this;
	}

	@Override
	public FileMultipartBuilder addFile(String name, String filePath) {
		this.fileName = name;
		this.filePath = filePath;
		return this;
	}

	@Override
	public FileMultipartBuilder addMultipartParam(Map<String, String> params) {
		this.params = params;
		return this;
	}

	@Override
	public FileMultipartBuilder cachePolicy(CachePolicy policy) {
		this.cachePolicy = policy;
		return this;
	}
}
