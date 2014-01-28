package fast.rocket.builder;

public interface RequestBuilder<R extends RequestBuilder<?>>{
    public JsonBuilder<? extends JsonBuilder<?>> asJson(Class<?> clazz);

    public ImageViewBuilder<? extends ImageViewBuilder<?>> asImage();
    
}
