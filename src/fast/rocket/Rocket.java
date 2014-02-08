package fast.rocket;

import java.io.File;

import fast.rocket.builder.LoadBuilder;
import fast.rocket.builder.RequestBuilder;
import fast.rocket.builder.RocketRequestBuilder;
import fast.rocket.cache.*;
import fast.rocket.http.BasicNetwork;
import fast.rocket.http.HttpClientStack;
import fast.rocket.http.HttpStack;
import fast.rocket.http.HurlStack;
import fast.rocket.http.Network;
import fast.rocket.request.RequestQueue;
import fast.rocket.utils.AndroidHttpClient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

/**
 * The Class Rocket.
 */
public class Rocket {
	/** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "rocket";

	/** The request dispatch queue. */
	private RequestQueue requestQueue;
	
	/** The image loader. */
	private ImageLoader imageLoader;
	
	/** The Rocket instance. */
	private static Rocket instance;
	
	/** The Rocket library name. */
	private String name;
	
    /** The network holds the http client for network request and response. */
    private Network network;
    
    /** The disk cache to store instant images or objects. */
    private Cache cache;
    
    private Context context;

	/**
	 *  Get the default Rocket object instance and begin building a request.
	 *
	 * @param context the context
	 * @return the rocket request builder
	 */
	public static LoadBuilder<RequestBuilder> with(Context context) {
	     return getDefault(context).build(context);
	}
	 
	public LoadBuilder<RequestBuilder> build(Context context) {
        return new RocketRequestBuilder(context, this);
    }
	
	/**
	 * Get the default Rocket instance.
	 *
	 * @param context the context
	 * @return Rocket instance
	 */
    public static Rocket getDefault(Context context) {
        if (instance == null)
            instance = new Rocket(context, "Rocket V1.0");
        return instance;
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public  RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public RequestQueue newRequestQueue(Context context, HttpStack stack) {
        String userAgent = "rocket";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }
        
        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See:
                // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        network = new BasicNetwork(stack);
        cache = new DiskBasedCache(getDiskCacheDir(context, DEFAULT_CACHE_DIR));

        final RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        return queue;
    }
    
	/**
	 * Invalidate the disk cache data by the url individually.
	 *
	 * @param url the request url
	 */
	public void invalidate(String url) {
		if(requestQueue == null) {
			throw new IllegalStateException("request queue must be init first"); 
		}
		
		final Cache.Entry entry = requestQueue.getCache().get(url);
		if (entry != null && entry.data != null && entry.data.length > 0) {
			if (!entry.isExpired()) {
				requestQueue.getCache().invalidate(url, true);
			}
		}
	}
	
	/**
	 * Gets the bytes data in the disk cache.
	 *
	 * @param key the key
	 * @return the data in disk cache
	 */
	public byte[] getDiskCacheData(String key) {
		if(requestQueue == null) {
			throw new IllegalStateException("request queue must be init first"); 
		}
		
		Cache.Entry entry = requestQueue.getCache().get(key);
		return entry == null ? null : entry.data;
	}
    
    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context) {
    	return getDiskCacheDir(context, DEFAULT_CACHE_DIR);
    }
    
    /**
     * Cancel all pending requests in the request queue.
     *
     * @param tag the tag
     */
	public void cancelAll(final Object tag) {
		if (requestQueue != null) {
			requestQueue.cancelAll(tag);
		}
	}
    
    /**
     * Builds the json request builder.
     *
     * @return the rocket request builder
     */
    public RocketRequestBuilder build() {
    	return new RocketRequestBuilder(context, this);
    }
    
    /**
     * Gets the Rocket name.
     *
     * @return the name
     */
    public String getName() {
		return name;
	}
	
	
	/**
	 * Gets the request queue.
	 *
	 * @return the request queue
	 */
	public RequestQueue getRequestQueue() {
		return requestQueue;
	}
	
	/**
	 * Gets the basic network.
	 *
	 * @return the basic network
	 */
	public Network getBasicNetwork() {
		return network;
	}

	/**
	 * Gets the cache.
	 *
	 * @return the cache
	 */
	public Cache getCache() {
		return cache;
	}
	
	/**
	 * Gets the image loader.
	 *
	 * @return the image loader
	 */
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	
	//*********************private apis*****************************//
	/**
	 * Instantiates a new rocket and the request queue.
	 *
	 * @param context the context
	 * @param name the name
	 */
	private Rocket(Context context, String name) {
		context = context.getApplicationContext();
		this.name = name;
		this.requestQueue = newRequestQueue(context);
		this.imageLoader = new ImageLoader(requestQueue, new BitmapLruCache());
		this.context = context;
	}
	
	/**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    private static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir

        // getCacheDir() should be moved to a background thread as it attempts to create the
        // directory if it does not exist (no disk access should happen on the main/UI thread).
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                        ? getExternalCacheDir(context).getPath()
                        : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
	
    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    private static File getExternalCacheDir(Context context) {
        // This needs to be moved to a background thread to ensure no disk access on the
        // main/UI thread as unfortunately getExternalCacheDir() calls mkdirs() for us (even
        // though the Rocket library will later try and call mkdirs() as well from a background
        // thread).
        return context.getExternalCacheDir();
    }

}
