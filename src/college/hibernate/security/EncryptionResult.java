package college.hibernate.security;

/**
 * Object used to transport an encrypted message and its salt
 */
public class EncryptionResult {
    private final String encryptedMessage;
    private final String salt;

    /**
     * Prevents invalid instantiation
     */
    private EncryptionResult() {
        encryptedMessage = null;
        salt = null;
    }

    /**
     * Constructor
     *
     * @param encryptedMessage The encrypted message
     * @param salt Salt used to encrypt the message
     */
    public EncryptionResult(String encryptedMessage, String salt) {
        this.encryptedMessage = encryptedMessage;
        this.salt = salt;
    }

    /**
     * @return The encrypted message
     */
    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    /**
     * @return Salt used to encrypt the message
     */
    public String getSalt() {
        return salt;
    }
}
