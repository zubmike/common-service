package com.github.zubmike.service.managers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.zubmike.core.utils.IOUtils;
import com.github.zubmike.core.utils.InternalException;
import com.github.zubmike.service.conf.JwtTokenProperties;
import com.github.zubmike.service.utils.AuthException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJwtTokenManager<T extends Serializable> implements TokenManager<T> {

	private static final String CLAIM_SUB_KEY = "sub";
	private static final String CLAIM_EXP_KEY = "exp";

	private final JwtTokenProperties jwtTokenProperties;

	private final ObjectReader jsonReader;
	private final ObjectWriter jsonWriter;

	public AbstractJwtTokenManager(JwtTokenProperties jwtTokenProperties, Class<T> clazz) {
		this.jwtTokenProperties = jwtTokenProperties;
		this.jsonReader = new ObjectMapper().readerFor(clazz);
		this.jsonWriter = new ObjectMapper().writerFor(clazz);
	}
	@Override
	public String createAccessToken(T userAccess) {
		long exp = LocalDateTime.now().atZone(ZoneId.systemDefault())
				.plus(jwtTokenProperties.getAccessTokenLiveTime(), jwtTokenProperties.getTokenLiveTimeUnit())
				.toInstant().toEpochMilli();
		return createToken(userAccess, exp);
	}

	@Override
	public String createRefreshToken(T userAccess) {
		long exp = LocalDateTime.now().atZone(ZoneId.systemDefault())
				.plus(jwtTokenProperties.getRefreshTokenLiveTime(), jwtTokenProperties.getTokenLiveTimeUnit())
				.toInstant().toEpochMilli();
		return createToken(userAccess, exp);
	}

	protected String createToken(T userAccess, Long exp) {
		try {
			JWSObject jwsObject = new JWSObject(
					new JWSHeader(JWSAlgorithm.HS256),
					new Payload(createClaims(userAccess, exp)));
			signToken(jwsObject);
			return jwsObject.serialize();
		} catch (JOSEException | JsonProcessingException e) {
			throw new InternalException(e);
		}
	}

	protected void signToken(JWSObject jwsObject) throws JOSEException {
		jwsObject.sign(new MACSigner(getSignKey()));
	}

	protected abstract SecretKey getSignKey();

	protected Map<String, Object> createClaims(T userAccess, Long exp) throws JsonProcessingException {
		var claimsMap = new HashMap<String, Object>();
		if (exp != null) {
			claimsMap.put(CLAIM_EXP_KEY, exp);
		}
		byte[] bytes = jsonWriter.writeValueAsBytes(userAccess);
		String sub = IOUtils.encodeBase64(bytes);
		claimsMap.put(CLAIM_SUB_KEY, sub);
		return claimsMap;
	}

	@Override
	public T getAccess(String token) {
		try {
			var claimsMap = getClaimsAndVerifySign(JWSObject.parse(token));
			Long exp = getExp(claimsMap);
			if (exp != null && exp <= System.currentTimeMillis()) {
				throw new AuthException("Invalid JWT token");
			}
			String sub = getSub(claimsMap);
			if (sub == null) {
				throw new AuthException("Invalid JWT token");
			}
			return parseUserAccess(sub);
		} catch (ParseException | JsonParseException | JsonMappingException e) {
			throw new AuthException("Invalid JWT token", e);
		} catch (JOSEException | IOException e) {
			throw new InternalException(e);
		}
	}

	protected Map<String, Object> getClaimsAndVerifySign(JWSObject jwsObject) throws JOSEException {
		boolean verify = verifyToken(jwsObject);
		if (verify && jwsObject.getPayload() != null) {
			var claimsMap = jwsObject.getPayload().toJSONObject();
			if (claimsMap != null) {
				return claimsMap;
			}
		}
		throw new AuthException("Invalid JWT token");
	}

	protected boolean verifyToken(JWSObject jwsObject) throws JOSEException {
		return jwsObject.verify(new MACVerifier(getSignKey()));
	}

	protected static String getSub(Map<String, Object> claimsMap) {
		return (String) claimsMap.get(CLAIM_SUB_KEY);
	}

	protected static Long getExp(Map<String, Object> claimsMap) {
		return (Long) claimsMap.get(CLAIM_EXP_KEY);
	}

	private T parseUserAccess(String sub) throws IOException {
		byte[] subBytes = IOUtils.decodeBase64(sub);
		return jsonReader.readValue(subBytes);
	}

}
