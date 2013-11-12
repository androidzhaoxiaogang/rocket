package fast.rocket.config;

import fast.rocket.error.RocketError;

public interface FutureCallback {
	public <T> void onCompleted(RocketError error, T result);
}
