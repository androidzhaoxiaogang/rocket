package fast.rocket.builder;

import java.io.File;

import fast.rocket.Rocket;
import fast.rocket.cache.CachePolicy;
import fast.rocket.cache.ImageLoader;
import fast.rocket.cache.NetworkCacheView;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.utils.RocketUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

@SuppressWarnings("rawtypes")
public class RocketImageBuilder implements ImageBuilder, ImageViewFutureBuilder, CacheBuilder, LoadBuilder {

	private int placeholderResource;
	private int errorResource;
	private int inAnimationResource;
	private int outAnimationResource;

	private int resizeWidth;
	private int resizeHeight;

	private Animation inAnimation;
	private Animation outAnimation;

	private Drawable placeholderDrawable;
	private Drawable errorDrawable;
	
	private boolean skipMemoryCache;
	private boolean skipDiskCache;

	private String uri;
	public Rocket rocket;
	private Context context;
	
	private ScaleMode scaleMode = ScaleMode.FitXY;
	private CachePolicy cachePolicy = CachePolicy.CACHEFIRST;
	
	/**
	 * With image view.
	 * 
	 * @param imageView NetworkCacheView
	 *            
	 * @return the image request builder
	 */
	public RocketImageBuilder withImageView(Context context, NetworkCacheView imageView) {
		this.context = context;
		return this;
	}

	@Override
	public RocketImageBuilder placeholder(Drawable drawable) {
		placeholderDrawable = drawable;
		return this;
	}

	@Override
	public RocketImageBuilder placeholder(int resourceId) {
		placeholderResource = resourceId;
		return this;
	}

	@Override
	public RocketImageBuilder error(Drawable drawable) {
		errorDrawable = drawable;
		return this;
	}

	@Override
	public RocketImageBuilder error(int resourceId) {
		errorResource = resourceId;
		return this;
	}

	@Override
	public RocketImageBuilder animateIn(Animation in) {
		inAnimation = in;
		return this;
	}

	@Override
	public RocketImageBuilder animateIn(int animationResource) {
		inAnimationResource = animationResource;
		return this;
	}

	@Override
	public RocketImageBuilder animateOut(Animation out) {
		outAnimation = out;
		return this;
	}

	@Override
	public RocketImageBuilder animateOut(int animationResource) {
		outAnimationResource = animationResource;
		return this;
	}

	@Override
	public RocketImageBuilder resize(int width, int height) {
		resizeWidth = width;
		resizeHeight = height;
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
		if (resizeWidth <= 0 || resizeHeight <= 0)
			throw new IllegalStateException("Please call resize first!");
		scaleMode = ScaleMode.CenterCrop;
		return this;
	}

	@Override
	public RocketImageBuilder centerInside() {
		if (resizeWidth <= 0 || resizeHeight <= 0)
			throw new IllegalStateException("Please call resize first!");
		scaleMode = ScaleMode.CenterInside;
		return this;
	}

	@Override
	public void into(ImageView imageView) {
		final ImageLoader loader = rocket.getImageLoader();
		final ImageListener listener = ImageLoader.getImageListener(imageView,
				placeholderDrawable, placeholderResource, errorDrawable,
				errorResource, inAnimation, inAnimationResource);

		loader.get(uri, listener, resizeWidth, resizeHeight, skipMemoryCache, skipDiskCache);
		RocketUtils.loadAnimation(imageView, outAnimation, outAnimationResource);
	}

	@Override
	public void into(NetworkCacheView imageView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cachePolicy(CachePolicy policy) {
		cachePolicy = policy;
	}

	@Override
	public void skipMemoryCache(boolean skipMemoryCache) {
		this.skipMemoryCache = skipMemoryCache;
	}

	@Override
	public void skipDiskCache(boolean skipDiskCache) {
		this.skipDiskCache = skipDiskCache;
	}

	@Override
	public Object load(String uri) {
		this.uri = uri;
		return null;
	}

	@Override
	public Object load(String method, String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object load(File file) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
