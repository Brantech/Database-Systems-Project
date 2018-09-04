package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.validator.SizeValidator;
import gwt.material.design.client.base.validator.Validator;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HomeScreen extends Composite {
    @UiField
    MaterialColumn loginContainer;

    @UiField
    MaterialButton loginButton;

    @UiField
    MaterialColumn registerContainer;

    @UiField
    MaterialButton registerButton;

    @UiField
    MaterialTextBox firstName;

    @UiField
    MaterialTextBox lastName;

    @UiField
    MaterialTextBox rUsername;

    @UiField
    MaterialTextBox rPassword;

    @UiField
    MaterialTextBox rPassword2;

    @UiField
    MaterialTextBox email;

    @UiField
    MaterialTextBox email2;

    private static Logger logger = Logger.getLogger(HomeScreen.class.getName());

    interface HomeScreenUiBinder extends UiBinder<Widget, HomeScreen> {
    }

    private static HomeScreenUiBinder ourUiBinder = GWT.create(HomeScreenUiBinder.class);

    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    /**
     * Default constructor
     */
    public HomeScreen() {
        initWidget(ourUiBinder.createAndBindUi(this));

        initialize();
    }

    private void initialize() {
        registerButton.addClickHandler(e -> {
            loginContainer.setDisplay(Display.NONE);
            registerContainer.setDisplay(Display.BLOCK);
        });
    }

    private void addValidators() {

    }
}