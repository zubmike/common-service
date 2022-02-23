package com.github.zubmike.service.conf;

import java.io.Serial;
import java.io.Serializable;
import java.time.temporal.ChronoUnit;

public class JwtTokenProperties implements Serializable {

	@Serial
	private static final long serialVersionUID = 2960557730113409223L;

	private long accessTokenLiveTime = 1;
	private long refreshTokenLiveTime = 4;
	private ChronoUnit tokenLiveTimeUnit = ChronoUnit.HOURS;

	public long getAccessTokenLiveTime() {
		return accessTokenLiveTime;
	}

	public void setAccessTokenLiveTime(long accessTokenLiveTime) {
		this.accessTokenLiveTime = accessTokenLiveTime;
	}

	public long getRefreshTokenLiveTime() {
		return refreshTokenLiveTime;
	}

	public void setRefreshTokenLiveTime(long refreshTokenLiveTime) {
		this.refreshTokenLiveTime = refreshTokenLiveTime;
	}

	public ChronoUnit getTokenLiveTimeUnit() {
		return tokenLiveTimeUnit;
	}

	public void setTokenLiveTimeUnit(ChronoUnit tokenLiveTimeUnit) {
		this.tokenLiveTimeUnit = tokenLiveTimeUnit;
	}
}
