package fast.rocket.builder;


/**
 * The Interface CacheViewConfig.
 */
public interface CacheViewConfig {

    /**
     * Set a placeholder on the ImageView while the request is loading.
     *
     */
    public void placeholder();

    /**
     * Set an error image on the ImageView if the request fails to load.
     *
     */
    public void error();

    /**
     * If the ImageView needs to load from a remote source or file storage,
     * the given Animation will be used while it is loading.
     *
     */
    public void animateLoad();

}
