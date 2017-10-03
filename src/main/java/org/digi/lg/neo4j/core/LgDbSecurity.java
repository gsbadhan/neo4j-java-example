/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.core;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.digi.lg.neo4j.exception.SecurityException;

public class LgDbSecurity {
	private static final String ALGO = "AES";
	private static final byte[] SALT = "hdASR6372768B@#$".getBytes();
	private static SecretKeySpec keySpec = null;
	static {
		keySpec = new SecretKeySpec(SALT, ALGO);
	}

	private LgDbSecurity() {
	}

	public static String encrypt(String input) {
		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));
		} catch (Exception e) {
			throw new SecurityException(e, ErrorCodes.INVALID_TOKEN);
		}
	}

	public static String dcrypt(String input) {
		try {
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decoded = Base64.getDecoder().decode(input.getBytes());
			return new String(cipher.doFinal(decoded));
		} catch (Exception e) {
			throw new SecurityException(e, ErrorCodes.INVALID_TOKEN);
		}
	}
}
