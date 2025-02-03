package org.project.pack.classes;

import java.security.SecureRandom;

public final class Randomizer {
	static String chars = "0123456789qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP";
	private Randomizer() {}
	
	public static String generateString(int length) {
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < length; i += 1) {
			builder.append(chars.charAt(secureRandom.nextInt(chars.length())));
		}
		return builder.toString();
	}
}
