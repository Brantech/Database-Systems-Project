package college.events.website.shared.validators;

import college.events.website.client.utils.BooleanCallback;
import college.events.website.shared.errors.GenericError;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import gwt.material.design.client.base.validator.Validator;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates that two MaterialTextBox's have the same text
 */
public class MatchValidator implements Validator<String> {
    private MaterialTextBox other;
    private final String FIELD_NAME_1;
    private final String FIELD_NAME_2;
    private final BooleanCallback callback;

    /**
     * Prevents invalid instantiation
     */
    @SuppressWarnings("unused")
    private MatchValidator() {
        FIELD_NAME_1 = null;
        FIELD_NAME_2 = null;
        callback = null;
    }

    /**
     * Default constructor
     *
     * @param other Other widget to match text to
     * @param fieldName1 First field's name
     * @param fieldName2 Second field's name
     */
    public MatchValidator(MaterialTextBox other, String fieldName1, String fieldName2, BooleanCallback callback) {
        this.other = other;
        FIELD_NAME_1 = fieldName1;
        FIELD_NAME_2 = fieldName2;
        this.callback = callback;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        List<EditorError> errors = new ArrayList<>();
        if((value == null && other.getText() != null) || !value.equals(other.getText())) {
            errors.add(new GenericError(FIELD_NAME_1 + " does not match " + FIELD_NAME_2));
            callback.onFalse();
        } else {
            callback.onTrue();
        }

        return errors;
    }
}
