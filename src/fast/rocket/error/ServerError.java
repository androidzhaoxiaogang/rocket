
package fast.rocket.error;

import fast.rocket.response.NetworkResponse;

/**
 * Indicates that the error responded with an error response.
 */
@SuppressWarnings("serial")
public class ServerError extends RocketError {
    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ServerError() {
        super();
    }
}
