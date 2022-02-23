package com.github.zubmike.service.utils;

import java.io.Serial;

public class DuplicateException extends IllegalArgumentException {

	@Serial
	private static final long serialVersionUID = 6376599594638209502L;

	public DuplicateException() {
	}

	public DuplicateException(String message) {
		super(message);
	}

	public DuplicateException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateException(Throwable cause) {
		super(cause);
	}
}
