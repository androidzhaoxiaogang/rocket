package fast.rocket.cache;

/**
 * CachePolicy.NOCACHE This policy will not use any caching,and will execute
 * every request online.
 * 
 * CachePolicy.CACHEFIRST This policy will first retrieve an element from the
 * cache, if the cached data does not exist or is expired and then it will
 * attempt to execute the request on line. Use this policy it does the volley
 * same way.The cache time is determined by the server "Cache-Control".
 * 
 * CachePolicy.NETWORKFIRST This policy will execute the request on the network,
 * and will store the result in the cache. If the online execution fails, the
 * error results will be post to the UI thread. Use this policy if you
 * application wants the latest data but you still want responsiveness if a
 * connection is lost.
 * 
 * CachePolicy.BOTH This policy will first attempt to retrieve data from the
 * cache. If the data has been cached, it will be returned. If the data does not
 * exist in the cache or not, the data will be retrieved from server every
 * time.Use this policy your application can display data for an instant that
 * you expect whether it was expired or not every time.(e.g. The first or home
 * page data in your APPs.) Then if the new data from remote server is returned,
 * the old data should be updated both in memory and disk cache. Otherwise, the
 * error results will be post to the UI thread.
 * 
 * */

/**
 * CachePolicy.NOCACHE - This policy will not use any caching, and will execute
 * every request online. Use this policy if your application is dependant on
 * data that is shared between multiple users and always needs to be up to date.
 * 
 * CachePolicy.CACHEONLY - This policy will only retrieve data from the cache,
 * and will not use any network connection. Use this policy in combination with
 * another policy, to allow for quick response times without requiring a network
 * connection for specific operations.
 * 
 * CachePolicy.CACHEFIRST - This policy will first attempt to retrieve data from
 * the cache. If the data has been cached, it will be returned. If the data does
 * not exist in the cache, the data will be retrieved from server Backend and
 * the cache will be updated. Use this policy if your application can display
 * data that doesn't change very often but you still want local updates.
 * 
 * CachePolicy.CACHEFIRST_NOREFRESH - This policy will first attempt to retrieve
 * data from the cache. If the data has been cached, it will be returned. If the
 * data does not exist in the cache, the data will be retrieved from server
 * Backend but the cache will not be updated with the new results. Use this
 * policy if you want to set default results, however if a request is made that
 * cannot return these defaults a live request will be made (without modifying
 * those default values) 
 * 
 * CachePolicy.NETWORKFIRST - This policy will execute the request on the
 * network, and will store the result in the cache. If the online execution
 * fails, the results will be pulled from the cache. Use this policy if you
 * application wants the latest data but you still want responsiveness if a
 * connection is lost
 * 
 * CachePolicy.BOTH - This policy will first retrieve an element from the cache,
 * and then it will attempt to execute the request on line.Use this policy if
 * you want more responsiveness without sacrificing the consistency of data with
 * your backend.
 * 
 * 
 * */

public enum CachePolicy {
	NOCACHE,

	CACHEONLY,

	CACHEFIRST,

	CACHEFIRST_NOREFRESH,

	NETWORKFIRST,

	BOTH;
}
