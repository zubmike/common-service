package com.github.zubmike.service.managers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.zubmike.core.utils.IOUtils;
import com.github.zubmike.core.utils.InternalException;
import com.github.zubmike.service.utils.AuthException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import net.minidev.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;

public abstract class AbstractJwtTokenManager<T extends Serializable> implements TokenManager<T> {

	private final long accessTokenLiveTime;
	private final long refreshTokenLiveTime;
	private final TemporalUnit tokenLiveUnit;

	private final ObjectReader jsonReader;
	private final ObjectWriter jsonWriter;

	public AbstractJwtTokenManager(long accessTokenLiveTime, long refreshTokenLiveTime, TemporalUnit tokenLiveUnit,
	                                  Class<T> clazz) {
		this.accessTokenLiveTime = accessTokenLiveTime;
		this.refreshTokenLiveTime = refreshTokenLiveTime;
		this.tokenLiveUnit = tokenLiveUnit;
		this.jsonReader = new ObjectMapper().readerFor(clazz);
		this.jsonWriter = new ObjectMapper().writerFor(clazz);
	}
	@Override
	public String createAccessToken(T userAccess) {
		long exp = LocalDateTime.now().atZone(ZoneId.systemDefault())
				.plus(accessTokenLiveTime, tokenLiveUnit)
				.toInstant().toEpochMilli();
		return createToken(userAccess, exp);
	}

	@Override
	public String createRefreshToken(T userAccess) {
		long exp = LocalDateTime.now().atZone(ZoneId.systemDefault())
				.plus(refreshTokenLiveTime, tokenLiveUnit)
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

	protected JSONObject createClaims(T userAccess, Long exp) throws JsonProcessingException {
		JSONObject claims = new JSONObject();
		if (exp != null) {
			claims.put("exp", exp);
		}
		byte[] bytes = jsonWriter.writeValueAsBytes(userAccess);
		String sub = IOUtils.encodeBase64(bytes);
		claims.put("sub", sub);
		return claims;
	}

	@Override
	public T getAccess(String token) {
		try {
			JSONObject jsonObject = getClaimsAndVerifySign(JWSObject.parse(token));
			Long exp = getExp(jsonObject);
			if (exp != null && exp <= System.currentTimeMillis()) {
				throw new AuthException("Invalid token");
			}
			String sub = getSub(jsonObject);
			if (sub == null) {
				throw new AuthException("Invalid token");
			}
			return parseUserAccess(sub);
		} catch (ParseException | JsonParseException | JsonMappingException e) {
			throw new AuthException("Invalid token", e);
		} catch (JOSEException | IOException e) {
			throw new InternalException(e);
		}
	}

	protected JSONObject getClaimsAndVerifySign(JWSObject jwsObject) throws JOSEException {
		boolean verify = verifyToken(jwsObject);
		if (verify && jwsObject.getPayload() != null) {
			JSONObject jsonObject = jwsObject.getPayload().toJSONObject();
			if (jsonObject != null) {
				return jsonObject;
			}
		}
		throw new AuthException("Invalid token");
	}

	protected boolean verifyToken(JWSObject jwsObject) throws JOSEException {
		return jwsObject.verify(new MACVerifier(getSignKey()));
	}

	protected static String getSub(JSONObject jsonObject) {
		return (String) jsonObject.get("sub");
	}

	protected static Long getExp(JSONObject jsonObject) {
		return (Long) jsonObject.get("exp");
	}

	private T parseUserAccess(String sub) throws IOException {
		byte[] subBytes = IOUtils.decodeBase64(sub);
		return jsonReader.readValue(subBytes);
	}
}
