package fast.rocket.builder;

public interface BitmapBuilder<B extends BitmapBuilder<?>> {
    /**
     * Resize the bitmap to the given dimensions.
     * @param width
     * @param height
     * @return
     */
    public B resize(int width, int height);
    
    /** Resize the image to the specified dimension size.
     * @param targetWidthResId
     * @param targetHeightResId
     * @return
     */		
    public B resizeDimen(int targetWidthResId, int targetHeightResId);

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation. This will scale the image so that it fills the bounds, and crops
     * the extra.
     * @return
     */
    public B centerCrop();

    /**
     * Center the image inside of the bounds specified by the ImageView or resize
     * operation. This will scale the image so that one dimension is as large as the requested
     * bounds.
     * @return
     */
    public B centerInside();
    
}
