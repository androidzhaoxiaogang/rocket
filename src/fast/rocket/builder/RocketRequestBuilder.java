package fast.rocket.builder;

import java.io.File;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;

@SuppressWarnings("rawtypes")
public class RocketRequestBuilder implements RequestBuilder, LoadBuilder<RocketRequestBuilder>, CacheBuilder {
	private Context context;
	private Rocket rocket;
	
	public RocketRequestBuilder(Context context, Rocket rocket) {
		this.context = context;
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
	}

	@Override
	public RocketImageBuilder asImage() {
		return new RocketImageBuilder(context);
	}

	@Override
	public void cachePolicy(CachePolicy policy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skipMemoryCache(boolean skipMemoryCache) {
		
	}

	@Override
	public void skipDiskCache(boolean skipDiskCache) {
		
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
