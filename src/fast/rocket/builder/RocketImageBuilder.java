package fast.rocket.builder;

import java.io.File;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.cache.ImageLoader;
import fast.rocket.cache.NetworkCacheView;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.request.Request.Method;
import fast.rocket.utils.RocketUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

@SuppressWarnings("rawtypes")
public class RocketImageBuilder implements ImageViewBuilder, CacheBuilder<RocketImageBuilder>,
		LoadBuilder<RocketImageBuilder> {

	public Rocket rocket;
	private Context context;
	private Builder builder;
	
	private ScaleMode scaleMode = ScaleMode.FitXY;
	private CachePolicy cachePolicy = CachePolicy.CACHEFIRST;
	
	public RocketImageBuilder (Context context, RocketRequestBuilder requestBuilder) {
		this.context = context;
		this.builder = new Builder(scaleMode, cachePolicy);
		this.rocket = requestBuilder.rocket;
	}
	
	@Override
	public RocketImageBuilder placeholder(Drawable drawable) {
		builder.placeholderDrawable = drawable;
		return this;
	}

	@Override
	public RocketImageBuilder placeholder(int resourceId) {
		builder.placeholderResource = resourceId;
		return this;
	}

	@Override
	public RocketImageBuilder error(Drawable drawable) {
		builder.errorDrawable = drawable;
		return this;
	}

	@Override
	public RocketImageBuilder error(int resourceId) {
		builder.errorResource = resourceId;
		return this;
	}

	@Override
	public RocketImageBuilder animateIn(Animation in) {
		builder.inAnimation = in;
		return this;
	}

	@Override
	public RocketImageBuilder animateIn(int animationResource) {
		builder.inAnimationResource = animationResource;
		return this;
	}

	@Override
	public RocketImageBuilder animateOut(Animation out) {
		builder.outAnimation = out;
		return this;
	}

	@Override
	public RocketImageBuilder animateOut(int animationResource) {
		builder.outAnimationResource = animationResource;
		return this;
	}

	@Override
	public RocketImageBuilder resize(int width, int height) {
		builder.resizeWidth = width;
		builder.resizeHeight = height;
		return this;
	}
	
	/** Resize the image to the specified dimension size. */
	public RocketImageBuilder resizeDimen(int targetWidthResId, int targetHeightResId) {
		Resources resources = context.getResources();
		int targetWidth = resources.getDimensionPixelSize(targetWidthResId);
		int targetHeight = resources.getDimensionPixelSize(targetHeightResId);
		return resize(targetWidth, targetHeight);
	}

	@Override
	public RocketImageBuilder centerCrop() {
		if (builder.resizeWidth <= 0 || builder.resizeHeight <= 0)
			throw new IllegalStateException("Please call resize first!");
		builder.scaleMode = ScaleMode.CenterCrop;
		return this;
	}

	@Override
	public RocketImageBuilder centerInside() {
		if (builder.resizeWidth <= 0 || builder.resizeHeight <= 0)
			throw new IllegalStateException("Please call resize first!");
		builder.scaleMode = ScaleMode.CenterInside;
		return this;
	}

	@Override
	public void into(ImageView imageView) {
		final ImageLoader loader = rocket.getImageLoader();
		final ImageListener listener = ImageLoader.getImageListener(imageView, builder);

		loader.get(listener, builder);
		RocketUtils.loadAnimation(imageView, builder.inAnimation, builder.inAnimationResource);
	}

	@Override
	public void into(NetworkCacheView imageView) {
		final ImageLoader loader = rocket.getImageLoader();

		imageView.setImageUrl(loader, builder);
		RocketUtils.loadAnimation(imageView, builder.inAnimation, builder.inAnimationResource);
	}

	@Override
	public RocketImageBuilder cachePolicy(CachePolicy policy) {
		builder.cachePolicy = policy;
		return this;
	}

	@Override
	public RocketImageBuilder skipMemoryCache(boolean skipMemoryCache) {
		builder.skipMemoryCache = skipMemoryCache;
		return this;
	}

	@Override
	public RocketImageBuilder skipDiskCache(boolean skipDiskCache) {
		builder.skipDiskCache = skipDiskCache;
		return this;
	}

	@Override
	public RocketImageBuilder load(String uri) {
		load(Method.GET, uri);
		return this;
	}

	@Override
	public RocketImageBuilder load(int method, String url) {
		builder.uri = url;
		return this;
	}

	@Override
	public RocketImageBuilder load(File file) {
		return this;
	}
	
	public static final class Builder {
		public int placeholderResource;
		public int errorResource;
		public int inAnimationResource;
		public int outAnimationResource;

		public int resizeWidth;
		public int resizeHeight;

		public Animation inAnimation;
		public Animation outAnimation;

		public Drawable placeholderDrawable;
		public Drawable errorDrawable;
		
		public boolean skipMemoryCache;
		public boolean skipDiskCache;

		public String uri;
		
		public ScaleMode scaleMode;
		public CachePolicy cachePolicy;
		
		public Builder( ScaleMode sm, CachePolicy cp) {
			scaleMode = sm;
			cachePolicy = cp;
		}
	}

}
