package college.events.hibernate.security;

import com.google.gson.JsonObject;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Used to encrypt and authenticate strings
 */
public class EncryptionService {
    /**
     * Password for the key generation factory
     */
    private static final String FACTORY_PASS = "PROJECT KEY";

    /**
     * Encryption algorithm used
     */
    private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Algorithm used to generate keys
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * Arguments for object applying the encryption algorithm
     */
    private static final String CIPHER_ARGUMENTS = "AES/CBC/PKCS5Padding";

    /**
     * Number of times to apply the encryption algorithm
     */
    private static final int ENCRYPTION_COUNT = 10000;

    /**
     * Length of the encrypted message
     */
    private static final int ENCRYPTED_LENGTH = 256;

    /**
     * Length of the the generated salt
     */
    private static final int SALT_LENGTH = 32;

    /**
     * Initialization vector length
     */
    private static final int IV_LENGTH = 16;

    /**
     * Prevents instantiation
     */
    private EncryptionService() {}

    /**
     * Encrypts the message
     *
     * @param message Message to encrypt
     * @return Encrypted message and salt used
     */
    public static String encrypt(String message) {
        try {
            // Generate the secret key

            byte[] salt = getSalt();
            byte[] iv = getIV();

            SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(FACTORY_PASS.toCharArray(), salt, ENCRYPTION_COUNT, ENCRYPTED_LENGTH);

            SecretKey sk = factory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(sk.getEncoded(), KEY_ALGORITHM);

            // Encrypt the message

            Cipher cipher = Cipher.getInstance(CIPHER_ARGUMENTS);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

            AlgorithmParameters params = cipher.getParameters();

            byte[] encrypted = cipher.doFinal(message.getBytes("UTF-8"));

            // Return results

            encrypted = concatArray(salt, concatArray(iv, encrypted));

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    /**
     * Decrypts the passed in message
     *
     * @param cypherText Encrypted message
     * @return Decrypted message
     */
    public static String decrypt(String cypherText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ARGUMENTS);

            ByteBuffer bytes = ByteBuffer.wrap(Base64.getDecoder().decode(cypherText));

            // Parse the message
            byte[] salt = new byte[SALT_LENGTH];
            bytes.get(salt, 0, SALT_LENGTH);

            byte[] iv = new byte[IV_LENGTH];
            bytes.get(iv, 0, iv.length);

            byte[] text = new byte[bytes.remaining()];
            bytes.get(text);

            // Get key

            SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(FACTORY_PASS.toCharArray(), salt, ENCRYPTION_COUNT, ENCRYPTED_LENGTH);

            SecretKey sk = factory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(sk.getEncoded(), KEY_ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

            return new String(cipher.doFinal(text));
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
     * @return Are they equal?
     */
    public static boolean authenticate(String message, String eMessage) {
        if(message == null && eMessage != null) {
            return false;
        }

        return message.equals(decrypt(eMessage));
    }

    /**
     * Creates an authentication token with the username and password
     *
     * @param username
     * @param password
     * @return Encrypted token
     */
    public static String createToken(String username, String password) {
        JsonObject token = new JsonObject();
        token.addProperty("username", username);
        token.addProperty("password", password);

        return encrypt(token.toString());
    }

    /**
     * Generates 256 bits of salt
     * @return Salt
     */
    private static byte[] getSalt() {
        SecureRandom rnd = new SecureRandom();

        // 256 bit iv
        byte[] salt = new byte[SALT_LENGTH];
        rnd.nextBytes(salt);

        return salt;
    }

    /**
     * Generates an initialization vector
     * @return IV
     */
    private static byte[] getIV() {
        SecureRandom rnd = new SecureRandom();

        byte[] iv = new byte[IV_LENGTH];
        rnd.nextBytes(iv);

        return iv;
    }

    /**
     * Adds array b to the end of a
     *
     * @param a
     * @param b
     * @return New concatenated array
     */
    private static byte[] concatArray(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];

        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }
}
