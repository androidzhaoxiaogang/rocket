package fast.rocket.builder;

import java.io.File;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.cache.ImageLoader;
import fast.rocket.cache.ImageLoader.ImageCallback;
import fast.rocket.cache.NetworkCacheView;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.request.Request.Method;
import fast.rocket.utils.RocketUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * The Class RocketImageBuilder.
 */
@SuppressWarnings("rawtypes")
public class RocketImageBuilder implements ImageViewBuilder, CacheBuilder<RocketImageBuilder> {

	/** The rocket. */
	public Rocket rocket;
	
	/** The context. */
	private Context context;
	
	/** The builder. */
	private Builder builder;
	
	/** The scale mode. */
	private ScaleMode scaleMode = ScaleMode.FitXY;
	
	/** The cache policy. */
	private CachePolicy cachePolicy = CachePolicy.CACHEFIRST;
	
	/**
	 * Instantiates a new rocket image builder.
	 *
	 * @param context the context
	 * @param requestBuilder the request builder
	 */
	public RocketImageBuilder (Context context, RocketRequestBuilder requestBuilder) {
		this.context = context;
		this.builder = new Builder(scaleMode, cachePolicy);
		this.rocket = requestBuilder.rocket;
	}
	
	@Override
	public ImageViewBuilder invoke(ImageCallback callback) {
		builder.callback = callback;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#placeholder(android.graphics.drawable.Drawable)
	 */
	@Override
	public RocketImageBuilder placeholder(Drawable drawable) {
		builder.placeholderDrawable = drawable;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#placeholder(int)
	 */
	@Override
	public RocketImageBuilder placeholder(int resourceId) {
		builder.placeholderResource = resourceId;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#error(android.graphics.drawable.Drawable)
	 */
	@Override
	public RocketImageBuilder error(Drawable drawable) {
		builder.errorDrawable = drawable;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#error(int)
	 */
	@Override
	public RocketImageBuilder error(int resourceId) {
		builder.errorResource = resourceId;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#animateIn(android.view.animation.Animation)
	 */
	@Override
	public RocketImageBuilder animateIn(Animation in) {
		builder.inAnimation = in;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#animateIn(int)
	 */
	@Override
	public RocketImageBuilder animateIn(int animationResource) {
		builder.inAnimationResource = animationResource;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#animateOut(android.view.animation.Animation)
	 */
	@Override
	public RocketImageBuilder animateOut(Animation out) {
		builder.outAnimation = out;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#animateOut(int)
	 */
	@Override
	public RocketImageBuilder animateOut(int animationResource) {
		builder.outAnimationResource = animationResource;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#resize(int, int)
	 */
	@Override
	public RocketImageBuilder resize(int width, int height) {
		builder.resizeWidth = width;
		builder.resizeHeight = height;
		return this;
	}
	
	/**
	 * Resize the image to the specified dimension size.
	 *
	 * @param targetWidthResId the target width res id
	 * @param targetHeightResId the target height res id
	 * @return the rocket image builder
	 */
	public RocketImageBuilder resizeDimen(int targetWidthResId, int targetHeightResId) {
		Resources resources = context.getResources();
		int targetWidth = resources.getDimensionPixelSize(targetWidthResId);
		int targetHeight = resources.getDimensionPixelSize(targetHeightResId);
		return resize(targetWidth, targetHeight);
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#centerCrop()
	 */
	@Override
	public RocketImageBuilder centerCrop() {
		if (builder.resizeWidth <= 0 || builder.resizeHeight <= 0)
			throw new IllegalStateException("Please call resize first!");
		builder.scaleMode = ScaleMode.CenterCrop;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#centerInside()
	 */
	@Override
	public RocketImageBuilder centerInside() {
		if (builder.resizeWidth <= 0 || builder.resizeHeight <= 0)
			throw new IllegalStateException("Please call resize first!");
		builder.scaleMode = ScaleMode.CenterInside;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#into(android.widget.ImageView)
	 */
	@Override
	public RocketImageBuilder into(ImageView imageView) {
		final ImageLoader loader = rocket.getImageLoader();
		final ImageListener listener = ImageLoader.getImageListener(imageView, builder);
		builder.skipMemoryCache = true;

		loader.get(listener, builder);
		RocketUtils.loadAnimation(imageView, builder.inAnimation, builder.inAnimationResource);
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.ImageViewBuilder#into(fast.rocket.cache.NetworkCacheView)
	 */
	@Override
	public RocketImageBuilder into(NetworkCacheView imageView) {
		final ImageLoader loader = rocket.getImageLoader();
		builder.skipMemoryCache = false;

		imageView.setImageUrl(loader, builder);
		RocketUtils.loadAnimation(imageView, builder.inAnimation, builder.inAnimationResource);
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.CacheBuilder#cachePolicy(fast.rocket.cache.CachePolicy)
	 */
	@Override
	public RocketImageBuilder cachePolicy(CachePolicy policy) {
		builder.cachePolicy = policy;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.CacheBuilder#skipMemoryCache(boolean)
	 */
	@Override
	public RocketImageBuilder skipMemoryCache(boolean skipMemoryCache) {
		builder.skipMemoryCache = skipMemoryCache;
		return this;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.LoadBuilder#load(java.lang.String)
	 */
	@Override
	public void load(String uri) {
		load(Method.GET, uri);
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.LoadBuilder#load(int, java.lang.String)
	 */
	@Override
	public void load(int method, String url) {
		builder.uri = url;
	}

	/* (non-Javadoc)
	 * @see fast.rocket.builder.LoadBuilder#load(java.io.File)
	 */
	@Override
	public void load(File file) {
	}
	
	/**
	 * The Class Builder.
	 */
	public static final class Builder {
		
		/** The placeholder resource. */
		public int placeholderResource;
		
		/** The error resource. */
		public int errorResource;
		
		/** The in animation resource. */
		public int inAnimationResource;
		
		/** The out animation resource. */
		public int outAnimationResource;

		/** The resize width. */
		public int resizeWidth;
		
		/** The resize height. */
		public int resizeHeight;

		/** The in animation. */
		public Animation inAnimation;
		
		/** The out animation. */
		public Animation outAnimation;

		/** The placeholder drawable. */
		public Drawable placeholderDrawable;
		
		/** The error drawable. */
		public Drawable errorDrawable;
		
		/** The skip memory cache. */
		public boolean skipMemoryCache = false;
		
		public ImageCallback callback;

		/** The uri. */
		public String uri;
		
		/** The scale mode. */
		public ScaleMode scaleMode;
		
		/** The cache policy. */
		public CachePolicy cachePolicy;
		
		/**
		 * Instantiates a new builder.
		 *
		 * @param sm the sm
		 * @param cp the cp
		 */
		public Builder( ScaleMode sm, CachePolicy cp) {
			scaleMode = sm;
			cachePolicy = cp;
		}
	}
	
}
