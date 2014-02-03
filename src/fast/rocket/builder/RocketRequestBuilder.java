package fast.rocket.builder;

import android.content.Context;
import fast.rocket.Rocket;

/**
 * The Class RocketRequestBuilder.
 */
@SuppressWarnings("rawtypes")
public class RocketRequestBuilder implements RequestBuilder{
	
	/** The context. */
	private Context context;
	
	/** The rocket. */
	public Rocket rocket;

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
	public ImageViewBuilder asImage() {
		return new RocketImageBuilder(this.context, this);
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.RequestBuilder#asJson(java.lang.Class)
	 */
	@Override
	public JsonBuilder asJson(Class clazz) {
		return new RocketJsonBuilder(rocket, clazz);
	}

}