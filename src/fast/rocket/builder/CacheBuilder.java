package fast.rocket.builder;

import fast.rocket.cache.CachePolicy;

public interface CacheBuilder {
	void cachePolicy(CachePolicy policy);
	
	void skipMemoryCache(boolean skipMemoryCache);
	
	void skipDiskCache(boolean skipDiskCache);
}
