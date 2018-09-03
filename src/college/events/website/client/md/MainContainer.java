package college.events.website.client.md;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialPanel;

import java.util.logging.Logger;

/**
 * Header and side nav shared across every screen
 */
public class MainContainer extends Composite {
    private static Logger logger = Logger.getLogger(MainContainer.class.getName());

    interface MainContainerUiBinder extends UiBinder<Widget, MainContainer> {
    }
    private static MainContainerUiBinder uiBinder = GWT.create(MainContainerUiBinder.class);


    @UiField
    MaterialPanel widgetContainer;

    /**
     * Default constructor
     */
    public MainContainer() {
        logger.info("MainContainer()");
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Sets the displayed screen
     *
     * @param widget Screen widget
     */
    public void setDisplayedScreen(Composite widget) {
        widgetContainer.clear();
        widgetContainer.add(widget);
    }
}