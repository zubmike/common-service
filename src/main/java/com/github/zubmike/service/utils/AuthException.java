package com.github.zubmike.service.utils;

import java.io.Serial;

public class AuthException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -5221693224474173945L;

	public AuthException() {
	}

	public AuthException(String message) {
		super(message);
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
	}
}
