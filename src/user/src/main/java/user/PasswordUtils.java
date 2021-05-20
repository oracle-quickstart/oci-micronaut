package user;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordUtils {

    private static final Random RANDOM = new SecureRandom();
    private static final SecretKeyFactory SECRET_KEY_FACTORY;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    static {
        try {
            SECRET_KEY_FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot find hashing key factory: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a random salt to be used to hash a password.
     *
     * @return a random salt
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Returns a salted and hashed password using the provided hash.
     *
     * @param passwordString the password to be hashed
     * @param saltBase64     the salt
     * @return the hashed password
     */
    public static String hash(String passwordString, String saltBase64) {
        System.out.println("passwordString " + passwordString);
        System.out.println("saltBase64 " + saltBase64);
        char[] password = passwordString.toCharArray();
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            byte[] encoded = SECRET_KEY_FACTORY.generateSecret(spec).getEncoded();
            String hashedPassword = Base64.getEncoder().encodeToString(encoded);
            System.out.println("hashedPassword " + saltBase64);
            return hashedPassword;
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Returns true if the given password and salt match the hashed value, false otherwise.
     *
     * @param passwordString       the password to check
     * @param saltBase64           the salt used to hash the password
     * @param hashedPasswordBase64 the expected hashed value of the password
     * @return true if the given password and salt match the hashed value, false otherwise
     */
    public static boolean isExpectedPassword(String passwordString,
                                             String saltBase64,
                                             String hashedPasswordBase64) {
        return hashedPasswordBase64.equals(hash(passwordString, saltBase64));
    }
}
