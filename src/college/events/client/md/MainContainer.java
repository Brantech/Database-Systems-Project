package college.events.client.md;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import gwt.material.design.client.ui.MaterialPanel;

public class MainContainer extends Composite {
    interface MainContainerUiBinder extends UiBinder<MaterialPanel, MainContainer> {
    }

    private static MainContainerUiBinder ourUiBinder = GWT.create(MainContainerUiBinder.class);

    public MainContainer() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}