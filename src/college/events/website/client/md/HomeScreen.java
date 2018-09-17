package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.client.utils.BooleanCallback;
import college.events.website.shared.ScreenEnum;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.validators.EmptyValidator;
import college.events.website.shared.validators.MatchValidator;
import college.events.website.shared.validators.NameValidator;
import college.events.website.shared.validators.PasswordValidator;
import college.events.website.shared.validators.UsernameValidator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialTextBox;
import java.util.logging.Logger;

public class HomeScreen extends Composite {
    @UiField
    MaterialColumn loginContainer;

    @UiField
    MaterialTextBox username;

    @UiField
    MaterialTextBox password;

    @UiField
    MaterialButton loginButton;

    @UiField
    MaterialButton registerButton;

    @UiField
    MaterialColumn registerContainer;

    @UiField
    MaterialButton backButton;

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

    @UiField
    MaterialButton submitButton;

    private static Logger logger = Logger.getLogger(HomeScreen.class.getName());
    interface HomeScreenUiBinder extends UiBinder<Widget, HomeScreen> {

    }

    private static HomeScreenUiBinder uiBinder = GWT.create(HomeScreenUiBinder.class);

    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);


    /**
     * Default constructor
     */
    public HomeScreen() {
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
    }

    private void initialize() {
        loginButton.addClickHandler(e ->{
            cewServiceAsync.login(username.getText(), password.getText(), new AsyncCallback<GenericRPCResponse<String>>() {
                @Override
                public void onFailure(Throwable caught) {
                    logger.warning("Incorrect username or password");
                }

                @Override
                public void onSuccess(GenericRPCResponse<String> result) {
                    UiManager.getInstance().displayScreen(ScreenEnum.EVENTS);
                }
            });
        });

        registerButton.addClickHandler(e -> {
            loginContainer.setDisplay(Display.NONE);
            registerContainer.setDisplay(Display.BLOCK);
        });

        backButton.addClickHandler(e -> {
            loginContainer.setDisplay(Display.BLOCK);
            registerContainer.setDisplay(Display.NONE);
        });

        submitButton.addClickHandler(event -> {
            boolean valid = true;
            valid &= firstName.validate();
            valid &= lastName.validate();
            valid &= rUsername.validate();
            valid &= rPassword.validate();
            valid &= rPassword2.validate();
            valid &= email.validate();
            valid &= email2.validate();
            if(valid) {
                cewServiceAsync.createAccount(rUsername.getText(), rPassword.getText(), firstName.getText(), lastName.getText(), email.getText(), new AsyncCallback<GenericRPCResponse<String>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        logger.warning("Failed to create new account");
                        //TODO: Tell user why
                    }

                    @Override
                    public void onSuccess(GenericRPCResponse<String> result) {
                        logger.warning("New user has been created");
                        //TODO: Transition to next screen
                    }
                });
            }
        });

        addValidators();
    }

    /**
     * Add validators for the text input
     */
    private void addValidators() {

        firstName.addFocusHandler(getFieldFocusHandler(firstName));
        firstName.addValidator(new NameValidator(getValidatorCallback(firstName)));

        lastName.addFocusHandler(getFieldFocusHandler(lastName));
        lastName.addValidator(new NameValidator(getValidatorCallback(lastName)));

        rUsername.addFocusHandler(getFieldFocusHandler(rUsername));
        rUsername.addValidator(new UsernameValidator(getValidatorCallback(rUsername)));

        rPassword.addFocusHandler(getFieldFocusHandler(rPassword));
        rPassword.addValidator(new PasswordValidator(getValidatorCallback(rPassword)));

        rPassword2.addFocusHandler(getFieldFocusHandler(rPassword2));
        rPassword2.addValidator(new MatchValidator(rPassword, "Password 1", "Password 2", getValidatorCallback(rPassword2)));

        email.addFocusHandler(getFieldFocusHandler(email));
        email.addValidator(new EmptyValidator(getValidatorCallback(email)));

        email2.addFocusHandler(getFieldFocusHandler(email2));
        email2.addValidator(new MatchValidator(email, "Email 1", "Email 2", getValidatorCallback(email2)));
    }

    private FocusHandler getFieldFocusHandler(MaterialTextBox widget) {
        return event -> {
            widget.getWidget(0).getElement().removeAttribute("style");
            widget.getWidget(1).getElement().removeAttribute("style");
        };
    }

    private BooleanCallback getValidatorCallback(MaterialTextBox widget) {
        return new BooleanCallback() {
            @Override
            public void onFalse() {
                widget.getWidget(0).getElement().setAttribute("style", "border: 2px solid red !important;");
                widget.getWidget(1).getElement().setAttribute("style", "color: red !important;");
            }

            @Override
            public void onTrue() {
                widget.getWidget(0).getElement().removeAttribute("syle");
                widget.getWidget(1).getElement().removeAttribute("style");
            }
        };
    }
}