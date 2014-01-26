
package fast.rocket.http;

import fast.rocket.error.RocketError;
import fast.rocket.request.Request;
import fast.rocket.response.NetworkResponse;

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
