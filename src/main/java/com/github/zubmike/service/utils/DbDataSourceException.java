package com.github.zubmike.service.utils;

import com.github.zubmike.core.utils.DataSourceException;

public class DbDataSourceException extends DataSourceException {

	private static final long serialVersionUID = 1L;

	public DbDataSourceException(String message) {
		super(DataSourceType.DB, message);
	}

	public DbDataSourceException(String message, Throwable cause) {
		super(DataSourceType.DB, message, cause);
	}

}
