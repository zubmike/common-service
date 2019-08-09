package ru.zubmike.service.managers;

import javax.crypto.SecretKey;
import java.util.Optional;

public interface KeyStoreManager {

	void setKey(String alias, SecretKey key);

	Optional<SecretKey> getKey(String alias);

	SecretKey getOrCreateKey(String alias, String algorithm, int size);

}
