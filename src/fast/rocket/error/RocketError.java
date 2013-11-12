
package fast.rocket.error;

import fast.rocket.NetworkResponse;

/**
 * Exception style class encapsulating Rocket errors
 */
@SuppressWarnings("serial")
public class RocketError extends Exception {
    public final NetworkResponse networkResponse;

    public RocketError() {
        networkResponse = null;
    }

    public RocketError(NetworkResponse response) {
        networkResponse = response;
    }

    public RocketError(String exceptionMessage) {
       super(exceptionMessage);
       networkResponse = null;
    }

    public RocketError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public RocketError(Throwable cause) {
        super(cause);
        networkResponse = null;
    }
}
