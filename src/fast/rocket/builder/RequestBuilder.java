package fast.rocket.builder;

public interface RequestBuilder<R extends RequestBuilder<?>>{
	/**
     * Deserialize the JSON request into a Java object of the given class using Gson.
     * @param <T>
     * @return
     */
    public <T>void asJson(Class<T> clazz);

    /**
     * Add this request to a group specified by groupKey. This key can be used in a later call to
     * Ion.cancelAll(groupKey) to cancel all the requests in the same group.
     * @param groupKey
     * @return
     */
    public void group(Object groupKey);
    
    
    public ImageViewBuilder<? extends ImageViewBuilder<?>> asImage();
    
    public void requestHeaders();
    
    public void requestParams();
}
