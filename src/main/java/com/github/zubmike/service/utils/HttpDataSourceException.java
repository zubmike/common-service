package com.github.zubmike.service.utils;

import com.github.zubmike.core.utils.DataSourceException;

public class HttpDataSourceException extends DataSourceException {

	private static final long serialVersionUID = 1L;

	private final int code;

	public HttpDataSourceException(int code, String message) {
		super(DataSourceType.HTTP, message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
