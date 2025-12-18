package com.vention.medfast.mail.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class EmailTokenGenerator {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private static final int CODE_BOUND = 10_000;

	private static final int RAW_TOKEN_BYTE_LENGTH = 32;

	private static final String HASH_ALGORITHM = "SHA-256";

	private EmailTokenGenerator() {
	}

	public static int generateCode() {
		return SECURE_RANDOM.nextInt(CODE_BOUND);
	}

	public static String generateRawToken() {
		byte[] randomBytes = new byte[RAW_TOKEN_BYTE_LENGTH];
		SECURE_RANDOM.nextBytes(randomBytes);
		return toHex(randomBytes);
	}

	public static String hash(String rawToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			byte[] hashedBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			return toHex(hashedBytes);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 algorithm not available", e);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

}
