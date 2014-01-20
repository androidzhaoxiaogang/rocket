package fast.rocket.cache;

/**
 * CachePolicy.NOCACHE This policy will not use any caching,and will execute
 * every request online.
 * 
 * CachePolicy.CACHEONLY This policy will only retrieve data from the cache, and
 * will not use any network connection.Use this policy in combination with
 * another policy, to allow for quick response times without requiring a network
 * connection for specific operations.
 * 
 * CachePolicy.CACHEFIRST This policy will first attempt to retrieve data from
 * the cache. If the data has been cached, it will be returned. If the data does
 * not exist in the cache, the data will be retrieved from server and the cache
 * will be updated. Use this policy if your application can display data that
 * doesn't change very often but you still want local updates.
 * 
 * CachePolicy.NETWORKFIRST This policy will execute the request on the network,
 * and will store the result in the cache. If the online execution fails, the
 * results will be pulled from the cache. Use this policy if you application
 * wants the latest data but you still want responsiveness if a connection is
 * lost
 * 
 * CachePolicy.BOTH This policy will first retrieve an element from the cache,
 * and then it will attempt to execute the request on line. Use this policy if
 * you want more responsiveness without sacrificing the consistency of data with
 * your backend.
 * 
 * */
public enum CachePolicy {
	NOCACHE,

	CACHEONLY,

	CACHEFIRST,

	NETWORKFIRST,

	BOTH;
}
