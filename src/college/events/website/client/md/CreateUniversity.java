package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.shared.CookyKeys;
import college.events.website.shared.ScreenEnum;
import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.logging.Logger;

public class CreateUniversity extends Composite {

    private static Logger logger = Logger.getLogger(HomeScreen.class.getName());
    interface CreateUniversityUiBinder extends UiBinder<Widget, CreateUniversity> {

    }

    private static CreateUniversityUiBinder uiBinder = GWT.create(CreateUniversityUiBinder.class);

    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    @UiField
    MaterialTextBox name, location;

    @UiField
    MaterialTextArea description;

    @UiField
    MaterialButton createUniButton;

    public CreateUniversity() {
        initWidget(uiBinder.createAndBindUi(this));


    }

    @UiHandler("createUniButton")
    void onCreateClick(ClickEvent event) {
        createUniButton.setEnabled(false);

        cewServiceAsync.createUniversity(name.getText(), location.getText(), description.getText(), Cookies.getCookie(CookyKeys.AUTH_TOKEN), new AsyncCallback<GenericRPCResponse<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                logger.severe(caught.toString());
                createUniButton.setEnabled(true);
            }

            @Override
            public void onSuccess(GenericRPCResponse<String> result) {
                if(result.isSuccess()) {
                    // TODO: Navigate away
                } else {
                    Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                    UiManager.getInstance().displayScreen(ScreenEnum.HOME);
                }
            }
        });
    }
}
