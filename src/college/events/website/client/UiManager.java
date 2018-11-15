package college.events.website.client;

import college.events.website.client.md.CreateUniversity;
import college.events.website.client.md.EventsScreen;
import college.events.website.client.md.HomeScreen;
import college.events.website.client.md.Inbox;
import college.events.website.client.md.MainContainer;
import college.events.website.client.md.RSOScreen;
import college.events.website.shared.ScreenEnum;
import college.events.website.shared.messages.UserInfo;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Singleton class in charge of managing the displayed screens
 */
public class UiManager {
    /**
     * Instance of the UiManager
     */
    private static UiManager instance;

    /**
     * Main container of the displayed screens
     */
    private MainContainer mainContainer;

    private UserInfo userInfo;

    /**
     * Constructor preventing outside instantiation
     */
    private UiManager() {
        RootPanel doc = RootPanel.get();
        doc.clear();

        mainContainer = new MainContainer();

        doc.add(mainContainer);
    }

    /**
     * Gets an instance of the UiManager
     * @return
     */
    public static UiManager getInstance() {
        if(instance == null) {
            instance = new UiManager();
        }
        return instance;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void adminMode() {
        mainContainer.adminMode();
    }

    public void studentMode() {
        mainContainer.studentMode();
    }

    /**
     * Displays a screen based on the passed in enum
     *
     * @param screen Screen enum
     */
    public void displayScreen(ScreenEnum screen) {
        if(ScreenEnum.HOME.equals(screen)) {
            displayHomeScreen();
        } else if(ScreenEnum.CREATE_UNIVERSITY.equals(screen)) {
            displayCreateUniversityScreen();
        } else if(ScreenEnum.INBOX.equals(screen)) {
            displayInboxScreen();
        } else if(ScreenEnum.EVENTS.equals(screen)) {
            displayEventsScreen();
        } else if(ScreenEnum.RSO.equals(screen)) {
            displayRSOScreen();
        }
    }

    /**
     * Displays the home screen
     */
    private void displayHomeScreen() {
        mainContainer.setDisplayedScreen(new HomeScreen());
    }

    /**
     * Displays the create university screen
     */
    private void displayCreateUniversityScreen() {
        mainContainer.setDisplayedScreen(new CreateUniversity());
    }

    /**
     * Displays the inbox screen
     */
    private void displayInboxScreen() {
        mainContainer.setDisplayedScreen(new Inbox());
    }

    private void displayEventsScreen() {
        mainContainer.setDisplayedScreen(new EventsScreen());
    }

    private void displayRSOScreen() {
        mainContainer.setDisplayedScreen(new RSOScreen());
    }
}
