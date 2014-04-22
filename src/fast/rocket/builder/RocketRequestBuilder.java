package fast.rocket.builder;

import java.io.File;

import android.content.Context;
import fast.rocket.Rocket;
import fast.rocket.request.Request.Method;

/**
 * The Class RocketRequestBuilder.
 */
@SuppressWarnings("rawtypes")
public class RocketRequestBuilder implements RequestBuilder, LoadBuilder<RequestBuilder>{
	
	/** The context. */
	private Context context;
	
	/** The rocket. */
	public Rocket rocket;
	
	private String uri;
	
	//private File file;
	
	private int method;

	/**
	 * Instantiates a new rocket request builder.
	 *
	 * @param context the context
	 * @param rocket the rocket
	 */
	public RocketRequestBuilder(Context context, Rocket rocket) {
		this.context = context;
		this.rocket = rocket;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.RequestBuilder#asImage()
	 */
	@Override
	public ImageViewBuilder<?> asImage() {
		return new RocketImageBuilder(this.context, this, uri);
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.RequestBuilder#asJson(java.lang.Class)
	 */
	@Override
	public JsonBuilder asJson(Class clazz) {
		return new RocketJsonBuilder(rocket, clazz, uri, method);
	}

	@Override
	public RocketRequestBuilder load(String uri) {
		return load(Method.POST, uri);
	}

	@Override
	public RocketRequestBuilder load(int method, String url) {
		this.uri = url;
		this.method = method;
		return this;
	}

	@Override
	public RocketRequestBuilder load(File file) {
		return null;
	}

}
