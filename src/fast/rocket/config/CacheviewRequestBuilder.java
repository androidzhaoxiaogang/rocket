/*
 * 
 */
package fast.rocket.config;

import java.lang.ref.WeakReference;

import fast.rocket.Request.Method;
import fast.rocket.Rocket;
import fast.rocket.cache.NetworkCacheView;
import fast.rocket.cache.ImageLoader;
import fast.rocket.cache.ImageLoader.ImageCallback;
import fast.rocket.utils.RocketUtils;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;

/**
 * The Class CacheviewRequestBuilder for the {@link NetworkCacheView} configuration.
 */
public class CacheviewRequestBuilder implements LaunchBuilder{
	
	/** Skip the disk cache. */
	private boolean skipDiskCache;
	
	/** The placeholder resource. */
	private int placeholderResource;
	
	/** The error resource. */
	private int errorResource;
	
	/** The in animation resource. */
	private int inAnimationResource;
	
	/** The load animation resource. */
	private int loadAnimationResource;
	
	/** The placeholder drawable. */
	private Drawable placeholderDrawable;
	
	/** The error drawable. */
	private Drawable errorDrawable;
	
	/** The in animation. */
	private Animation inAnimation;
	
	/** The load animation. */
	private Animation loadAnimation;
	
	/** The rocket. */
	public Rocket rocket;
	
    /** The resize width. */
    private int resizeWidth = 0;
    
    /** The resize height. */
    private int resizeHeight = 0;

	/** The image view ref. */
	private WeakReference<NetworkCacheView> imageViewRef;
	
	/** The callback for image loading completed. */
	private ImageCallback callback;

	/**
	 * With cache image view.
	 *
	 * @param imageView the image view
	 * @return the image request builder
	 */
	public CacheviewRequestBuilder withImageView(NetworkCacheView imageView) {
		imageViewRef = new WeakReference<NetworkCacheView>(imageView);
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
	 *
	 * @return the cacheview request builder
	 */
	public CacheviewRequestBuilder skipDiskCache() {
		skipDiskCache = true;
		return this;
	}
	
    /**
     * Resize.
     *
     * @param width the width
     * @param height the height
     * @return the cacheview request builder
     */
    public CacheviewRequestBuilder resize(int width, int height) {
        resizeWidth = width;
        resizeHeight = height;
        return this;
    }
    
    /**
     * Register a callback for image loading completed.
     *
     * @param callback the callback
     */
    public CacheviewRequestBuilder invoke(ImageCallback callback) {
    	this.callback = callback;
    	return this;
    }

	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(java.lang.String)
	 */
	@Override
	public void load(String uri) {
		load(Method.GET, uri);
	}

	/* (non-Javadoc)
	 * @see fast.rocket.config.LaunchBuilder#load(int, java.lang.String)
	 */
	@Override
	public void load(int method, String uri) {
		final NetworkCacheView imageView = imageViewRef.get();
		final ImageLoader loader = rocket.getImageLoader();
		
		initCacheView(imageView, placeholderDrawable, placeholderResource,
				errorDrawable, errorResource, inAnimation, inAnimationResource);
		imageView.setImageUrl(uri, loader, resizeWidth, resizeHeight,
				skipDiskCache, callback);

		//loading animation
		RocketUtils.loadAnimation(imageView, loadAnimation,
				loadAnimationResource);
	}
	
	//**************************************private apis***************************************//
	/**
	 * Inits the cache view.
	 *
	 * @param imageView the image view
	 * @param drawable the drawable
	 * @param resourceId the resource id
	 * @param errDrawable the err drawable
	 * @param errResourceId the err resource id
	 * @param in the in
	 * @param animationResource the animation resource
	 */
	private void initCacheView(NetworkCacheView imageView, Drawable drawable,
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
