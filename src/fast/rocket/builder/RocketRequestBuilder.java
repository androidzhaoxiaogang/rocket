package fast.rocket.builder;

import android.content.Context;
import fast.rocket.Rocket;

@SuppressWarnings("rawtypes")
public class RocketRequestBuilder implements RequestBuilder{
	private Context context;
	public Rocket rocket;

	public RocketRequestBuilder(Context context, Rocket rocket) {
		this.context = context;
		this.rocket = rocket;
	}

	@Override
	public ImageViewBuilder asImage() {
		return new RocketImageBuilder(this.context, this);
	}

	@Override
	public JsonBuilder asJson(Class clazz) {
		return new RocketJsonBuilder(rocket, clazz);
	}

}