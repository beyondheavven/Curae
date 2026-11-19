package com.vention.curae.registration.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomPasswordGenerator {

	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

	private static final String NUMBERS = "0123456789";

	private static final String ALL_CHARS = LOWERCASE + NUMBERS;

	public static String generate(int length) {
		if (length < 2) {
			throw new IllegalArgumentException("Password length must be at least 2 characters");
		}

		Random random = new SecureRandom();

		String letter = RandomStringUtils.random(1, 0, 0, false, false, LOWERCASE.toCharArray(), random);
		String number = RandomStringUtils.random(1, 0, 0, false, false, NUMBERS.toCharArray(), random);

		String rest = RandomStringUtils.random(length - 2, 0, 0, false, false, ALL_CHARS.toCharArray(), random);

		String password = letter.concat(number).concat(rest);
		List<Character> chars = new ArrayList<>(password.chars().mapToObj(c -> (char) c).toList());

		Collections.shuffle(chars);
		return chars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	}

}
