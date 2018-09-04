package college.events.website.shared.errors;

/**
 * Used to display invalid length errors
 */
public class LengthError extends GenericError {
    enum Mode {
        MIN, MAX
    }

    /**
     * Prevents the instantiation of a blank error
     */
    private LengthError() {
        super(null);
    }

    /**
     * Constructor for length not being between min and max
     *
     * @param fieldName Subject of the error
     * @param min Minimum length
     * @param max Max length
     */
    public LengthError(String fieldName, int min, int max) {
        super(fieldName + " must be between " + min + " and " + max + " characters.");
    }
}
