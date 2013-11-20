/*
 * 
 */
package fast.rocket.config;

import java.lang.ref.WeakReference;

import fast.rocket.Request.Method;
import fast.rocket.Rocket;
import fast.rocket.cache.CacheImageView;
import fast.rocket.cache.ImageLoader;
import fast.rocket.utils.RocketUtils;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;

public class CacheviewRequestBuilder implements LaunchBuilder{
	
	private boolean skipDiskCache;
	
	private int placeholderResource;
	private int errorResource;
	private int inAnimationResource;
	private int loadAnimationResource;
	
	private Drawable placeholderDrawable;
	private Drawable errorDrawable;
	
	private Animation inAnimation;
	private Animation loadAnimation;
	
	/** The rocket. */
	public Rocket rocket;
	
    private int resizeWidth = 0;
    private int resizeHeight = 0;

	private WeakReference<CacheImageView> imageViewRef;

	/**
	 * With image view.
	 *
	 * @param imageView the image view
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder withImageView(CacheImageView imageView) {
		imageViewRef = new WeakReference<CacheImageView>(imageView);
		return this;
	}

	/**
	 * Placeholder.
	 *
	 * @param drawable the drawable
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder placeholder(Drawable drawable) {
		if (placeholderResource != 0) {
			throw new IllegalStateException("Placeholder image already set.");
		}
		placeholderDrawable = drawable;
		return this;
	}

	/**
	 * Placeholder.
	 *
	 * @param resourceId the resource id
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder placeholder(int resourceId) {
		if (resourceId == 0) {
			throw new IllegalArgumentException(
					"Placeholder image resource invalid.");
		}
		if (placeholderDrawable != null) {
			throw new IllegalStateException("Placeholder image already set.");
		}
		placeholderResource = resourceId;
		return this;
	}

	/**
	 * Error.
	 *
	 * @param drawable the drawable
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder error(Drawable drawable) {
		if (errorDrawable == null) {
			throw new IllegalArgumentException("Error image may not be null.");
		}
		if (errorResource != 0) {
			throw new IllegalStateException("Error image already set.");
		}
		errorDrawable = drawable;
		return this;
	}

	/**
	 * Error.
	 *
	 * @param resourceId the resource id
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder error(int resourceId) {
		errorResource = resourceId;
		return this;
	}
	
	/**
	 * Animate in.
	 *
	 * @param in the in
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder animateIn(Animation in) {
		inAnimation = in;
		return this;
	}

	/**
	 * Animate in.
	 *
	 * @param animationResource the animation resource
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder animateIn(int animationResource) {
		inAnimationResource = animationResource;
		return this;
	}

	/**
	 * Animate load.
	 *
	 * @param load the load
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder animateLoad(Animation load) {
		loadAnimation = load;
		return this;
	}

	/**
	 * Animate load.
	 *
	 * @param animationResource the animation resource
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder animateLoad(int animationResource) {
		loadAnimationResource = animationResource;
		return this;
	}

	/**
	 * Indicate that this action should not use the disk cache for attempting
	 * to load or save the image. This can be useful when you know an image will
	 * only ever be used once (e.g., loading an image from the filesystem and
	 * uploading to a remote server).
	 */
	public CacheviewRequestBuilder skipDiskCache() {
		skipDiskCache = true;
		return this;
	}
	
    public CacheviewRequestBuilder resize(int width, int height) {
        resizeWidth = width;
        resizeHeight = height;
        return this;
    }

	@Override
	public void load(String uri) {
		load(Method.GET, uri);
	}

	@Override
	public void load(int method, String uri) {
		final CacheImageView imageView = imageViewRef.get();
		final ImageLoader loader = rocket.getImageLoader();
		
		initCacheView(imageView, placeholderDrawable, placeholderResource,
				errorDrawable, errorResource, inAnimation, inAnimationResource);
		imageView.setImageUrl(uri, loader, resizeWidth, resizeHeight,
				skipDiskCache);

		RocketUtils.loadAnimation(imageView, loadAnimation,
				loadAnimationResource);
	}
	
	//**************************************private apis***************************************//
	private void initCacheView(CacheImageView imageView, Drawable drawable,
			int resourceId, Drawable errDrawable, int errResourceId,
			Animation in, int animationResource) {
		if(resourceId != 0) {
			imageView.setDefaultImageResId(resourceId);
		}
		
		if (drawable != null) {
			imageView.setPlaceholder(drawable);
		}
		
		if (errResourceId != 0) {
			imageView.setErrorImageResId(errResourceId);
		}
		
		if (errorDrawable != null) {
			imageView.setErrorDrawable(errorDrawable);
		}
		
		if (inAnimation != null) {
			imageView.setAnimateIn(inAnimation);
		}
		
		if (animationResource != 0) {
			imageView.setAnimateIn(animationResource);
		}
	}
}
