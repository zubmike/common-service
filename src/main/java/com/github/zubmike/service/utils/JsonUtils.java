package com.github.zubmike.service.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.zubmike.core.utils.InternalException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

	private static final ObjectMapper MAPPER = createObjectMapper();

	public static <T> T parse(String value, Class<T> clazz) {
		try {
			return MAPPER.readerFor(clazz).readValue(value);
		} catch (IOException e) {
			throw new InternalException(e);
		}
	}


	public static <T> T parse(InputStream inputStream, Class<T> clazz) {
		try {
			return MAPPER.readerFor(clazz).readValue(inputStream);
		} catch (IOException e) {
			throw new InternalException(e);
		}
	}

	public static <T> List<T> parseList(InputStream inputStream, Class<T[]> clazz) {
		try {
			return Arrays.asList(MAPPER.readValue(inputStream, clazz));
		} catch (IOException e) {
			throw new InternalException(e);
		}
	}

	public static byte[] writeObjectAsBytes(Serializable value) {
		try {
			return MAPPER.writeValueAsBytes(value);
		} catch (JsonProcessingException e) {
			throw new InternalException(e);
		}
	}

	public static String writeObjectAsString(Serializable value) {
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new InternalException(e);
		}
	}

	public static void writeObjectAsBytes(OutputStream outputStream, Serializable value) throws IOException {
		MAPPER.writeValue(outputStream, value);
	}

	public static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper;
	}
}
