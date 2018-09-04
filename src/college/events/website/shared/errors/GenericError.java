package college.events.website.shared.errors;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

/**
 * Class that all error classes in this package are based on.
 */
public class GenericError implements EditorError {
    /**
     * Consumed status
     */
    private boolean consumed;

    /**
     * Error message
     */
    String message;

    /**
     * Default constructor
     *
     * @param message Error message
     */
    public GenericError(String message) {
        this.message = message;
    }

    @Override
    public String getAbsolutePath() {
        return null;
    }

    @Override
    public Editor<?> getEditor() {
        return null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public Object getUserData() {
        return null;
    }

    @Override
    public Object getValue() {
        return message;
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
}
