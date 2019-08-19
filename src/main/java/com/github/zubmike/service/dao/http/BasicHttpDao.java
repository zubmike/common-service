package com.github.zubmike.service.dao.http;

import com.github.zubmike.service.utils.JsonUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;

public class BasicHttpDao extends AbstractHttpDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicHttpDao.class);

	public BasicHttpDao(String userAgent, int maxConnectionTimeoutMillis) {
		super(userAgent, maxConnectionTimeoutMillis);
	}

	@Override
	protected void setBody(Request request, @Null byte[] body) {
		if (body != null && body.length > 0) {
			LOGGER.trace(" -> {}", request.toString());
			LOGGER.trace(JsonUtils.writeObjectAsString(body));
			request.bodyByteArray(body, ContentType.APPLICATION_JSON);
		}
	}
}
