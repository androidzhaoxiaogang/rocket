
package fast.rocket.response;

import fast.rocket.error.RocketError;
import fast.rocket.request.Request;

public interface ResponseDelivery {
    /**
     * Parses a response from the network or cache and delivers it.
     */
    public void postResponse(Request<?> request, Response<?> response);

    /**
     * Parses a response from the network or cache and delivers it. The provided
     * Runnable will be executed after delivery.
     */
    public void postResponse(Request<?> request, Response<?> response, Runnable runnable);

    /**
     * Posts an error for the given request.
     */
    public void postError(Request<?> request, RocketError error);
}
