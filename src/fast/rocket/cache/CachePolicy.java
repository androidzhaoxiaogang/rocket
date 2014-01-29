package fast.rocket.cache;

/**
 * CachePolicy.NOCACHE This policy will not use any caching,and will execute
 * every request online.
 * 
 * CachePolicy.CACHEFIRST This policy will first attempt to retrieve data from
 * the cache. If the data has been cached, it will be returned. If the data does
 * not exist in the cache, the data will be retrieved from server and the cache
 * will be updated automatically. Use this policy your application can display 
 * data for an instant that you expect whether it was expired or not every time.
 * Then if the new data from remote server is available, the old data should be 
 * updated both in memory and disk cache.
 * 
 * CachePolicy.NETWORKFIRST This policy will execute the request on the network,
 * and will store the result in the cache. If the online execution fails, the
 * error results will be post to the UI thread. Use this policy if you application
 * wants the latest data but you still want responsiveness if a connection is
 * lost.
 * 
 * CachePolicy.BOTH This policy will first retrieve an element from the cache, if the
 * cached data does not exist or is expired and then it will attempt to execute the 
 * request on line. Use this policy if you want more responsiveness without sacrificing 
 * the consistency of data with your backend. Also it does the volley same way. 
 * 
 * */
public enum CachePolicy {
	NOCACHE,

	CACHEFIRST,

	NETWORKFIRST,

	BOTH;
}
