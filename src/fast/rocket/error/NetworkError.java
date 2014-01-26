
package fast.rocket.error;

import fast.rocket.response.NetworkResponse;


/**
 * Indicates that there was a network error when performing a Rocket request.
 */
@SuppressWarnings("serial")
public class NetworkError extends RocketError {
    public NetworkError() {
        super();
    }

    public NetworkError(Throwable cause) {
        super(cause);
    }

    public NetworkError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
