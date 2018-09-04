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

    private static final String INVALID_BORDER = "border: 1px solid red !important";


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

        addValidators();
    }

    private void addValidators() {
        firstName.addFocusHandler(e -> firstName.getWidget(0).getElement().setAttribute("style", ""));
        firstName.addBlurHandler(e -> {
            if(firstName.getText() == null
                    || firstName.getText().isEmpty()) {
                firstName.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                firstName.getWidget(0).getElement().setAttribute("style", "");
            }
        });

        lastName.addFocusHandler(e -> lastName.getWidget(0).getElement().setAttribute("style", ""));
        lastName.addBlurHandler(e -> {
            if(lastName.getText() == null
                    || lastName.getText().isEmpty()) {
                lastName.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                lastName.getWidget(0).getElement().setAttribute("style", "");
            }
        });

        rUsername.addFocusHandler(e -> rUsername.getWidget(0).getElement().setAttribute("style", ""));
        rUsername.addBlurHandler(e -> {
            if(rUsername.getText() == null
                    || rUsername.getText().isEmpty()
                    || rUsername.getText().length() < 5
                    || rUsername.getText().length() > 20) {
                rUsername.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                rUsername.getWidget(0).getElement().setAttribute("style", "");
            }
        });

        rPassword.addFocusHandler(e -> rPassword.getWidget(0).getElement().setAttribute("style", ""));
        rPassword.addBlurHandler(e -> {
            if(rPassword.getText() == null
                    || rPassword.getText().isEmpty()
                    || rPassword.getText().length() < 5
                    || rPassword.getText().length() > 20) {
                rPassword.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                rPassword.getWidget(0).getElement().setAttribute("style", "");
            }
        });

        rPassword2.addFocusHandler(e -> rPassword2.getWidget(0).getElement().setAttribute("style", ""));
        rPassword2.addBlurHandler(e -> {
            if(rPassword2.getText() == null
                    || rPassword2.getText().isEmpty()
                    || rPassword2.getText().length() < 5
                    || rPassword2.getText().length() > 20
                    || !rPassword2.getText().equals(rPassword.getText())) {
                rPassword2.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                rPassword2.getWidget(0).getElement().setAttribute("style", "");
            }
        });

        email.addFocusHandler(e -> email.getWidget(0).getElement().setAttribute("style", ""));
        email.addBlurHandler(e -> {
            if(email.getText() == null
                    || email.getText().isEmpty()
                    || email.getText().length() < 5) {
                email.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                email.getWidget(0).getElement().setAttribute("style", "");
            }
        });

        email2.addFocusHandler(e -> email2.getWidget(0).getElement().setAttribute("style", ""));
        email2.addBlurHandler(e -> {
            if(email2.getText() == null
                    || email2.getText().isEmpty()
                    || email2.getText().length() < 5
                    || !email2.getText().equals(email.getText())) {
                email2.getWidget(0).getElement().setAttribute("style", INVALID_BORDER);
            } else {
                email2.getWidget(0).getElement().setAttribute("style", "");
            }
        });
    }
}