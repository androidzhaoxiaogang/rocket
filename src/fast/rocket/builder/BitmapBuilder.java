package fast.rocket.builder;

public interface BitmapBuilder<B extends BitmapBuilder<?>> {
    /**
     * Resize the bitmap to the given dimensions.
     * @param width
     * @param height
     * @return
     */
    public B resize(int width, int height);

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

    /**
     * Enable/disable automatic resizing to the dimensions of the device when loading the image.
     * @param smartSize
     * @return
     */
    public B smartSize(boolean smartSize);
}
