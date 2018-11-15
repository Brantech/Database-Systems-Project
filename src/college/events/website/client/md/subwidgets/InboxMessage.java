package college.events.website.client.md.subwidgets;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialRow;
import java.util.Date;

public class InboxMessage extends Composite {
    interface InboxMessageUiBinder extends UiBinder<Widget, InboxMessage> {}
    private static InboxMessageUiBinder uiBinder = GWT.create(InboxMessageUiBinder.class);

    @UiField
    MaterialLabel subject;

    @UiField
    MaterialLabel date;

    @UiField
    MaterialRow mainContainer;

    private Callback callback;

    public InboxMessage() {
        initWidget(uiBinder.createAndBindUi(this));

    }
    public InboxMessage(String subject, long date, Callback callback) {
        initWidget(uiBinder.createAndBindUi(this));
        init(subject, date);

        this.callback = callback;
    }

    private void init(String subject, long date) {
        this.subject.setText(subject);
        this.date.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(date)));
    }

    public void setSubject(String subject) {
        this.subject.setText(subject);
    }

    public void setDate(long date) {
        this.date.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(date)));
    }

    @UiHandler("mainContainer")
    void onClick(ClickEvent event) {
        if(callback != null) {
            callback.onSuccess(null);
        }
    }
}
