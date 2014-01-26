package fast.rocket.response;

import fast.rocket.error.RocketError;

/**
 * The Interface FutureCallback.
 *
 * @param <T> the generic type
 */
public interface JsonCallback<T> {
	
	/**
	 * On completed.
	 *
	 * @param error the error
	 * @param result the result
	 */
	public void onCompleted(RocketError error, T result);
}
