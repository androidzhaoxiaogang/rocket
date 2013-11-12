package fast.rocket.config;

import fast.rocket.error.RocketError;

public interface FutureCallback<T> {
	public void onCompleted(RocketError error, T result);
}
