package fast.rocket.config;

import fast.rocket.error.RocketError;

// TODO: Auto-generated Javadoc
/**
 * The Interface FutureCallback.
 *
 * @param <T> the generic type
 */
public interface FutureCallback<T> {
	
	/**
	 * On completed.
	 *
	 * @param error the error
	 * @param result the result
	 */
	public void onCompleted(RocketError error, T result);
}
