package fast.rocket.builder;

import java.util.Map;

import fast.rocket.response.JsonCallback;

public interface FileMultipartBuilder {
	/**
	 * Sets the request tag. Request can be removed by the tag.
	 * 
	 * @param tag
	 *            the tag
	 * @return the rocket request builder
	 */
	public FileMultipartBuilder requestTag(Object tag);

	/**
	 * @param callback
	 * @return
	 */
	public FileMultipartBuilder invoke(JsonCallback<?> callback);

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public FileMultipartBuilder addMultipartParam(Map<String, String> params);

	/**
	 * @param name
	 * @param filePath
	 * @return
	 */
	public FileMultipartBuilder addFile(String name, String filePath);
}
