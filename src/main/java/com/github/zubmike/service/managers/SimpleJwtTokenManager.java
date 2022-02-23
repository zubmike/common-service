package com.github.zubmike.service.managers;

import com.github.zubmike.service.conf.JwtTokenProperties;

import javax.crypto.SecretKey;
import java.io.Serializable;

public class SimpleJwtTokenManager<T extends Serializable> extends AbstractJwtTokenManager<T> {

	private static final String KEY_ALIAS = "jwt-sign-key";
	private static final String HMAC_SHA_256_ALGORITHM = "HmacSHA256";
	private static final int KEY_SIZE = 256;

	private final KeyStoreManager keyStoreManager;

	public SimpleJwtTokenManager(KeyStoreManager keyStoreManager, JwtTokenProperties jwtTokenProperties, Class<T> clazz) {
		super(jwtTokenProperties, clazz);
		this.keyStoreManager = keyStoreManager;
	}

	@Override
	protected SecretKey getSignKey() {
		return keyStoreManager.getOrCreateKey(KEY_ALIAS, HMAC_SHA_256_ALGORITHM, KEY_SIZE);
	}
}
