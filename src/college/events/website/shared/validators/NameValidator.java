package college.events.website.shared.validators;

import college.events.website.client.utils.BooleanCallback;
import college.events.website.shared.errors.GenericError;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import gwt.material.design.client.base.validator.Validator;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates people's names
 */
public class NameValidator implements Validator<String> {
    private final BooleanCallback callback;

    /**
     * Constructor preventing invalid instantiation
     */
    private NameValidator() {
        callback = null;
    }

    /**
     * Default constructor
     * @param callback Ui callback
     */
    public NameValidator(BooleanCallback callback) {
        this.callback = callback;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        List<EditorError> errors = new ArrayList<>();

        for(char c : value.toLowerCase().toCharArray()) {
            if(c - 'a' < 0 || c - 'a' > 'z') {
                errors.add(new GenericError("Names can only have letters in the English alphabet. (A-Z, a-z)"));
                break;
            }
        }

        if(!errors.isEmpty()) {
            callback.onFalse();
        } else if(value == null || value.isEmpty()) {
            errors.add(new GenericError("Field cannot be empty"));
            callback.onFalse();
        } else {
            callback.onTrue();
        }

        return errors;
    }
}
