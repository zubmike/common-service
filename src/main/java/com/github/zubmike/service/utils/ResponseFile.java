package com.github.zubmike.service.utils;

import java.io.InputStream;

public class ResponseFile {

	private final String name;
	private final EntityContentType type;
	private final long size;
	private final InputStream inputStream;

	public ResponseFile(String name, EntityContentType type, long size, InputStream inputStream) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.inputStream = inputStream;
	}

	public String getName() {
		return name;
	}

	public EntityContentType getType() {
		return type;
	}

	public long getSize() {
		return size;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}
