package fast.rocket.config;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;

public interface ImageViewBuilder<IVB extends ImageViewBuilder<?>> {
    /**
     * Set a placeholder on the ImageView while the request is loading
     * @param drawable
     * @return
     */
    public IVB placeholder(Drawable drawable);

    /**
     * Set a placeholder on the ImageView while the request is loading
     * @param resourceId
     * @return
     */
    public IVB placeholder(int resourceId);

    /**
     * Set an error image on the ImageView if the request fails to load
     * @param drawable
     * @return
     */
    public IVB error(Drawable drawable);

    /**
     * Set an error image on the ImageView if the request fails to load
     * @param resourceId
     * @return
     */
    public IVB error(int resourceId);

    /**
     * If an ImageView is loaded successfully from a remote source or file storage,
     * animate it in using the given Animation. The default animation is to fade
     * in.
     * @param in Animation to apply to the ImageView after the request has loaded
     *           and the Bitmap has been retrieved.
     * @return
     */
    public IVB animateIn(Animation in);

    /**
     * If an ImageView is loaded successfully from a remote source or file storage,
     * animate it in using the given Animation resource. The default animation is to fade
     * in.
     * @param animationResource Animation resource to apply to the ImageView after the request has loaded
     *           and the Bitmap has been retrieved.
     * @return
     */
    public IVB animateIn(int animationResource);

    /**
     * If the ImageView needs to load from a remote source or file storage,
     * the given Animation will be used while it is loading.
     * @param load Animation to apply to the imageView while the request is loading.
     * @return
     */
    public IVB animateLoad(Animation load);

    /**
     * If the ImageView needs to load from a remote source or file storage,
     * the given Animation resource will be used while it is loading.
     * @param animationResource Animation resource to apply to the imageView while the request is loading.
     * @return
     */
    public IVB animateLoad(int animationResource);
}
