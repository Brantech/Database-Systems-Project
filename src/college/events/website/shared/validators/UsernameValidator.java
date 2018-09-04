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
 * Validates usernames
 */
public class UsernameValidator implements Validator<String> {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 20;

    private final BooleanCallback callback;

    /**
     * Constructor preventing invalid instantiation
     */
    @SuppressWarnings("unused")
    private UsernameValidator() {
        callback = null;
    }

    /**
     * Default constructor
     *
     * @param callback Ui callback
     */
    public UsernameValidator(BooleanCallback callback) {
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
            errors.add(new GenericError("Username cannot be empty."));
            callback.onFalse();
        } else if(value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            errors.add(new LengthError("Username", MIN_LENGTH, MAX_LENGTH));
            callback.onFalse();
        } else {
            callback.onTrue();
        }

        return errors;
    }
}
