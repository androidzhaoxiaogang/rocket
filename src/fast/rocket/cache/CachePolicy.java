package fast.rocket.cache;

public enum CachePolicy {
	NOCACHE,

	CACHEONLY,

	CACHEFIRST,

	CACHEFIRST_NOREFRESH,

	NETWORKFIRST,

	BOTH;
}
