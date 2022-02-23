package com.github.zubmike.service.conf;

import java.io.Serial;
import java.io.Serializable;

public class ServerProperties implements Serializable {

	@Serial
	private static final long serialVersionUID = 4186811394943426851L;

	private String contextUrl = "";
	private int port = 8080;

	public String getContextUrl() {
		return contextUrl;
	}

	public void setContextUrl(String contextUrl) {
		this.contextUrl = contextUrl;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
