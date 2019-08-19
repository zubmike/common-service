package com.github.zubmike.service.conf;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataBaseProperties implements Serializable {

	private static final long serialVersionUID = 2L;

	private String driverClass;
	private String dialect;
	private String url;
	private String user;
	private String password;
	private Map<String, String> properties = new LinkedHashMap<>();

	public DataBaseProperties() {
	}

	public DataBaseProperties(String driverClass, String dialect, String url, String user, String password,
	                          Map<String, String> properties) {
		this.driverClass = driverClass;
		this.dialect = dialect;
		this.url = url;
		this.user = user;
		this.password = password;
		this.properties = properties;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}
