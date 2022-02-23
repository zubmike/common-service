package com.github.zubmike.service.dao.http;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;
import java.nio.charset.StandardCharsets;

public class BasicHttpDao extends AbstractHttpDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicHttpDao.class);

	private static final int MAX_LOGGED_BODY_SIZE = 4096;

	public BasicHttpDao(String userAgent, int maxConnectionTimeoutMillis) {
		super(userAgent, maxConnectionTimeoutMillis);
	}

	@Override
	protected void setBody(Request request, @Null byte[] body) {
		if (body != null && body.length > 0) {
			if (body.length <= MAX_LOGGED_BODY_SIZE) {
				LOGGER.trace(new String(body, StandardCharsets.UTF_8));
			}
			request.bodyByteArray(body, ContentType.APPLICATION_JSON);
		}
	}
}
