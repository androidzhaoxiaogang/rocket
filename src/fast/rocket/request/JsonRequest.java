package fast.rocket.request;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import fast.rocket.error.AuthFailureError;
import fast.rocket.error.ParseError;
import fast.rocket.http.HttpHeaderParser;
import fast.rocket.response.NetworkResponse;
import fast.rocket.response.Response;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

public class JsonRequest<T> extends Request<T> {
	private final Gson gson = new Gson();
	private Class<T> clazz;
	private Map<String, String> headers;
	private Map<String, String> params;
	private final Listener<T> listener;

	/**
	 * Make a GET request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 * @param clazz
	 *            Relevant class object, for Gson's reflection
	 * @param headers
	 *            Map of request headers
	 */
	public JsonRequest(int method, String url, Class<T> clazz,
			Map<String, String> headers, Map<String, String> params,
			Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.clazz = clazz;
		this.headers = headers;
		this.params = params;
		this.listener = listener;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params != null ? params : super.getParams();
	}

	@Override
	public void deliverResponse(T response) {
		listener.onResponse(response);
	}

	@Override
	public Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(gson.fromJson(json, clazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}