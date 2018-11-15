package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.client.md.subwidgets.RSOItem;
import college.events.website.shared.CookyKeys;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialDialog;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialToast;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RSOScreen extends Composite {
    private static Logger logger = Logger.getLogger(RSOScreen.class.getName());

    interface RSOScreenUiBinder extends UiBinder<Widget, RSOScreen> {}
    private static RSOScreenUiBinder uiBinder = GWT.create(RSOScreenUiBinder.class);

    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    @UiField
    MaterialDialog rsoViewer, rsoCreationDialog;

    @UiField
    MaterialCard rsoContainer;

    @UiField
    MaterialCard emptyState;

    @UiField
    MaterialButton rsoTypeButton, join, leave, deleteRSO;

    @UiField
    MaterialTextBox rsoName, member1, member2, member3, member4;

    @UiField
    MaterialTextArea rsoDescription;

    @UiField
    MaterialLabel name, type, description;

    private RSOMessage selected;

    private List<RSOMessage> rsos;
    private List<String> follows;

    public RSOScreen() {
        initWidget(uiBinder.createAndBindUi(this));

        cewServiceAsync.getRSOs(UiManager.getInstance().getUserInfo(), new AsyncCallback<GetRSOResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                logger.severe(caught.toString());
                MaterialToast.fireToast("Could not load RSO list");
            }

            @Override
            public void onSuccess(GetRSOResponse result) {
                if(result.isSuccess()) {
                    rsos = result.getRsos();
                    follows = result.getFollows();
                    addRSOItems();
                } else {
                    MaterialToast.fireToast("Could not load RSO list");
                }
            }
        });

        rsoViewer.close();
    }

    private void resetRSOCreator() {
        rsoName.setText(null);
        member1.setText(null);
        member2.setText(null);
        member3.setText(null);
        member4.setText(null);
    }

    private void addRSOItems() {
        if(rsos == null || rsos.size() == 0) {
            emptyState.setDisplay(Display.FLEX);
            rsoContainer.setDisplay(Display.NONE);
        } else {
            emptyState.setDisplay(Display.NONE);
            rsoContainer.setDisplay(Display.BLOCK);
        }

        rsoContainer.clear();
        for(RSOMessage m : rsos) {
            rsoContainer.add(new RSOItem(this, m));
        }
    }

    public void viewRSO(RSOMessage rso) {
        if(follows.contains(rso.getRSO_ID())) {
            join.setDisplay(Display.NONE);
            leave.setDisplay(Display.BLOCK);
        } else {
            join.setDisplay(Display.BLOCK);
            leave.setDisplay(Display.NONE);
        }

        if(rso.getADMIN_ID().equals(UiManager.getInstance().getUserInfo().getUSER_ID())) {
            deleteRSO.setDisplay(Display.BLOCK);
        } else {
            deleteRSO.setDisplay(Display.NONE);
        }

        rsoViewer.open();
        name.setText(rso.getNAME());
        type.setText(rso.getTYPE());
        description.setText(rso.getDESCRIPTION());
        this.selected = rso;
    }

    @Override
    public void onUnload() {
        rsoViewer.close();
        rsoCreationDialog.close();
    }

    @UiHandler("createRSO")
    void onCreateRSOClick(ClickEvent e) {
        rsoCreationDialog.open();
    }

    @UiHandler("cancel")
    void onCancelClick(ClickEvent e) {
        rsoCreationDialog.close();
    }

    @UiHandler("apply")
    void onApplyClick(ClickEvent e) {
        if(rsoName.getText().length() == 0) {
            MaterialToast.fireToast("RSO name cannot be empty!");
            return;
        }

        if(rsoTypeButton.getText().isEmpty()) {
            MaterialToast.fireToast("RSO must have a type!");
            return;
        }

        List<String> memberEmails = new ArrayList<>();
        memberEmails.add(member1.getText());
        memberEmails.add(member2.getText());
        memberEmails.add(member3.getText());
        memberEmails.add(member4.getText());

        if(memberEmails.stream().distinct().count() != 4) {
            MaterialToast.fireToast("Please enter four different student emails!");
            return;
        }

        cewServiceAsync.createRSO(rsoName.getText(), rsoDescription.getText(), rsoTypeButton.getText(), memberEmails, Cookies.getCookie(CookyKeys.AUTH_TOKEN), new AsyncCallback<GenericRPCResponse<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                logger.severe(caught.toString());
                MaterialToast.fireToast("Failed to send application");
            }

            @Override
            public void onSuccess(GenericRPCResponse<String> result) {
                if(result.isSuccess()) {
                    MaterialToast.fireToast("Your RSO creation request has been sent and is waiting for approval");
                    rsoCreationDialog.close();
                } else {

                }

                resetRSOCreator();
            }
        });
    }

    @UiHandler("rsoTypeDropDown")
    void onRSOTypeSelect(SelectionEvent<Widget> event) {
        MaterialLink source = (MaterialLink) event.getSelectedItem();

        rsoTypeButton.setText(source.getText());
    }

    @UiHandler("join")
    void onJoinClick(ClickEvent e) {
        cewServiceAsync.joinRSO(selected, UiManager.getInstance().getUserInfo().getAuthToken(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    MaterialToast.fireToast("RSO joined!");
                    leave.setDisplay(Display.BLOCK);
                    join.setDisplay(Display.NONE);
                }
            }
        });
    }

    @UiHandler("leave")
    void onLeaveClick(ClickEvent e) {
        cewServiceAsync.leaveRSO(selected, UiManager.getInstance().getUserInfo().getAuthToken(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    MaterialToast.fireToast("You left the RSO");
                    join.setDisplay(Display.BLOCK);
                    leave.setDisplay(Display.NONE);
                }
            }
        });
    }

    @UiHandler("close")
    void onCloseClick(ClickEvent e) {
        rsoViewer.close();
    }

    @UiHandler("deleteRSO")
    void onDeleteRSOClick(ClickEvent e) {
        cewServiceAsync.deleteRSO(selected, UiManager.getInstance().getUserInfo().getAuthToken(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Boolean result) {
                if(result) {
                    rsos.remove(selected);
                    addRSOItems();
                    rsoViewer.close();
                }
            }
        });
    }
}
