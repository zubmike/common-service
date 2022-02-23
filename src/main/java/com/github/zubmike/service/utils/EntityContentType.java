package com.github.zubmike.service.utils;

import java.util.Optional;
import java.util.stream.Stream;

public enum EntityContentType {

	CVS("cvs", "text/csv"),
	DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	JPG("jpg", "image/jpeg"),
	JPEG("jpeg", "image/jpeg"),
	JSON("json", "application/json"),
	OCTET_STREAM("", "application/octet-stream"), // for unknown types
	PDF("pdf", "application/pdf"),
	PNG("png", "image/png"),
	XLS("xls", "application/vnd.ms-excel"),
	XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	XML("xml", "application/xml"),
	JAR("jar", "application/java-archive");

	private final String type;
	private final String mediaType;

	EntityContentType(String type, String mediaType) {
		this.type = type;
		this.mediaType = mediaType;
	}

	public String getType() {
		return type;
	}

	public String getMediaType() {
		return mediaType;
	}

	public static Optional<EntityContentType> fromType(String type) {
		return Stream.of(values())
				.filter(item -> item.getType().equals(type))
				.findFirst();
	}

	public static Optional<EntityContentType> fromMediaType(String mediaType) {
		return Stream.of(values())
				.filter(item -> item.getMediaType().equals(mediaType))
				.findFirst();
	}

}
