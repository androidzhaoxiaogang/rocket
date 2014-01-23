/*
 * 
 */
package fast.rocket.config;

import java.lang.ref.WeakReference;

import fast.rocket.Request.Method;
import fast.rocket.Rocket;
import fast.rocket.cache.ImageLoader;
import fast.rocket.cache.ImageLoader.ImageCallback;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.utils.RocketUtils;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * The Class ImageviewRequestBuilder for image download loader configuration.
 * It's benefit for you if don't wanna the download image put into the LRU 
 * cache (L1) or the disk cache(L2). This is fit for self-manage the image
 * strong reference.  
 * 
 */
public class ImageviewRequestBuilder implements LaunchBuilder{
	
	/** The skip memory cache. */
	private boolean skipMemoryCache;
	
	/** The skip disk cache. */
	private boolean skipDiskCache;
	
	/** The placeholder resource id. */
	private int placeholderResource;
	
	/** The error resource id. */
	private int errorResource;
	
	/** The in animation resource id. */
	private int inAnimationResource;
	
	/** The load animation resource id. */
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
	private WeakReference<ImageView> imageViewRef;
	
	/** The callback for image loading completed. */
	private ImageCallback callback;

	/**
	 * With image view.
	 *
	 * @param imageView the image view
	 * @return the image request builder
	 */
	public ImageviewRequestBuilder withImageView(ImageView imageView) {
		imageViewRef = new WeakReference<ImageView>(imageView);
		return this;
	}

	/**
	 * Placeholder.
	 *
	 * @param drawable the drawable
	 * @return the image request builder
	 */
	public ImageviewRequestBuilder placeholder(Drawable drawable) {
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
	public ImageviewRequestBuilder placeholder(int resourceId) {
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
	public ImageviewRequestBuilder error(Drawable drawable) {
		if (drawable == null) {
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
	public ImageviewRequestBuilder error(int resourceId) {
		errorResource = resourceId;
		return this;
	}
	
	/**
	 * Animation for image sliding in.
	 *
	 * @param in the in
	 * @return the image request builder
	 */
	public ImageviewRequestBuilder animateIn(Animation in) {
		inAnimation = in;
		return this;
	}

	/**
	 * Animation for image sliding in.
	 *
	 * @param animationResource the animation resource
	 * @return the image request builder
	 */
	public ImageviewRequestBuilder animateIn(int animationResource) {
		inAnimationResource = animationResource;
		return this;
	}

	/**
	 * Animation for image sliding out.
	 *
	 * @param load the load
	 * @return the image request builder
	 */
	public ImageviewRequestBuilder animateLoad(Animation load) {
		loadAnimation = load;
		return this;
	}

	/**
	 * Animation for image sliding out.
	 *
	 * @param animationResource the animation resource
	 * @return the image request builder
	 */
	public ImageviewRequestBuilder animateLoad(int animationResource) {
		loadAnimationResource = animationResource;
		return this;
	}

	/**
	 * Indicate that this action should not use the memory cache for attempting
	 * to load or save the image. This can be useful when you know an image will
	 * only ever be used once (e.g., loading an image from the filesystem and
	 * uploading to a remote server).
	 *
	 * @return the imageview request builder
	 */
	public ImageviewRequestBuilder skipMemoryCache() {
		skipMemoryCache = true;
		return this;
	}
	
	/**
	 * Indicate that this action should not use the disk cache for attempting
	 * to load or save the image. This can be useful when you know an image will
	 * only ever be used once (e.g., loading an image from the filesystem and
	 * uploading to a remote server).
	 *
	 * @return the imageview request builder
	 */
	public ImageviewRequestBuilder skipDiskCache() {
		skipDiskCache = true;
		return this;
	}
	
    /**
     * Set proper image width and height then resize the image view.
     *
     * @param width the width
     * @param height the height
     * @return the imageview request builder
     */
    public ImageviewRequestBuilder resize(int width, int height) {
        resizeWidth = width;
        resizeHeight = height;
        return this;
    }
    
    /**
     * Register a callback after image loading completed. Then can 
     * manage the image reference yourselves.
     *
     * @param callback the callback
     * @return the imageview request builder
     */
    public ImageviewRequestBuilder invoke(ImageCallback callback) {
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
		final ImageView imageView = imageViewRef.get();
		final ImageLoader loader = rocket.getImageLoader();
		final ImageListener listener = ImageLoader.getImageListener(imageView,
				placeholderDrawable, placeholderResource, errorDrawable,
				errorResource, inAnimation, inAnimationResource, callback);

		loader.get(uri, listener, resizeWidth, resizeHeight, skipMemoryCache,
				skipDiskCache);
		RocketUtils.loadAnimation(imageView, loadAnimation,
				loadAnimationResource);
	}
	
	//**************************************private apis***************************************//

}
