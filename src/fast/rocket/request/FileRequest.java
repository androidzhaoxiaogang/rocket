package fast.rocket.request;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import fast.rocket.error.ParseError;
import fast.rocket.http.HttpHeaderParser;
import fast.rocket.request.filecore.MultiPartRequest;
import fast.rocket.response.NetworkResponse;
import fast.rocket.response.Response;
import fast.rocket.response.Response.ErrorListener;
import fast.rocket.response.Response.Listener;

public class FileRequest<T> extends MultiPartRequest<T> {
	private final Gson gson = new Gson();
	private Class<T> clazz;
	
	public FileRequest(int method, String url, Listener<T> listener,
			ErrorListener errorlistener, Class<T> clazz) {
		super(method, url, listener, errorlistener);
		
		this.clazz = clazz;
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
