package ru.zubmike.service.managers;

import java.io.Serializable;

public interface TokenManager<T extends Serializable> {

	String createAccessToken(T userAccess);

	String createRefreshToken(T userAccess);

	T getAccess(String token);

}
