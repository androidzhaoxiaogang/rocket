
package fast.rocket.http;

import fast.rocket.error.RocketError;

/**
 * Retry policy for a request.
 */
public interface RetryPolicy {

    /**
     * Returns the current timeout (used for logging).
     */
    public int getCurrentTimeout();

    /**
     * Returns the current retry count (used for logging).
     */
    public int getCurrentRetryCount();

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     * @param error The error code of the last attempt.
     * @throws RocketError In the event that the retry could not be performed (for example if we
     * ran out of attempts), the passed in error is thrown.
     */
    public void retry(RocketError error) throws RocketError;
}
