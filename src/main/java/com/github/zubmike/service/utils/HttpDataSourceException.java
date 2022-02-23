package com.github.zubmike.service.utils;

import com.github.zubmike.core.utils.DataSourceException;

import java.io.Serial;

public class HttpDataSourceException extends DataSourceException {

	@Serial
	private static final long serialVersionUID = 4372013846884934455L;

	private final int code;

	public HttpDataSourceException(int code, String message) {
		super(DataSourceType.HTTP, message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
