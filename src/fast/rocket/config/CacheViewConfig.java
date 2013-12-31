package fast.rocket.config;


public interface CacheViewConfig {

    /**
     * Set a placeholder on the ImageView while the request is loading
     * @param resourceId
     * @return
     */
    public void placeholder();

    /**
     * Set an error image on the ImageView if the request fails to load
     * @param resourceId
     * @return
     */
    public void error();

    /**
     * If the ImageView needs to load from a remote source or file storage,
     * the given Animation will be used while it is loading.
     * @param load Animation to apply to the imageView while the request is loading.
     * @return
     */
    public void animateLoad();

}
