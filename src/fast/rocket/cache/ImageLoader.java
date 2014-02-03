
package fast.rocket.cache;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.widget.ImageView;


import java.util.HashMap;
import java.util.LinkedList;

import fast.rocket.builder.RocketImageBuilder;
import fast.rocket.error.RocketError;
import fast.rocket.request.ImageRequest;
import fast.rocket.request.Request;
import fast.rocket.request.RequestQueue;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;
import fast.rocket.utils.RocketUtils;


/**
 * Helper that handles loading and caching images from remote URLs.
 *
 * The simple way to use this class is to call {@link ImageLoader#get(String, ImageListener)}
 * and to pass in the default image listener provided by
 * {@link ImageLoader#getImageListener(ImageView, int, int)}. Note that all function calls to
 * this class must be made from the main thead, and all responses will be delivered to the main
 * thread as well.
 */
public class ImageLoader {
	
    /** RequestQueue for dispatching ImageRequests onto. */
    private final RequestQueue mRequestQueue;

    /** Amount of time to wait after first response arrives before delivering all responses. */
    private int mBatchResponseDelayMs = 100;

    /** The cache implementation to be used as an L1 cache before calling into Rocket. */
    private final ImageCache mCache;

    /**
     * HashMap of Cache keys -> BatchedImageRequest used to track in-flight requests so
     * that we can coalesce multiple requests to the same URL into a single network request.
     */
    private final HashMap<String, BatchedImageRequest> mInFlightRequests =
            new HashMap<String, BatchedImageRequest>();

    /** HashMap of the currently pending responses (waiting to be delivered). */
    private final HashMap<String, BatchedImageRequest> mBatchedResponses =
            new HashMap<String, BatchedImageRequest>();

    /** Handler to the main thread. */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /** Runnable for in-flight response delivery. */
    private Runnable mRunnable;

    /**
     * Simple cache adapter interface. If provided to the ImageLoader, it
     * will be used as an L1 cache before dispatch to rocket. Implementations
     * must not block. Implementation with an LruCache is recommended.
     */
    public interface ImageCache {
        
        /**
         * Gets the bitmap.
         *
         * @param url the url
         * @return the bitmap
         */
        public Bitmap getBitmap(String url);
        
        /**
         * Put bitmap.
         *
         * @param url the url
         * @param bitmap the bitmap
         */
        public void putBitmap(String url, Bitmap bitmap);
    }
    
    /**
     * The Interface ImageCallback.
     */
    public interface ImageCallback{
    	
	    /**
	     * On complete.
	     *
	     * @param error the error
	     * @param result the result
	     */
	    public void onComplete(ImageView view, Bitmap result, boolean isImmediate);
    }

    /**
     * Constructs a new ImageLoader.
     * @param queue The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public ImageLoader(RequestQueue queue, ImageCache imageCache) {
        mRequestQueue = queue;
        mCache = imageCache;
    }

    /**
     * The default implementation of ImageListener which handles basic functionality
     * of showing a default image until the network response is received, at which point
     * it will switch to either the actual image or the error image.
     *
     * @param view the view
     * @param placeholderDrawable the placeholder drawable
     * @param defaultImageResId Default image resource ID to use, or 0 if it doesn't exist.
     * @param errorDrawable the error drawable
     * @param errorImageResId Error image resource ID to use, or 0 if it doesn't exist.
     * @param animation the animation
     * @param animationResource the animation resource
     * @return the image listener
     */
	public static ImageListener getImageListener(final ImageView view,
			final Drawable placeholderDrawable, final int defaultImageResId,
			final Drawable errorDrawable, final int errorImageResId,
			final Animation animation, final int animationResource,
			final ImageCallback callback) {
		return new ImageListener() {
			@Override
			public void onErrorResponse(RocketError error) {
				if (errorImageResId != 0) {
					view.setImageResource(errorImageResId);
				} else {
					view.setImageDrawable(errorDrawable);
				}
				
				if(callback != null) {
					callback.onComplete(view, null, false);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					setImageBitmap(view, response.getBitmap(), animation,
							animationResource);
				} else if (defaultImageResId != 0) {
					view.setImageResource(defaultImageResId);
				} else {
					view.setImageDrawable(placeholderDrawable);
				}
				
				if(callback != null) {
					callback.onComplete(view, response.getBitmap(), isImmediate);
				}
			}
		};
	}
	
