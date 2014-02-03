package fast.rocket.builder;

import fast.rocket.cache.CachePolicy;

/**
 * The Interface CacheBuilder.
 *
 * @param <T> the generic type
 */
public interface CacheBuilder<T> {
	
	/**
	 * Cache policy.
	 *
	 * @param policy the policy
	 * @return the t
	 */
	T cachePolicy(CachePolicy policy);
	
	/**
	 * Skip memory cache.
	 *
	 * @param skipMemoryCache the skip memory cache
	 * @return the t
	 */
	T skipMemoryCache(boolean skipMemoryCache);
}
