package fast.rocket.builder;

/**
 * The Interface RequestBuilder.
 *
 * @param <R> the generic type
 */
public interface RequestBuilder<R extends RequestBuilder<?>>{
    
    /**
     * As json.
     *
     * @param clazz the clazz
     * @return the json builder<? extends json builder<?>>
     */
    public JsonBuilder<? extends JsonBuilder<?>> asJson(Class<?> clazz);

    /**
     * As image.
     *
     * @return the image view builder<? extends image view builder<?>>
     */
    public ImageViewBuilder<? extends ImageViewBuilder<?>> asImage();
    
}
