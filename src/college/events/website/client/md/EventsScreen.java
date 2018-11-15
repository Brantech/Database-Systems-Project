package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.shared.CookyKeys;
import college.events.website.shared.Events;
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
import gwt.material.design.client.ui.MaterialDialog;
import java.util.ArrayList;
import java.util.List;

public class EventsScreen extends Composite {
    interface EventsScreenUiBinder extends UiBinder<Widget, EventsScreen> {
    }
    private static EventsScreenUiBinder uiBinder = GWT.create(EventsScreenUiBinder.class);
    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    @UiField
    MaterialDialog eventCreationDialog;

    @UiField
    MaterialButton cancel, createEvent, submit;

    private List<Events> events;

    public EventsScreen() {
        initWidget(uiBinder.createAndBindUi(this));

        cewServiceAsync.getEvents(Cookies.getCookie(CookyKeys.AUTH_TOKEN), new AsyncCallback<GenericRPCResponse<ArrayList<Events>>>() {
            @Override
            public void onFailure(Throwable caught) {
                Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                UiManager.getInstance().displayScreen(ScreenEnum.HOME);
            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<Events>> result) {
                if(result.isSuccess()) {
                    events = result.getPayload();
                    init();
                } else {
                    Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                    UiManager.getInstance().displayScreen(ScreenEnum.HOME);
                }

            }
        });

    }

    private void init() {

    }

    @UiHandler("createEvent")
    void onCreateEventClick(ClickEvent event) {
        eventCreationDialog.open();
    }

    @UiHandler("cancel")
    void onCancelClick(ClickEvent event) {
        eventCreationDialog.close();
    }

    @UiHandler("submit")
    void onSubmitClick(ClickEvent event) {

    }
}
