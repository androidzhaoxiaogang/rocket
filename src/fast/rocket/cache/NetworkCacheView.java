package fast.rocket.cache;

import com.android.rocket.R;

import fast.rocket.builder.RocketImageBuilder;
import fast.rocket.cache.ImageLoader.ImageContainer;
import fast.rocket.cache.ImageLoader.ImageListener;
import fast.rocket.error.RocketError;
import fast.rocket.utils.RocketUtils;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request. Also images can be circular by adding the attributes
 * in the XML resource file.
 */
public class NetworkCacheView extends ImageView {

	/** The image width. */
	private int width;
	
	/** The image height. */
	private int height;

	/** Local copy of the ImageLoader. */
	private ImageLoader mImageLoader;

	/** Current ImageContainer. (either in-flight or finished) */
	private ImageContainer mImageContainer;

	/** The bitmap. */
	private Bitmap bitmap;

	/** The border width. */
	private float borderWidth = 2.0F;

	/** The center. */
	private float center;

	/** default set draw border false. */
	private boolean drawBorder = false;

	/** default set image to draw circle false. */
	private boolean drawCircle = false;

	/** default border color. */
	private int borderColor = 0xFF129FCD;

	/** The paint. */
	private Paint paint;

	/** The paint border. */
	private Paint paintBorder;

	/** The shader. */
	private BitmapShader shader;
	
	private RocketImageBuilder.Builder builder;

	/**
	 * Instantiates a new circular cache view.
	 * 
	 * @param paramContext
	 *            the param context
	 */
	public NetworkCacheView(Context context) {
		super(context);
		initialize(context, null);
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
		initialize(context, attrs);
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
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.rocket);
		drawCircle = a.getBoolean(R.styleable.rocket_drawCircle, drawCircle);
		drawBorder = a.getBoolean(R.styleable.rocket_drawCircleBorder, drawBorder);
		borderColor = a.getInt(R.styleable.rocket_borderColor, borderColor);
		a.recycle();

		if (drawCircle) initialize();
	}

	/**
	 * Sets the shader.
	 */
	private void setShader() {
		BitmapDrawable drawable = (BitmapDrawable) getDrawable();
		if (drawable != null) {
			this.bitmap = drawable.getBitmap();
		}

		if ((this.bitmap != null) && (this.width > 0) && (this.height > 0)) {
			this.shader = new BitmapShader(Bitmap.createScaledBitmap(
					this.bitmap, this.width, this.height, false),
					BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			this.paint.setShader(this.shader);
		}
	}

	/**
	 * Initialize the paint and paint border.
	 */
	private void initialize() {
		Resources localResources = getResources();
		this.borderWidth = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, this.borderWidth,
				localResources.getDisplayMetrics());
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paintBorder = new Paint();
		this.paintBorder.setColor(borderColor);
		this.paintBorder.setStyle(Paint.Style.STROKE);
		this.paintBorder.setStrokeWidth(this.borderWidth);
		this.paintBorder.setAntiAlias(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	public void onDraw(Canvas paramCanvas) {
		if (RocketUtils.hasHoneycomb()) {
			if (drawCircle)
				drawCircle(paramCanvas);
			else
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
				if (drawCircle)
					drawCircle(paramCanvas);
				else
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
		// The URL has potentially changed. See if we need to load it.
		loadImageIfNecessary(false);
	}

	/**
	 * Draw circle.
	 * 
	 * @param paramCanvas
	 *            the param canvas
	 */
	private void drawCircle(Canvas paramCanvas) {
		if ((this.bitmap != null) && (this.shader != null)) {
			float f1 = this.center - 2 * (int) this.borderWidth;
			float f2 = this.center - ((int) this.borderWidth >> 1);
			paramCanvas.drawCircle(this.center, this.center, f1, this.paint);
			if (this.drawBorder)
				paramCanvas.drawCircle(this.center, this.center, f2
						- this.borderWidth, this.paintBorder);
		}
	}

	/**
	 * Loads the image for the view if it isn't already loaded.
	 * 
	 * @param isInLayoutPass
	 *            True if this was invoked from a layout pass, false otherwise.
	 */
	private void loadImageIfNecessary(final boolean isInLayoutPass) {
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
							setImageBitmap(response.getBitmap());
							RocketUtils.loadAnimation(NetworkCacheView.this, 
									builder.inAnimation, builder.inAnimationResource);
						} else {
							if (builder.placeholderResource != 0) {
								setImageResource(builder.placeholderResource);
							} else {
								setImageDrawable(builder.placeholderDrawable);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		this.width = paramInt1;
		this.height = paramInt2;
		this.center = (this.width >> 1);
		setShader();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable
	 * )
	 */
	public void setImageDrawable(Drawable paramDrawable) {
		super.setImageDrawable(paramDrawable);
		if ((paramDrawable instanceof BitmapDrawable)) {
			this.bitmap = ((BitmapDrawable) paramDrawable).getBitmap();
			setShader();
			return;
		}
		this.shader = null;
		invalidate();
	}

}
