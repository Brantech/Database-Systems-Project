package college.events.website.shared.validators;

import college.events.website.client.utils.BooleanCallback;
import college.events.website.shared.errors.GenericError;
import college.events.website.shared.errors.LengthError;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import gwt.material.design.client.base.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates passwords
 */
public class PasswordValidator implements Validator<String> {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 20;

    private final BooleanCallback callback;

    /**
     * Constructor preventing invalid instantiation
     */
    @SuppressWarnings("unused")
    private PasswordValidator() {
        callback = null;
    }

    /**
     * Default constructor
     *
     * @param callback Ui callback
     */
    public PasswordValidator(BooleanCallback callback) {
        this.callback = callback;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        List<EditorError> errors = new ArrayList<>();

        if(value == null || value.isEmpty()) {
            errors.add(new GenericError("Password cannot be empty."));
            callback.onFalse();
        } else if(value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            errors.add(new LengthError("Password", MIN_LENGTH, MAX_LENGTH));
            callback.onFalse();
        } else {
            boolean hasUpper = false;
            boolean hasLower = false;
            boolean hasSpecial = false;

            for(char c : value.toCharArray()) {
                if(hasUpper && hasLower && hasSpecial) {
                    break;
                }

                if(c >= 'A' && c <= 'Z') {
                    hasUpper = true;
                } else if(c >= 'a' && c <= 'z') {
                    hasLower = true;
                } else {
                    hasSpecial = true;
                }
            }

            if(!hasLower || !hasUpper || !hasSpecial) {
                errors.add(new GenericError("Passwords must have at least one uppercase letter, lowercase letter, and a special character."));
                callback.onFalse();
            } else {
                callback.onTrue();
            }
        }

        return errors;
    }
}
