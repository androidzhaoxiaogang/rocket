package fast.rocket.builder;

/**
 * The Interface LaunchBuilder.
 */
public interface LaunchBuilder {
    
    /**
     * Load.
     *
     * @param uri the uri
     */
    public void load(String uri);

    /**
     * Load.
     *
     * @param method the method
     * @param uri the uri
     */
    public void load(int method, String uri);
}
