package ru.zubmike.service.conf;

import java.io.Serializable;

public class FileKeyStoreProperties implements Serializable {

	private static final long serialVersionUID = 1L;

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
