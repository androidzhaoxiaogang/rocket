
package fast.rocket.request.filecore;

import java.util.HashMap;
import java.util.Map;

import fast.rocket.http.DefaultRetryPolicy;
import fast.rocket.request.Request;
import fast.rocket.response.NetworkResponse;
import fast.rocket.response.Response;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

/**
 * A request for making a Multi Part request
 * 
 * @param <T>
 *            Response expected
 */
public abstract class MultiPartRequest<T> extends Request<T> {

    private static final String         PROTOCOL_CHARSET = "utf-8";

    /**
     * Listener object for delivering the response
     */
    private Listener<T>                 mListener;

    /**
     * A map for Multipart parameters
     */
    private Map<String, MultiPartParam> mMultipartParams = null;

    /**
     * A map for uploading files
     */
    private Map<String, String>         mFileUploads     = null;
    
    /**
     * Default connection timeout for Multipart requests
     */
    public static final int TIMEOUT_MS = 30000;

    public MultiPartRequest(int method, String url, Listener<T> listener, ErrorListener errorlistener) {
        super(method, url, errorlistener, new DefaultRetryPolicy(TIMEOUT_MS, 
        		DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mListener = listener;
        mMultipartParams = new HashMap<String, MultiPartRequest.MultiPartParam>();
        mFileUploads = new HashMap<String, String>();
        
    }

    /**
     * Add a parameter to be sent in the multipart request
     * 
     * @param name
     *            The name of the paramter
     * @param contentType
     *            The content type of the paramter
     * @param value
     *            the value of the paramter
     * @return The Multipart request for chaining calls
     */
    public MultiPartRequest<T> addMultipartParam(String name, String contentType, String value) {

        mMultipartParams.put(name, new MultiPartParam(contentType, value));
        return this;
    }

    /**
     * Add a file to be uploaded in the multipart request
     * 
     * @param name
     *            The name of the file key
     * @param filePath
     *            The path to the file. This file MUST exist.
     * @return The Multipart request for chaining method calls
     */
    public MultiPartRequest<T> addFile(String name, String filePath) {
        mFileUploads.put(name, filePath);
        return this;
    }

    @Override
	public
    abstract Response<T> parseNetworkResponse(NetworkResponse response);

    @Override
	public void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    /**
     * A representation of a MultiPart parameter
     */
    public static final class MultiPartParam {
        public String contentType;
        public String value;

        /**
         * Initialize a multipart request param with the value and content type
         * 
         * @param contentType
         *            The content type of the param
         * @param value
         *            The value of the param
         */
        public MultiPartParam(String contentType, String value) {

            this.contentType = contentType;
            this.value = value;
        }
    }

    /**
     * Get all the multipart params for this request
     * 
     * @return A map of all the multipart params NOT including the file uploads
     */
    public Map<String, MultiPartParam> getMultipartParams() {

        return mMultipartParams;
    }

    /**
     * Get all the files to be uploaded for this request
     * 
     * @return A map of all the files to be uploaded for this request
     */
    public Map<String, String> getFilesToUpload() {

        return mFileUploads;
    }
    
    /**
     * Get the protocol charset
     */
    public String getProtocolCharset() {
        
        return PROTOCOL_CHARSET;
    }
    
}
