package college.events.client.md;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

public class HomeScreen extends Composite {
    interface HomeScreenUiBinder extends UiBinder<gwt.material.design.client.ui.MaterialPanel, college.events.client.md.HomeScreen> {
    }

    private static HomeScreenUiBinder ourUiBinder = GWT.create(HomeScreenUiBinder.class);

    public HomeScreen() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}