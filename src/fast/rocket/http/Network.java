
package fast.rocket.http;

import fast.rocket.NetworkResponse;
import fast.rocket.Request;
import fast.rocket.error.RocketError;

/**
 * An interface for performing requests.
 */
public interface Network {
    /**
     * Performs the specified request.
     * @param request Request to process
     * @return A {@link NetworkResponse} with data and caching metadata; will never be null
     * @throws RocketError on errors
     */
    public NetworkResponse performRequest(Request<?> request) throws RocketError;
}
