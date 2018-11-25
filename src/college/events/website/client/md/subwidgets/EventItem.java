package college.events.website.client.md.subwidgets;

import college.events.website.shared.messages.EventMessage;
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

public class EventItem extends Composite {
    interface EventItemUiBinder extends UiBinder<Widget, EventItem> {}
    private static EventItemUiBinder uiBinder = GWT.create(EventItemUiBinder.class);

    @UiField
    MaterialLabel name, university, category, date;

    @UiField
    MaterialRow mainContainer;

    private EventMessage event;

    private Callback<Void, Void> callback;

    public EventItem() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public EventItem(EventMessage event, Callback<Void, Void> callback) {
        initWidget(uiBinder.createAndBindUi(this));

        this.event = event;
        this.callback = callback;

        init();
    }

    private void init() {
        name.setText(event.getName());
        university.setText(event.getUniversity());
        category.setText(event.getCategory());
        date.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(Long.parseLong(event.getDate().split(" ")[0]))));
    }

    @UiHandler("mainContainer")
    void onClick(ClickEvent e) {
        if(callback != null) {
            callback.onSuccess(null);
        }
    }

}
