package fast.rocket.builder;

import android.widget.ImageView;
import fast.rocket.cache.NetworkCacheView;

public interface ImageViewFutureBuilder {
	  /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView
     * @param imageView ImageView to set once the request completes
     * @return
     */
    public void into(ImageView imageView);
    
    /**
     * Perform the request and get the result as a Bitmap, which will then be loaded
     * into the given ImageView.The image references managed by the LRU bitmap cache
     * and the image file managed by the LRU disk cache.
     * @param imageView NetworkCacheView to set once the request completes
     * @return
     */
    public void into(NetworkCacheView imageView);
    
}
