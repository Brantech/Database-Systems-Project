package college.hibernate.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Used to encrypt and authenticate strings
 */
public class EncryptionService {
    /**
     * Encryption algorithm used
     */
    private static final String ENCRYPTION_ALGORITHM = "PBKDF2WithHmacSHA512";

    /**
     * Number of times to apply the encryption algorithm
     */
    private static final int ENCRYPTION_COUNT = 10000;

    /**
     * Length of the encrypted message
     */
    private static final int ENCRYPTED_LENGTH = 512;

    /**
     * Prevents instantiation
     */
    private EncryptionService() {}

    /**
     * Encrypts the message using the passed in salt
     *
     * @param message Message to encrypt
     * @param salt Salt to encrypt the message with
     * @return Encrypted message and salt used
     */
    private static EncryptionResult encrypt(String message, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(message.toCharArray(), salt, ENCRYPTION_COUNT, ENCRYPTED_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
            return new EncryptionResult(new String(factory.generateSecret(spec).getEncoded()), new String(salt));
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    /**
     * Encrypts the message
     *
     * @param message Message to encrypt
     * @return Encrypted message and salt used
     */
    public static EncryptionResult encrypt(String message) {
        try {
            byte[] salt = getSalt();
            KeySpec spec = new PBEKeySpec(message.toCharArray(), salt, ENCRYPTION_COUNT, ENCRYPTED_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
            return new EncryptionResult(new String(factory.generateSecret(spec).getEncoded()), new String(salt));
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    /**
     * Checks if the message equals the encrypted message
     *
     * @param message Message to encrypt
     * @param eMessage Encrypted message to compare
     * @param salt Salt used to encrypt eMessage
     * @return
     */
    public static boolean authenticate(String message, String eMessage, String salt) {
        if(message == null && eMessage != null) {
            return false;
        }
        return message.equals(encrypt(message, salt.getBytes()));
    }

    /**
     * Generates 512 bits of salt
     * @return Salt
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom rnd = new SecureRandom();

        // 512 bit salt
        byte[] salt = new byte[64];
        rnd.nextBytes(salt);

        return salt;
    }
}
