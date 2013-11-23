package fast.rocket.cache;

import fast.rocket.cache.ImageLoader.ImageContainer;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.error.RocketError;
import fast.rocket.utils.RocketUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 */
public class CacheImageView extends ImageView {
	
	private boolean skipDiskCache;
	
    /** The URL of the network image to load */
    private String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;
    
    /** The in animation resource. */
    private int inAnimationResource;
	
	/** The max width. */
	private int maxWidth;
	
	/** The max height. */
	private int maxHeight;
	
	/** The placeholder drawable. */
	private Drawable placeholderDrawable;
	
	/** The error drawable. */
	private Drawable errorDrawable;
	
	/**
	 * The inAnimation。
	 */
	private Animation inAnimation;

    /** Local copy of the ImageLoader. */
    private ImageLoader mImageLoader;

    /** Current ImageContainer. (either in-flight or finished) */
    private ImageContainer mImageContainer;

    public CacheImageView(Context context) {
        this(context, null);
    }

    public CacheImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CacheImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link CacheImageView#setDefaultImageResId(int)} on the view.
     *
     * NOTE: If applicable, {@link CacheImageView#setDefaultImageResId(int)} and
     * {@link CacheImageView#setErrorImageResId(int)} should be called prior to calling
     * this function.
     *
     * @param url The URL that should be loaded into this ImageView.
     * @param imageLoader ImageLoader that will be used to make the request.
     */
	public void setImageUrl(String url, ImageLoader imageLoader, int maxWidth,
			int maxHeight, boolean skipDiskCache) {
        this.mUrl = url;
        this.mImageLoader = imageLoader;
        this.skipDiskCache = skipDiskCache;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }
    
    /**
	 * Placeholder.
	 *
	 * @param drawable the drawable
	 * @return the image request builder
	 */
	public void setPlaceholder(Drawable drawable) {
		if (mDefaultImageId != 0) {
			throw new IllegalStateException("Placeholder image already set.");
		}
		placeholderDrawable = drawable;
	}
	
	/**
	 * Error.
	 *
	 * @param drawable the drawable
	 * @return the image request builder
	 */
	public void setErrorDrawable(Drawable drawable) {
		if (drawable == null) {
			throw new IllegalArgumentException("Error image may not be null.");
		}
		if (mErrorImageId != 0) {
			throw new IllegalStateException("Error image already set.");
		}
		errorDrawable = drawable;
	}

	/**
	 * Animate in.
	 *
	 * @param in the in
	 * @return 
	 */
	public void setAnimateIn(Animation in) {
		inAnimation = in;
	}

	/**
	 * Animate in.
	 *
	 * @param animationResource the animation resource
	 * @return 
	 */
	public void setAnimateIn(int animationResource) {
		inAnimationResource = animationResource;
	}

    /**
     * Loads the image for the view if it isn't already loaded.
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    private void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();

        boolean isFullyWrapContent = getLayoutParams() != null
                && getLayoutParams().height == LayoutParams.WRAP_CONTENT
                && getLayoutParams().width == LayoutParams.WRAP_CONTENT;
        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setImageBitmap(null);
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                mImageContainer.cancelRequest();
                setImageBitmap(null);
            }
        }

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        ImageContainer newContainer = mImageLoader.get(mUrl,
                new ImageListener() {
                    @Override
                    public void onErrorResponse(RocketError error) {
                        if (mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        } else {
                        	setImageDrawable(errorDrawable);
                        }
                    }

                    @Override
                    public void onResponse(final ImageContainer response, boolean isImmediate) {
                        // If this was an immediate response that was delivered inside of a layout
                        // pass do not set the image immediately as it will trigger a requestLayout
                        // inside of a layout. Instead, defer setting the image by posting back to
                        // the main thread.
                        if (isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }

                        if (response.getBitmap() != null) {
                            setImageBitmap(response.getBitmap());
                            RocketUtils.loadAnimation(CacheImageView.this, 
                            		inAnimation, inAnimationResource);
                        } else if (mDefaultImageId != 0) {
                            setImageResource(mDefaultImageId);
                        } else {
                        	setImageDrawable(placeholderDrawable);
                        }
                    }
                }, maxWidth, maxHeight, skipDiskCache);

        // update the ImageContainer to be the new bitmap container.
        mImageContainer = newContainer;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }
    
    /**
     * Handle the OOM issue before 3.0, and to avoid the used the recycled bitmap
     * runtime exception {@link  BitmapLruCache#entryRemoved(boolean , 
     * String , Bitmap , Bitmap )}.
     */
	@Override
	protected void onDraw(Canvas canvas) {
		if (RocketUtils.hasHoneycomb()) {
			super.onDraw(canvas);
		} else {
			BitmapDrawable drawable = (BitmapDrawable) getDrawable();
			if (drawable == null) {
				setImageResource(mDefaultImageId);
			} else if ((drawable.getBitmap() == null)
					|| (drawable.getBitmap().isRecycled())) {
				setImageResource(mDefaultImageId);
			}
			try {
				super.onDraw(canvas);
			} catch (RuntimeException localRuntimeException) {
			}
		}
	}

	@Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            // also clear out the container so we can reload the image if necessary.
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
