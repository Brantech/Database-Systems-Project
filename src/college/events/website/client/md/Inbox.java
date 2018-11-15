package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.client.md.subwidgets.InboxMessage;
import college.events.website.shared.CookyKeys;
import college.events.website.shared.ScreenEnum;
import college.events.website.shared.messages.SuperAdminMessage;
import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialDialog;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Inbox extends Composite {
    private static Logger logger = Logger.getLogger(Inbox.class.getName());
    interface InboxUiBinder extends UiBinder<Widget, Inbox> {}

    private static InboxUiBinder uiBinder = GWT.create(InboxUiBinder.class);

    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    @UiField
    MaterialColumn messageContainer;

    @UiField
    MaterialDialog messageViewer;

    @UiField
    MaterialTextBox subject;

    @UiField
    MaterialTextArea content;

    private List<SuperAdminMessage> messages;

    private SuperAdminMessage selected;

    public Inbox() {
        initWidget(uiBinder.createAndBindUi(this));

        init();
    }

    private void init() {
        cewServiceAsync.getSuperAdminMessages(Cookies.getCookie(CookyKeys.AUTH_TOKEN), new AsyncCallback<GenericRPCResponse<ArrayList<SuperAdminMessage>>>() {
            @Override
            public void onFailure(Throwable caught) {
                logger.severe(caught.getLocalizedMessage());
                Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                UiManager.getInstance().displayScreen(ScreenEnum.HOME);
            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<SuperAdminMessage>> result) {
                if(result.isSuccess()) {
                    messages = result.getPayload();
                    showMessages();
                } else {
                    Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                    UiManager.getInstance().displayScreen(ScreenEnum.HOME);
                }
            }
        });
    }

    private void showMessages() {
        for(SuperAdminMessage m : messages) {
            InboxMessage i = new InboxMessage(m.getSubject(), m.getSendDate(), new Callback() {
                @Override
                public void onFailure(Object reason) {

                }

                @Override
                public void onSuccess(Object result) {
                    messageViewer.open();
                    selected = m;
                    subject.setText(m.getSubject());
                    content.setText(m.getMessage());
                }
            });
            messageContainer.add(i);
        }
    }

    private void approveRSO() {
        cewServiceAsync.approveRSOApplication(selected.getId(), UiManager.getInstance().getUserInfo().getAuthToken(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    int i = messages.indexOf(selected);
                    messageContainer.remove(i);
                    messages.remove(i);
                    messageViewer.close();
                }
            }
        });
    }

    private void approveEvent() {
        cewServiceAsync.approveEvent(selected.getId(), UiManager.getInstance().getUserInfo().getAuthToken(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                logger.severe("asdf");
                if(result) {
                    int i = messages.indexOf(selected);
                    messageContainer.remove(i);
                    messages.remove(i);
                    messageViewer.close();
                }
            }
        });
    }

    @UiHandler("approve")
    void onClickApprove(ClickEvent event) {
        if(selected.getMessageType().equals("Event")) {
            approveEvent();
        } else {
            approveRSO();
        }
    }

    @UiHandler("decline")
    void onClickDecline(ClickEvent event) {
        cewServiceAsync.declineMessage(selected.getId(), UiManager.getInstance().getUserInfo().getAuthToken(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    int i = messages.indexOf(selected);
                    messageContainer.remove(i);
                    messages.remove(i);
                    messageViewer.close();
                }
            }
        });
    }

    @UiHandler("close")
    void onClickClose(ClickEvent event) {
        messageViewer.close();
    }

}
