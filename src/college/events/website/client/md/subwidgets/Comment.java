package college.events.website.client.md.subwidgets;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.ui.*;

public class Comment extends Composite {
    interface CommentUiBinder extends UiBinder<Widget, Comment> {}
    private static CommentUiBinder uiBinder = GWT.create(CommentUiBinder.class);
    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    @UiField
    MaterialLabel author;

    @UiField
    MaterialTextBox commentTitle;

    @UiField
    MaterialTextArea commentMessage;

    @UiField
    MaterialIcon cancel, save, edit;

    private String title, message, cID;

    private Comment() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Comment(String title, String message, String name, String cID) {
        initWidget(uiBinder.createAndBindUi(this));

        author.setText("By: " + name);
        commentTitle.setText(title);
        commentMessage.setText(message);

        this.title = title;
        this.message = message;
        this.cID = cID;
    }

    public void setEditable(boolean editable) {
        if(editable) {
            edit.setDisplay(Display.BLOCK);
        } else {
            edit.setDisplay(Display.NONE);
        }
    }

    @UiHandler("edit")
    void onEditClick(ClickEvent event) {
        edit.setDisplay(Display.NONE);
        cancel.setDisplay(Display.BLOCK);
        save.setDisplay(Display.BLOCK);

        commentTitle.setReadOnly(false);
        commentMessage.setReadOnly(false);
    }

    @UiHandler("cancel")
    void onCancelClick(ClickEvent event) {
        edit.setDisplay(Display.BLOCK);
        cancel.setDisplay(Display.NONE);
        save.setDisplay(Display.NONE);

        commentTitle.setText(title);
        commentTitle.setReadOnly(true);

        commentMessage.setText(message);
        commentMessage.setReadOnly(true);
    }

    @UiHandler("save")
    void onSaveClick(ClickEvent event) {

        cewServiceAsync.editComment(UiManager.getInstance().getUserInfo().getAuthToken(), commentTitle.getText(), commentMessage.getText(), cID,
                new AsyncCallback<GenericRPCResponse<String>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(GenericRPCResponse<String> result) {
                        if(result.isSuccess()) {
                            title = commentTitle.getText();
                            commentTitle.setReadOnly(true);

                            message = commentMessage.getText();
                            commentMessage.setReadOnly(true);

                            edit.setDisplay(Display.BLOCK);
                            cancel.setDisplay(Display.NONE);
                            save.setDisplay(Display.NONE);
                        } else {
                            MaterialToast.fireToast(result.getPayload());
                        }
                    }
                }
        );
    }
}
