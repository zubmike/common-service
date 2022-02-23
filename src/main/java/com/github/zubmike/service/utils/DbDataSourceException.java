package com.github.zubmike.service.utils;

import com.github.zubmike.core.utils.DataSourceException;

import java.io.Serial;

public class DbDataSourceException extends DataSourceException {

	@Serial
	private static final long serialVersionUID = 90742151408742153L;

	public DbDataSourceException(String message) {
		super(DataSourceType.DB, message);
	}

	public DbDataSourceException(String message, Throwable cause) {
		super(DataSourceType.DB, message, cause);
	}

}
