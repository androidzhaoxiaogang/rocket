package fast.rocket.config;

import java.lang.ref.WeakReference;

import fast.rocket.Rocket;
import fast.rocket.cache.ImageLoader;
import fast.rocket.cache.ImageLoader.ImageListener;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageRequestBuilder implements LaunchBuilder{
	public interface ScaleMode {
		int FitXY = 0x01;
		int CenterCrop = 0x02;
		int CenterInside = 0x03;
	}
	
	private Drawable placeholderDrawable;
	private int placeholderResource;
	private Drawable errorDrawable;
	private int errorResource;
	private Animation inAnimation;
	private Animation loadAnimation;
	private int inAnimationResource;
	private int loadAnimationResource;
	private boolean skipMemoryCache;
	private boolean skipDiskCache;
	private boolean fadeInImage = true;
	/** The rocket. */
	public Rocket rocket;
	
    private int scaleMode = ScaleMode.FitXY;
    private int resizeWidth;
    private int resizeHeight;

	private WeakReference<ImageView> imageViewRef;

	public ImageRequestBuilder withImageView(ImageView imageView) {
		imageViewRef = new WeakReference<ImageView>(imageView);
		return this;
	}

	public ImageRequestBuilder placeholder(Drawable drawable) {
		if (placeholderResource != 0) {
			throw new IllegalStateException("Placeholder image already set.");
		}
		placeholderDrawable = drawable;
		return this;
	}

	public ImageRequestBuilder placeholder(int resourceId) {
		if (resourceId == 0) {
			throw new IllegalArgumentException(
					"Placeholder image resource invalid.");
		}
		if (placeholderDrawable != null) {
			throw new IllegalStateException("Placeholder image already set.");
		}
		placeholderResource = resourceId;
		return this;
	}

	public ImageRequestBuilder error(Drawable drawable) {
		if (errorDrawable == null) {
			throw new IllegalArgumentException("Error image may not be null.");
		}
		if (errorResource != 0) {
			throw new IllegalStateException("Error image already set.");
		}
		errorDrawable = drawable;
		return this;
	}

	public ImageRequestBuilder error(int resourceId) {
		errorResource = resourceId;
		return this;
	}
	
	public ImageRequestBuilder noFadeIn() {
		this.fadeInImage = false;
        return this;
    }

	public ImageRequestBuilder animateIn(Animation in) {
		inAnimation = in;
		return this;
	}

	public ImageRequestBuilder animateIn(int animationResource) {
		inAnimationResource = animationResource;
		fadeInImage = true;
		return this;
	}

	public ImageRequestBuilder animateLoad(Animation load) {
		loadAnimation = load;
		fadeInImage = true;
		return this;
	}

	public ImageRequestBuilder animateLoad(int animationResource) {
		loadAnimationResource = animationResource;
		fadeInImage = true;
		return this;
	}

	/**
	 * Indicate that this action should not use the memory cache for attempting
	 * to load or save the image. This can be useful when you know an image will
	 * only ever be used once (e.g., loading an image from the filesystem and
	 * uploading to a remote server).
	 */
	public ImageRequestBuilder skipMemoryCache() {
		skipMemoryCache = true;
		return this;
	}
	
	/**
	 * Indicate that this action should not use the disk cache for attempting
	 * to load or save the image. This can be useful when you know an image will
	 * only ever be used once (e.g., loading an image from the filesystem and
	 * uploading to a remote server).
	 */
	public ImageRequestBuilder skipDiskCache() {
		skipDiskCache = true;
		return this;
	}
	
    public ImageRequestBuilder centerCrop() {
        if (resizeWidth == 0 || resizeHeight == 0)
            throw new IllegalStateException("must call resize first");
        scaleMode = ScaleMode.CenterCrop;
        return this;
    }

    public ImageRequestBuilder centerInside() {
        if (resizeWidth == 0 || resizeHeight == 0)
            throw new IllegalStateException("must call resize first");
        scaleMode = ScaleMode.CenterInside;
        return this;
    }

    public ImageRequestBuilder resize(int width, int height) {
        resizeWidth = width;
        resizeHeight = height;
        return this;
    }

	@Override
	public void load(String uri) {
		load(0, uri);
	}

	@Override
	public void load(int method, String uri) {
		final ImageView imageView = imageViewRef.get();
		final ImageLoader loader = rocket.getImageLoader();
		final ImageListener  listener = ImageLoader.getImageListener(imageView, 
				placeholderDrawable, placeholderResource,
				errorDrawable, errorResource, fadeInImage);
		
		loader.get(uri, listener, resizeWidth, resizeHeight, 
				skipMemoryCache, skipDiskCache);
		imageAnimation(imageView, loadAnimation, loadAnimationResource);
	}
	
	//**************************************private apis***************************************//
	private void imageAnimation(ImageView imageView, Animation animation, int animationResource) {
        if (imageView == null)
            return;
        if (animation == null && animationResource != 0)
            animation = AnimationUtils.loadAnimation(imageView.getContext(), animationResource);
        if (animation == null) {
            imageView.setAnimation(null);
            return;
        }

        imageView.startAnimation(animation);
    }
	
	private Bitmap transform(Bitmap b) {
        Bitmap ret = Bitmap.createBitmap(resizeWidth, resizeHeight, b.getConfig());
        Canvas canvas = new Canvas(ret);

        float xratio = (float)resizeWidth / (float)b.getWidth();
        float yratio = (float)resizeHeight / (float)b.getHeight();
        float transx = 0;
        float transy = 0;
        if (scaleMode != ScaleMode.FitXY) {
            float ratio;
            if (scaleMode == ScaleMode.CenterCrop)
                ratio = Math.max(xratio, yratio);
            else
                ratio = Math.min(xratio, yratio);

            xratio = ratio;
            yratio = ratio;

            float postx = b.getWidth() * ratio;
            float posty = b.getHeight() * ratio;
            transx = (resizeWidth - postx) / 2;
            transy = (resizeHeight - posty) / 2;
        }

        canvas.scale(xratio, yratio);
        canvas.drawBitmap(b, transx, transy, null);

        return ret;
    }

}
