package college.events.website.shared.validators;

import college.events.website.client.utils.BooleanCallback;
import college.events.website.shared.errors.GenericError;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import gwt.material.design.client.base.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if string is null or empty
 */
public class EmptyValidator implements Validator<String> {
    /**
     * UI callback
     */
    private final BooleanCallback callback;

    /**
     * Prevents invalid instantiation
     */
    private EmptyValidator() {
        callback = null;
    }

    /**
     * Default constructor
     *
     * @param callback Ui callback
     */
    public EmptyValidator(BooleanCallback callback) {
        this.callback = callback;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<EditorError> validate(Editor editor, String value) {
        List<EditorError> errors = new ArrayList<>();
        if(value == null || value.isEmpty()) {
            errors.add(new GenericError("Field cannot be empty"));
            callback.onFalse();
        } else {
            callback.onTrue();
        }
        return errors;
    }
}
