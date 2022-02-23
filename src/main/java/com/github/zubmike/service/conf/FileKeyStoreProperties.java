package com.github.zubmike.service.conf;

import java.io.Serial;
import java.io.Serializable;

public class FileKeyStoreProperties implements Serializable {

	@Serial
	private static final long serialVersionUID = 528462703494393136L;

	private String path;
	private String type;
	private String password;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
