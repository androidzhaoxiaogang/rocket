
package fast.rocket.error;

import fast.rocket.NetworkResponse;

/**
 * Indicates that the server's response could not be parsed.
 */
@SuppressWarnings("serial")
public class ParseError extends RocketError {
    public ParseError() { }

    public ParseError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ParseError(Throwable cause) {
        super(cause);
    }
}
