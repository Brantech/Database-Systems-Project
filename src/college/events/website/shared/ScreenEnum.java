package college.events.website.shared;

/**
 * Used for transitioning between the available screens
 */
public class ScreenEnum {
    public static final ScreenEnum HOME = new ScreenEnum("home");
    public static final ScreenEnum EVENTS = new ScreenEnum("events");

    /**
     * String value of the enum
     */
    private String value;

    /**
     * Constructor preventing outside instantiation
     */
    @SuppressWarnings("unused")
    private ScreenEnum() {
    }

    /**
     * Constructor
     *
     * @param value String value of the enum
     */
    private ScreenEnum(String value) {
        this.value = value;
    }

    /**
     * Translates the string into an enum
     *
     * @param str String to translate
     * @return Screen enum
     */
    public static ScreenEnum valueOf(String str) {
        if(HOME.value.equals(str)) {
            return HOME;
        }

        return null;
    }

    /**
     * Checks if the passed enum is equal to this one
     *
     * @param other Other enum
     * @return IsEqual
     */
    public boolean equals(ScreenEnum other) {
        return value.equals(other.value);
    }

    /**
     * Checks if the passed string is equal to this enum's value
     *
     * @param other String to compare
     * @return IsEqual
     */
    public boolean equals(String other) {
        return value.equals(other);
    }
}
