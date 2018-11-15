package college.events.website.client.md;

import college.events.website.client.UiManager;
import college.events.website.shared.CookyKeys;
import college.events.website.shared.ScreenEnum;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialPanel;

import gwt.material.design.client.ui.MaterialSideNavPush;
import java.util.logging.Logger;

import static gwt.material.design.jquery.client.api.JQuery.$;

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

    @UiField
    MaterialNavBar navBar;

    @UiField
    MaterialSideNavPush sideNav;

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

    @UiHandler("inbox")
    void onInboxClick(ClickEvent event) {
        UiManager.getInstance().displayScreen(ScreenEnum.INBOX);
    }

    @UiHandler("events")
    void onEventsClick(ClickEvent event) {
        UiManager.getInstance().displayScreen(ScreenEnum.EVENTS);
    }

    @UiHandler("rso")
    void onRSOClick(ClickEvent event) {
        UiManager.getInstance().displayScreen(ScreenEnum.RSO);
    }

    @UiHandler("logout")
    void onLogoutClick(ClickEvent event) {
        Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
        UiManager.getInstance().displayScreen(ScreenEnum.HOME);
    }
}