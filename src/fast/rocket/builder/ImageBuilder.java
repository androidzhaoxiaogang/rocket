package fast.rocket.builder;

public interface ImageBuilder<I extends ImageBuilder<?>> extends ImageViewBuilder<I>, BitmapBuilder<I>, ImageViewFutureBuilder {
	public enum ScaleMode {
	    FitXY,
	    CenterCrop,
	    CenterInside
	}

}
