package fast.rocket.builder;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;
import fast.rocket.cache.ImageLoader.DownloadListener;
import fast.rocket.cache.ImageLoader.ImageCallback;
import fast.rocket.cache.CachePolicy;
import fast.rocket.cache.NetworkCacheView;

/**
 * @author zhaoxiaogang
 *
 * @param <I>
 */
public interface ImageViewBuilder<I extends ImageViewBuilder<?>>{
	public enum ScaleMode {
	    FitXY,
	    CenterCrop,
	    CenterInside
	}
	
	/**
	 * Cache policy.
	 *
	 * @param policy the policy
	 * @return the i
	 */
	public I cachePolicy(CachePolicy policy);
	
	/**
	 * @param listener
	 * @return
	 */
	public I listen(DownloadListener listener);
	
	/**
	 * Invoke.
	 *
	 * @param callback the callback
	 * @return the i
	 */
	public I invoke(ImageCallback callback);
	
	/**
	 * Skip memory cache.
	 *
	 * @param skipMemoryCache the skip memory cache
	 * @return the i
	 */
	public I skipMemoryCache(boolean skipMemoryCache);
	
	/**
     * Set a placeholder on the ImageView while the request is loading
     * @param drawable
     * @return
     */
    public I placeholder(Drawable drawable);

    /**
     * Set a placeholder on the ImageView while the request is loading
     * @param resourceId
     * @return
     */
    public I placeholder(int resourceId);

    /**
     * Set an error image on the ImageView if the request fails to load
     * @param drawable
     * @return
     */
    public I error(Drawable drawable);

    /**
     * Set an error image on the ImageView if the request fails to load
     * @param resourceId
     * @return
     */
    public I error(int resourceId);

    /**
     * If the ImageView needs to load from a remote source or file storage,
     * the given Animation will be used while it is loading.
     * @param load Animation to apply to the imageView while the request is loading.
     * @return
     */
    public I animateIn(Animation in);

    /**
     * If the ImageView needs to load from a remote source or file storage,
     * the given Animation resource will be used while it is loading.
     * @param animationResource Animation resource to apply to the imageView while the request is loading.
     * @return
     */
    public I animateIn(int animationResource);

    /**
     * If an ImageView is loaded successfully from a remote source or file storage,
     * animate it in using the given Animation. The default animation is to fade
     * in.
     * @param in Animation to apply to the ImageView after the request has loaded
     *           and the Bitmap has been retrieved.
     * @return
     */
    public I animateOut(Animation load);

    /**
     * If an ImageView is loaded successfully from a remote source or file storage,
     * animate it in using the given Animation resource. The default animation is to fade
     * in.
     * @param animationResource Animation resource to apply to the ImageView after the request has loaded
     *           and the Bitmap has been retrieved.
     * @return
     */
    public I animateOut(int animationResource);
    
    /**
     * Resize the bitmap to the given dimensions.
     * @param width
     * @param height
     * @return
     */
    public I resize(int width, int height);
    
    /** Resize the image to the specified dimension size.
     * @param targetWidthResId
     * @param targetHeightResId
     * @return
     */		
    public I resizeDimen(int targetWidthResId, int targetHeightResId);

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation. This will scale the image so that it fills the bounds, and crops
     * the extra.
     * @return
     */
    public I centerCrop();

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation. This will scale the image so that one dimension is as large as the requested
     * bounds.
     * @return
     */
    public I centerInside();
	
	  /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView
     * @param imageView ImageView to set once the request completes
     * @return
     */
    public I into(ImageView imageView);
    
    /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView.The image references managed by the LRU bitmap cache
     * and the image file managed by the LRU disk cache.
     * @param imageView NetworkCacheView to set once the request completes
     * @return
     */
    public I into(NetworkCacheView imageView);

}
