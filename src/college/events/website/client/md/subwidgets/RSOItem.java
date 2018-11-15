package college.events.website.client.md.subwidgets;

import college.events.website.client.md.RSOScreen;
import college.events.website.shared.messages.RSOMessage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;

public class RSOItem extends Composite {
    interface RSOItemUiBinder extends UiBinder<Widget, RSOItem> {
    }
    private static RSOItemUiBinder uiBinder = GWT.create(RSOItemUiBinder.class);

    @UiField
    MaterialLabel name, type, members;

    private RSOScreen parent;
    private RSOMessage message;

    public RSOItem() {
        initWidget(uiBinder.createAndBindUi(this));

    }

    public RSOItem(RSOScreen parent, RSOMessage message) {
        initWidget(uiBinder.createAndBindUi(this));

        this.name.setText(message.getNAME());
        this.type.setText(message.getTYPE());
        this.members.setText(message.getMEMBERS() + "");

        this.parent = parent;
        this.message = message;
    }

    @UiHandler("mainContainer")
    void onClick(ClickEvent e) {
        parent.viewRSO(message);
    }
}
