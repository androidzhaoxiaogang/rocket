package fast.rocket.cache;

import fast.rocket.builder.RocketImageBuilder;
import fast.rocket.cache.ImageLoader.ImageContainer;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.error.RocketError;
import fast.rocket.utils.RocketUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request. Also images can be circular by adding the attributes
 * in the XML resource file.
 */
public class NetworkCacheView extends ImageView {

	/** Local copy of the ImageLoader. */
	private ImageLoader mImageLoader;

	/** Current ImageContainer. (either in-flight or finished) */
	private ImageContainer mImageContainer;

	private RocketImageBuilder.Builder builder;

	/**
	 * Instantiates a new circular cache view.
	 * 
	 * @param paramContext
	 *            the param context
	 */
	public NetworkCacheView(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new circular cache view.
	 * 
	 * @param paramContext
	 *            the param context
	 * @param paramAttributeSet
	 *            the param attribute set
	 */
	public NetworkCacheView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Instantiates a new circular cache view.
	 * 
	 * @param paramContext
	 *            the param context
	 * @param paramAttributeSet
	 *            the param attribute set
	 * @param paramInt
	 *            the param int
	 */
	public NetworkCacheView(Context context, AttributeSet attrs, int paramInt) {
		super(context, attrs, paramInt);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	public void onDraw(Canvas paramCanvas) {
		if (RocketUtils.hasHoneycomb()) {
			super.onDraw(paramCanvas);
		} else {
			BitmapDrawable drawable = (BitmapDrawable) getDrawable();
			if (drawable == null) {
				if (builder.placeholderResource != 0) {
					setImageResource(builder.placeholderResource);
				} else {
					setImageDrawable(builder.placeholderDrawable);
				}
			} else if ((drawable.getBitmap() == null)
					|| (drawable.getBitmap().isRecycled())) {
				if (builder.placeholderResource != 0) {
					setImageResource(builder.placeholderResource);
				} else {
					setImageDrawable(builder.placeholderDrawable);
				}
			}
			
			try {
				super.onDraw(paramCanvas);
			} catch (RuntimeException localRuntimeException) {
			}
		}
	}

	/**
	 * Sets URL of the image that should be loaded into this view. Note that
	 * calling this will immediately either set the cached image (if available)
	 * or the default image specified by
	 * 
	 * @param url
	 *            The URL that should be loaded into this ImageView.
	 * @param imageLoader
	 *            ImageLoader that will be used to make the request.
	 * @param maxWidth
	 *            the max width
	 * @param maxHeight
	 *            the max height
	 * @param skipDiskCache
	 *            the skip disk cache
	 * @param callback
	 *            the callback
	 * @param config
	 *            the config {@link NetworkCacheView#setDefaultImageResId(int)}
	 *            on the view.
	 * 
	 *            NOTE: If applicable,
	 *            {@link NetworkCacheView#setDefaultImageResId(int)} and
	 *            {@link NetworkCacheView#setErrorImageResId(int)} should be
	 *            called prior to calling this function.
	 */
	public void setImageUrl(ImageLoader imageLoader, RocketImageBuilder.Builder builder) {
		this.builder = builder;
		this.mImageLoader = imageLoader;
		// The URL has potentially changed. See if we need to load it.
		loadImageIfNecessary(false);
	}

	/**
	 * Loads the image for the view if it isn't already loaded.
	 * 
	 * @param isInLayoutPass
	 *            True if this was invoked from a layout pass, false otherwise.
	 */
	private void loadImageIfNecessary(final boolean isInLayoutPass) {
		if(builder == null) {
			return;
		}
		
		int width = getWidth();
		int height = getHeight();

		boolean isFullyWrapContent = getLayoutParams() != null
				&& getLayoutParams().height == LayoutParams.WRAP_CONTENT
				&& getLayoutParams().width == LayoutParams.WRAP_CONTENT;
		// if the view's bounds aren't known yet, and this is not a
		// wrap-content/wrap-content
		// view, hold off on loading the image.
		if (width == 0 && height == 0 && !isFullyWrapContent) {
			return;
		}

		// if the URL to be loaded in this view is empty, cancel any old
		// requests and clear the
		// currently loaded image.
		if (TextUtils.isEmpty(builder.uri)) {
			if (mImageContainer != null) {
				mImageContainer.cancelRequest();
				mImageContainer = null;
			}
			setImageBitmap(null);
			return;
		}

		// if there was an old request in this view, check if it needs to be
		// canceled.
		if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
			if (mImageContainer.getRequestUrl().equals(builder.uri)) {
				// if the request is from the same URL, return.
				return;
			} else {
				// if there is a pre-existing request, cancel it if it's
				// fetching a different URL.
				mImageContainer.cancelRequest();
				setImageBitmap(null);
			}
		}

		// The pre-existing content of this view didn't match the current URL.
		// Load the new image
		// from the network.
		ImageContainer newContainer = mImageLoader.get(
				new ImageListener() {
					@Override
					public void onErrorResponse(RocketError error) {
						if (builder.errorResource != 0) {
							setImageResource(builder.errorResource);
						} else {
							setImageDrawable(builder.errorDrawable);
						}
					}

					@Override
					public void onResponse(final ImageContainer response,
							boolean isImmediate) {
						// If this was an immediate response that was delivered
						// inside of a layout
						// pass do not set the image immediately as it will
						// trigger a requestLayout
						// inside of a layout. Instead, defer setting the image
						// by posting back to
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
							if(builder.listener != null) {
								builder.listener.onComplete();
							}
							
							setImageBitmap(response.getBitmap());
							RocketUtils.loadAnimation(NetworkCacheView.this, 
									builder.inAnimation, builder.inAnimationResource);
							if(builder.callback != null) {
								builder.callback.onComplete(NetworkCacheView.this, 
										response.getBitmap());
							}
						} else {
							if (builder.placeholderResource != 0) {
								setImageResource(builder.placeholderResource);
							} else {
								setImageDrawable(builder.placeholderDrawable);
							}
							
							if(builder.callback != null) {
								builder.callback.onComplete(NetworkCacheView.this, null);
							}
						}
					}
				}, builder);

		// update the ImageContainer to be the new bitmap container.
		mImageContainer = newContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		loadImageIfNecessary(true);
	}

}
