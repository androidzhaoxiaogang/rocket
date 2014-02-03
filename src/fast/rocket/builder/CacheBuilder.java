package fast.rocket.builder;

import fast.rocket.cache.CachePolicy;

public interface CacheBuilder<T> {
	T cachePolicy(CachePolicy policy);
	
	T skipMemoryCache(boolean skipMemoryCache);
}
