package es.sindicato.intelligence.user.application;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TemporaryPasswordGenerator {

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SYMBOLS = "!@#$%&*()-_=+";
    private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;
    private static final int PASSWORD_LENGTH = 14;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);

        builder.append(pick(UPPER));
        builder.append(pick(LOWER));
        builder.append(pick(DIGITS));
        builder.append(pick(SYMBOLS));

        while (builder.length() < PASSWORD_LENGTH) {
            builder.append(pick(ALL));
        }

        return shuffle(builder.toString());
    }

    private char pick(String chars) {
        return chars.charAt(secureRandom.nextInt(chars.length()));
    }

    private String shuffle(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }
}
