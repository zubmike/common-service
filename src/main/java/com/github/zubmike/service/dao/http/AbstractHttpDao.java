package com.github.zubmike.service.dao.http;

import com.github.zubmike.core.utils.IOUtils;
import com.github.zubmike.core.utils.InternalException;
import com.github.zubmike.core.utils.InvalidParameterException;
import com.github.zubmike.core.utils.NotFoundException;
import com.github.zubmike.service.utils.HttpDataSourceException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractHttpDao {

	private final String userAgent;
	private final int maxConnectionTimeoutMillis;

	public AbstractHttpDao(String userAgent, int maxConnectionTimeoutMillis) {
		this.userAgent = userAgent;
		this.maxConnectionTimeoutMillis = maxConnectionTimeoutMillis;
	}

	protected <R> R doGetRequest(URI uri, Function<InputStream, R> prepareResult) {
		return doRequest(Request.Get(uri), null, prepareResult);
	}

	protected <R> R doGetRequest(String url, Function<InputStream, R> prepareResult) {
		return doRequest(Request.Get(url), null, prepareResult);
	}

	protected <R> Optional<R> doGetRequestOptional(String url, Function<InputStream, R> prepareResult) {
		try {
			return Optional.ofNullable(doGetRequest(url, prepareResult));
		} catch (NotFoundException e) {
			return Optional.empty();
		}
	}

	protected <R> R doPostRequest(String url, @Null byte[] body, Function<InputStream, R> prepareResult) {
		return doRequest(Request.Post(url), body, prepareResult);
	}

	protected <R> R doPutRequest(String url, @Null byte[] body, Function<InputStream, R> prepareResult) {
		return doRequest(Request.Put(url), body, prepareResult);
	}

	protected <R> R doDeleteRequest(String url, Function<InputStream, R> prepareResult) {
		return doRequest(Request.Delete(url), null, prepareResult);
	}

	protected <R> R doRequest(Request request, @Null byte[] body, Function<InputStream, R> prepareResult) {
		initRequest(request);
		try {
			setBody(request, body);
			Response response = request.execute();
			return response.handleResponse(httpResponse ->
					processResponse(httpResponse, prepareResult));
		} catch (IOException e) {
			throw new InternalException("", e);
		}
	}

	protected abstract void setBody(Request request, @Null byte[] body);

	protected <R> R processResponse(HttpResponse response, Function<InputStream, R> prepareResult) throws IOException {
		int responseCode = response.getStatusLine().getStatusCode();
		switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
			case HttpURLConnection.HTTP_CREATED:
			case HttpURLConnection.HTTP_ACCEPTED:
				return prepareResult.apply(response.getEntity().getContent());
			case HttpURLConnection.HTTP_NO_CONTENT:
				throw new NotFoundException();
			case HttpURLConnection.HTTP_NOT_FOUND:
				throw new NotFoundException(getErrorMessage(response, responseCode));
			case HttpURLConnection.HTTP_BAD_REQUEST:
				throw new InvalidParameterException(getErrorMessage(response, responseCode));
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				throw new HttpDataSourceException(responseCode, "Unauthorized");
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
			case HttpURLConnection.HTTP_UNAVAILABLE:
			case HttpURLConnection.HTTP_BAD_GATEWAY:
			case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
				throw new HttpDataSourceException(responseCode, "Source unavailable");
			default:
				throw new HttpDataSourceException(responseCode, "Unknown error");
		}
	}

	protected String getErrorMessage(HttpResponse response, int responseCode) throws IOException {
		HttpEntity entity = response.getEntity();
		return entity != null && entity.getContent() != null
				? IOUtils.toString(entity.getContent())
				: ("Unknown " + responseCode);
	}

	protected void initRequest(Request request) {
		request.setHeader(HttpHeaders.USER_AGENT, userAgent);
		request.connectTimeout(maxConnectionTimeoutMillis);
		request.socketTimeout(maxConnectionTimeoutMillis);
	}

}