	/**
     * The default implementation of ImageListener which handles basic functionality
     * of showing a default image until the network response is received, at which point
     * it will switch to either the actual image or the error image.
     *
     * @param view the view
     * @param placeholderDrawable the placeholder drawable
     * @param defaultImageResId Default image resource ID to use, or 0 if it doesn't exist.
     * @param errorDrawable the error drawable
     * @param errorImageResId Error image resource ID to use, or 0 if it doesn't exist.
     * @param animation the animation
     * @param animationResource the animation resource
     * @return the image listener
     */
	public static ImageListener getImageListener(final ImageView view,
			final Drawable placeholderDrawable, final int defaultImageResId,
			final Drawable errorDrawable, final int errorImageResId,
			final Animation animation, final int animationResource) {
		return new ImageListener() {
			@Override
			public void onErrorResponse(RocketError error) {
				if (errorImageResId != 0) {
					view.setImageResource(errorImageResId);
				} else {
					view.setImageDrawable(errorDrawable);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					setImageBitmap(view, response.getBitmap(), animation,
							animationResource);
				} else if (defaultImageResId != 0) {
					view.setImageResource(defaultImageResId);
				} else {
					view.setImageDrawable(placeholderDrawable);
				}
			}
		};
	}
	
	/**
     * The default implementation of ImageListener which handles basic functionality
     * of showing a default image until the network response is received, at which point
     * it will switch to either the actual image or the error image.
     *
     * @param view the view
     * @param placeholderDrawable the placeholder drawable
     * @param defaultImageResId Default image resource ID to use, or 0 if it doesn't exist.
     * @param errorDrawable the error drawable
     * @param errorImageResId Error image resource ID to use, or 0 if it doesn't exist.
     * @param animation the animation
     * @param animationResource the animation resource
     * @return the image listener
     */
	public static ImageListener getImageListener(final ImageView view, final RocketImageBuilder.Builder builder) {
		return new ImageListener() {
			@Override
			public void onErrorResponse(RocketError error) {
				if (builder.errorResource != 0) {
					view.setImageResource(builder.errorResource);
				} else {
					view.setImageDrawable(builder.errorDrawable);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					setImageBitmap(view, response.getBitmap(), 
							builder.outAnimation, builder.outAnimationResource);
				} else if (builder.placeholderResource != 0) {
					view.setImageResource(builder.placeholderResource);
				} else {
					view.setImageDrawable(builder.placeholderDrawable);
				}
			}
		};
	}
    
    /**
     * Sets a {@link android.graphics.Bitmap} to an {@link android.widget.ImageView} using a
     * fade-in animation. If there is a {@link android.graphics.drawable.Drawable} already set on
     * the ImageView then use that as the image to fade from. Otherwise fade in from a transparent
     * Drawable.
     *
     * @param imageView the image view
     * @param bitmap the bitmap
     * @param animation the animation
     * @param animationResource the animation resource
     */
	private static void setImageBitmap(final ImageView imageView, final Bitmap bitmap,
			Animation animation, int animationResource) {
    	imageView.setImageBitmap(bitmap);
    	RocketUtils.loadAnimation(imageView, animation, animationResource);
    }

    /**
     * Interface for the response handlers on image requests.
     * 
     * The call flow is this:
     * 1. Upon being  attached to a request, onResponse(response, true) will
     * be invoked to reflect any cached data that was already available. If the
     * data was available, response.getBitmap() will be non-null.
     * 
     * 2. After a network response returns, only one of the following cases will happen:
     * - onResponse(response, false) will be called if the image was loaded.
     * or
     * - onErrorResponse will be called if there was an error loading the image.
     *
     * @see ImageEvent
     */
    public interface ImageListener extends ErrorListener {
        /**
         * Listens for non-error changes to the loading of the image request.
         *
         * @param response Holds all information pertaining to the request, as well
         * as the bitmap (if it is loaded).
         * @param isImmediate True if this was called during ImageLoader.get() variants.
         * This can be used to differentiate between a cached image loading and a network
         * image loading in order to, for example, run an animation to fade in network loaded
         * images.
         */
        public void onResponse(ImageContainer response, boolean isImmediate);
    }

    /**
     * Checks if the item is available in the cache.
     * @param requestUrl The url of the remote image
     * @param maxWidth The maximum width of the returned image.
     * @param maxHeight The maximum height of the returned image.
     * @return True if the item exists in cache, false otherwise.
     */
    public boolean isCached(String requestUrl, int maxWidth, int maxHeight) {
        throwIfNotOnMainThread();

        String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight);
        return mCache.getBitmap(cacheKey) != null;
    }

    /**
     * Returns an ImageContainer for the requested URL.
     * 
     * The ImageContainer will contain either the specified default bitmap or the loaded bitmap.
     * If the default was returned, the {@link ImageLoader} will be invoked when the
     * request is fulfilled.
     *
     * @param requestUrl The URL of the image to be loaded.
     * @param listener the listener
     * @param maxWidth the max width
     * @param maxHeight the max height
     * @param skipDiskCache the skip disk cache
     * @return the image container
     */
