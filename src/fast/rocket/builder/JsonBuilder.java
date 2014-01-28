package fast.rocket.builder;

import java.util.Map;

import fast.rocket.response.JsonCallback;

public interface JsonBuilder<J extends JsonBuilder<?>>{
	/**
	 * @param callback
	 * @return
	 */
	public J invoke(JsonCallback<?> callback);
	
	/**
	 * Sets the request tag. Request can be removed by the tag.
	 *
	 * @param tag the tag
	 * @return the rocket request builder
	 */
	public J requestTag(Object tag);

	/**
	 * Sets the request params for the http post.
	 *
	 * @param params the params
	 * @return the rocket request builder
	 */
	public J requestParams(Map<String, String> params);
	
	/**
	 * Sets the json request http headers.
	 *
	 * @param headers the headers
	 * @return the rocket request builder
	 */
	public J requestHeaders(Map<String, String> headers);
	
	/**
     * Sets the request cookie tag. Request can be removed by the tag.
     *
     * @param enableCookie the tag
     * @return the rocket request builder
     */
    public J enableCookie(boolean enableCookie);
}
