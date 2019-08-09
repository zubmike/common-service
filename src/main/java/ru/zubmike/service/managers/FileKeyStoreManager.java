package ru.zubmike.service.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zubmike.core.utils.InternalException;
import ru.zubmike.service.conf.FileKeyStoreProperties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileKeyStoreManager implements KeyStoreManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileKeyStoreManager.class);

	private final Map<String, SecretKey> cachedKeyMap = new HashMap<>();

	private File file;
	private char[] password;
	private KeyStore keyStore;

	public FileKeyStoreManager(FileKeyStoreProperties fileKeyStoreProperties) {
		init(fileKeyStoreProperties);
	}

	private void init(FileKeyStoreProperties fileKeyStoreProperties) {
		this.file = new File(fileKeyStoreProperties.getPath());
		this.password = fileKeyStoreProperties.getPassword().toCharArray();
		LOGGER.info("key store file: {}", file.getAbsoluteFile());
		try {
			keyStore = KeyStore.getInstance(fileKeyStoreProperties.getType());
			keyStore.load(null, null);
			if (!file.exists()) {
				save();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new InternalException(e);
		}
	}

	@Override
	public void setKey(String alias, SecretKey key) {
		try {
			keyStore.setKeyEntry(alias, key, password, null);
			cachedKeyMap.put(alias, key);
			save();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new InternalException(e);
		}
	}

	private void save() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			keyStore.store(fileOutputStream, password);
		}
	}

	@Override
	public Optional<SecretKey> getKey(String alias) {
		SecretKey key = cachedKeyMap.get(alias);
		if (key == null) {
			key = loadKey(alias);
			if (key != null) {
				cachedKeyMap.put(alias, key);
			}
		}
		return Optional.ofNullable(key);
	}

	private SecretKey loadKey(String alias) {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			keyStore.load(fileInputStream, password);
			return (SecretKey) keyStore.getKey(alias, password);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new InternalException(e);
		}
	}

	@Override
	public SecretKey getOrCreateKey(String alias, String algorithm, int size) {
		return getKey(alias).orElseGet(() -> {
			try {
				KeyGenerator generator = KeyGenerator.getInstance(algorithm);
				generator.init(size);
				SecretKey secretKey = generator.generateKey();
				setKey(alias, secretKey);
				return secretKey;
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				throw new InternalException(e);
			}
		});
	}

}
