package college.events.website.client.md;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import gwt.material.design.client.ui.MaterialPanel;

public class HomeScreen extends Composite {
    interface HomeScreenUiBinder extends UiBinder<MaterialPanel, HomeScreen> {
    }

    private static HomeScreenUiBinder ourUiBinder = GWT.create(HomeScreenUiBinder.class);

    /**
     * Default constructor
     */
    public HomeScreen() {
        initWidget(ourUiBinder.createAndBindUi(this));


    }
}