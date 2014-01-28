package fast.rocket.builder;

import android.content.Context;
import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import java.io.File;

@SuppressWarnings("rawtypes")
public class RocketRequestBuilder implements RequestBuilder,
		LoadBuilder<RocketRequestBuilder>, CacheBuilder {
	private Context context;
	private Rocket rocket;

	public RocketRequestBuilder(Context context, Rocket rocket) {
		this.context = context;
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

	@Override
	public RocketRequestBuilder load(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RocketRequestBuilder load(int method, String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RocketRequestBuilder load(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void asJson(Class clazz) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void group(Object groupKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ImageViewBuilder asImage() {
		return new RocketImageBuilder(this.context);
	}

	@Override
	public void requestHeaders() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestParams() {
		// TODO Auto-generated method stub
		
	}

	
}