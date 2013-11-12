package fast.rocket;


import java.io.File;

import fast.rocket.cache.Cache;
import fast.rocket.cache.DiskBasedCache;
import fast.rocket.config.RocketRequestBuilder;
import fast.rocket.http.BasicNetwork;
import fast.rocket.http.HttpClientStack;
import fast.rocket.http.HttpStack;
import fast.rocket.http.HurlStack;
import fast.rocket.http.Network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

/**
 * The Class Rocket.
 */
public class Rocket {
	/** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "rocket";

	/** The request queue. */
	private RequestQueue requestQueue;
	
	/** The Rocket instance. */
	private static Rocket instance;
	
	/** The Rocket name. */
	private String name;
	
    /** The network. */
    private Network network;
    
    /** The cache. */
    private Cache cache;

	/**
	 * With.
	 *
	 * @param context the context
	 * @return the rocket request builder
	 */
	public RocketRequestBuilder with(Context context) {
		 return getDefault(context).build();
	}
	
	/**
	 * Get the default Rocket instance.
	 *
	 * @param context the context
	 * @return Rocket instance
	 */
    public static Rocket getDefault(Context context) {
        if (instance == null)
            instance = new Rocket(context, "Rocket");
        return instance;
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
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

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
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        network = new BasicNetwork(stack);
        cache = new DiskBasedCache(cacheDir);

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        return queue;
    }
    
    /**
     * Cancel all pending requests.
     *
     * @param tag the tag
     */
	public void cancelAll(final Object tag) {
		if (requestQueue != null) {
			requestQueue.cancelAll(tag);
		}
	}
    
    /**
     * Builds the rocket request builder.
     *
     * @return the rocket request builder
     */
    public RocketRequestBuilder build() {
    	return new RocketRequestBuilder(this);
    }
    
	
	//*********************private apis*****************************//
	/**
	 * Instantiates a new rocket.
	 *
	 * @param context the context
	 * @param name the name
	 */
	private Rocket(Context context, String name) {
		context = context.getApplicationContext();
		this.name = name;
		
		newRequestQueue(context);
	}

}
