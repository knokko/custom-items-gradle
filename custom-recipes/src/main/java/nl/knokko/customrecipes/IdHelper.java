package nl.knokko.customrecipes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdHelper {

    public static String createHash(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(source.getBytes(StandardCharsets.UTF_8));
            byte[] raw = digest.digest();
            char[] result = new char[raw.length * 2];
            for (int index = 0; index < raw.length; index++) {
                int positive = raw[index] & 0xFF;
                result[2 * index] = (char) ('a' + (positive / 16));
                result[2 * index + 1] = (char) ('a' + (positive % 16));
            }
            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