//    public ImageContainer get(String requestUrl, final ImageListener listener, 
//    		int maxWidth, int maxHeight,final boolean skipDiskCache) {
//        return get(requestUrl, listener, maxWidth, maxHeight, false, skipDiskCache);
//    }
    
    /**
     * Issues a bitmap request with the given URL if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     *
     * @param requestUrl The url of the remote image
     * @param imageListener The listener to call when the remote image is loaded
     * @param maxWidth The maximum width of the returned image.
     * @param maxHeight The maximum height of the returned image.
     * @param skipMemoryCache the skip memory cache
     * @param skipDiskCache the skip disk cache
     * @return A container object that contains all of the properties of the request, as well as
     * the currently available image (default if remote is not loaded).
     */
//    public ImageContainer get(String requestUrl, ImageListener imageListener,
//            int maxWidth, int maxHeight, boolean skipMemoryCache) {
//    	return get( requestUrl,  imageListener,  maxWidth,  maxHeight) ;
//    }
    /**
     * Issues a bitmap request with the given URL if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     * @param requestUrl The url of the remote image
     * @param imageListener The listener to call when the remote image is loaded
     * @param maxWidth The maximum width of the returned image.
     * @param maxHeight The maximum height of the returned image.
     * @return A container object that contains all of the properties of the request, as well as
     *     the currently available image (default if remote is not loaded).
     */
    public ImageContainer get(ImageListener imageListener, final RocketImageBuilder.Builder builder) {
        // only fulfill requests that were initiated from the main thread.
        throwIfNotOnMainThread();

        final String cacheKey = getCacheKey(builder.uri, builder.resizeWidth, builder.resizeHeight);

        // Try to look up the request in the cache of remote images.
        Bitmap cachedBitmap = mCache.getBitmap(cacheKey);
        if (cachedBitmap != null) {
            // Return the cached bitmap.
            ImageContainer container = new ImageContainer(cachedBitmap, builder.uri, null, null);
            imageListener.onResponse(container, true);
            return container;
        }

        // The bitmap did not exist in the cache, fetch it!
        ImageContainer imageContainer =
                new ImageContainer(null, builder.uri, cacheKey, imageListener);

        // Update the caller to let them know that they should use the default bitmap.
        imageListener.onResponse(imageContainer, true);

        // Check to see if a request is already in-flight.
        BatchedImageRequest request = mInFlightRequests.get(cacheKey);
        if (request != null) {
            // If it is, add this request to the list of listeners.
            request.addContainer(imageContainer);
            return imageContainer;
        }

        // The request is not already in flight. Send the new request to the network and
        // track it.
        Request<?> newRequest =
            new ImageRequest(builder.uri, new Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    onGetImageSuccess(cacheKey, response, builder.skipMemoryCache);
                }
            }, builder.resizeWidth, builder.resizeHeight,
            Config.RGB_565, new ErrorListener() {
                @Override
                public void onErrorResponse(RocketError error) {
                    onGetImageError(cacheKey, error);
                }
            });
        newRequest.setCachePolicy(builder.cachePolicy);
        mRequestQueue.add(newRequest);
        mInFlightRequests.put(cacheKey,
                new BatchedImageRequest(newRequest, imageContainer));
        return imageContainer;
    }
    

    /**
     * Sets the amount of time to wait after the first response arrives before delivering all
     * responses. Batching can be disabled entirely by passing in 0.
     * @param newBatchedResponseDelayMs The time in milliseconds to wait.
     */
    public void setBatchedResponseDelay(int newBatchedResponseDelayMs) {
        mBatchResponseDelayMs = newBatchedResponseDelayMs;
    }

    /**
     * Handler for when an image was successfully loaded.
     *
     * @param cacheKey The cache key that is associated with the image request.
     * @param response The bitmap that was returned from the network.
     * @param skipMemoryCache the skip memory cache
     */
    private void onGetImageSuccess(String cacheKey, Bitmap response, 
    		final boolean skipMemoryCache) {
        // cache the image that was fetched.
		if (!skipMemoryCache) {
			mCache.putBitmap(cacheKey, response);
		}

        // remove the request from the list of in-flight requests.
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
            // Update the response bitmap.
            request.mResponseBitmap = response;

            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }
    
    /**
     * Handler for when an image failed to load.
     *
     * @param cacheKey The cache key that is associated with the image request.
     * @param error the error
     */
    private void onGetImageError(String cacheKey, RocketError error) {
        // Notify the requesters that something failed via a null result.
        // Remove this request from the list of in-flight requests.
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
        	// Set the error for this request
            request.setError(error);
            
            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    /**
     * Container object for all of the data surrounding an image request.
     */
    public class ImageContainer {
        /**
         * The most relevant bitmap for the container. If the image was in cache, the
         * Holder to use for the final bitmap (the one that pairs to the requested URL).
         */
        private Bitmap mBitmap;

        /** The m listener. */
        private final ImageListener mListener;

        /** The cache key that was associated with the request. */
        private final String mCacheKey;

        /** The request URL that was specified. */
        private final String mRequestUrl;

        /**
         * Constructs a BitmapContainer object.
         *
         * @param bitmap The final bitmap (if it exists).
         * @param requestUrl The requested URL for this container.
         * @param cacheKey The cache key that identifies the requested URL for this container.
         * @param listener the listener
         */
        public ImageContainer(Bitmap bitmap, String requestUrl,
                String cacheKey, ImageListener listener) {
            mBitmap = bitmap;
            mRequestUrl = requestUrl;
            mCacheKey = cacheKey;
            mListener = listener;
        }

        /**
         * Releases interest in the in-flight request (and cancels it if no one else is listening).
         */
        public void cancelRequest() {
            if (mListener == null) {
                return;
            }

            BatchedImageRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                // check to see if it is already batched for delivery.
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    request.removeContainerAndCancelIfNecessary(this);
                    if (request.mContainers.size() == 0) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }

        /**
         * Returns the bitmap associated with the request URL if it has been loaded, null otherwise.
         *
         * @return the bitmap
         */
        public Bitmap getBitmap() {
            return mBitmap;
        }

        /**
         * Returns the requested URL for this container.
         *
         * @return the request url
         */
        public String getRequestUrl() {
            return mRequestUrl;
        }
    }

    /**
     * Wrapper class used to map a Request to the set of active ImageContainer objects that are
     * interested in its results.
     */
    private class BatchedImageRequest {
        
        /** The request being tracked. */
        private final Request<?> mRequest;

        /** The result of the request being tracked by this item. */
        private Bitmap mResponseBitmap;

        /** Error if one occurred for this response. */
        private RocketError mError;

        /** List of all of the active ImageContainers that are interested in the request. */
        private final LinkedList<ImageContainer> mContainers = new LinkedList<ImageContainer>();

        /**
         * Constructs a new BatchedImageRequest object.
         *
         * @param request The request being tracked
         * @param container The ImageContainer of the person who initiated the request.
         */
        public BatchedImageRequest(Request<?> request, ImageContainer container) {
            mRequest = request;
            mContainers.add(container);
        }

        /**
         * Set the error for this response.
         *
         * @param error the new error
         */
        public void setError(RocketError error) {
            mError = error;
        }

        /**
         * Get the error for this response.
         *
         * @return the error
         */
        public RocketError getError() {
            return mError;
        }

        /**
         * Adds another ImageContainer to the list of those interested in the results of
         * the request.
         *
         * @param container the container
         */
        public void addContainer(ImageContainer container) {
            mContainers.add(container);
        }

        /**
         * Detatches the bitmap container from the request and cancels the request if no one is
         * left listening.
         * @param container The container to remove from the list
         * @return True if the request was canceled, false otherwise.
         */
        public boolean removeContainerAndCancelIfNecessary(ImageContainer container) {
            mContainers.remove(container);
            if (mContainers.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }

    /**
     * Starts the runnable for batched delivery of responses if it is not already started.
     *
     * @param cacheKey The cacheKey of the response being delivered.
     * @param request The BatchedImageRequest to be delivered.
     */
    private void batchResponse(String cacheKey, BatchedImageRequest request) {
        mBatchedResponses.put(cacheKey, request);
        // If we don't already have a batch delivery runnable in flight, make a new one.
        // Note that this will be used to deliver responses to all callers in mBatchedResponses.
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    for (BatchedImageRequest bir : mBatchedResponses.values()) {
                        for (ImageContainer container : bir.mContainers) {
                            // If one of the callers in the batched request canceled the request
                            // after the response was received but before it was delivered,
                            // skip them.
                            if (container.mListener == null) {
                                continue;
                            }
                            if (bir.getError() == null) {
                                container.mBitmap = bir.mResponseBitmap;
                                container.mListener.onResponse(container, false);
                            } else {
                                container.mListener.onErrorResponse(bir.getError());
                            }
                        }
                    }
                    mBatchedResponses.clear();
                    mRunnable = null;
                }

            };
            // Post the runnable.
            mHandler.postDelayed(mRunnable, mBatchResponseDelayMs);
        }
    }

    /**
     * Throw if not on main thread.
     */
    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }
    
    /**
     * Creates a cache key for use with the L1 cache.
     *
     * @param url The URL of the request.
     * @param maxWidth The max-width of the output.
     * @param maxHeight The max-height of the output.
     * @return the cache key
     */
    private static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append(url).toString();
    }
}
